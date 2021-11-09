package ru.siaw.free.regions.regions.utils.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.regions.Region;
import ru.siaw.free.regions.regions.utils.Print;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataBase extends YAML
{
    public static DataBase inst;

    public DataBase() {
        Initialize("data.yml");
        inst = this;
    }

    public boolean getBoolean(String path) {
        return configuration.getBoolean(mainKey + path);
    }

    public List<String> getList(String path) {
        return configuration.getStringList(mainKey + path);
    }

    public void readRegion(Player p) {
        new Thread(() -> {
            synchronized (configuration) {
                configuration.getKeys(true).forEach(key -> {
                    String[] splits = key.split("//.");
                    if (splits.length > 2) {
                        String regionName = splits[1];
                        List<String> owners = getList(regionName + ".owners");
                        List<String> members = getList(regionName + ".members");

                        String playerUUID = p.getUniqueId().toString();
                        if (owners.contains(playerUUID) || members.contains(playerUUID)) {
                            List<String> location1 = getList(regionName + ".location1");
                            List<String> location2 = getList(regionName + ".location2");
                            World world = Bukkit.getWorld(location1.get(0));

                            List<Double> loc1 = new ArrayList<>();
                            for (String s : location1.get(1).split(";")) {
                                loc1.add(Double.parseDouble(s));
                            }
                            List<Double> loc2 = new ArrayList<>();
                            for (String s : location2.get(0).split(";")) {
                                loc2.add(Double.parseDouble(s));
                            }

                            List<Player> ownersPlayer = new ArrayList<>();
                            owners.forEach(pUuid -> ownersPlayer.add(Bukkit.getPlayer(UUID.fromString(pUuid))));

                            List<Player> membersPlayer = new ArrayList<>();
                            members.forEach(pUuid -> membersPlayer.add(Bukkit.getPlayer(UUID.fromString(pUuid))));

                            if (world != null && loc1.size() == 3 && loc2.size() == 3) {
                                new Region(regionName, new Location(world, loc1.get(0),  loc1.get(1), loc1.get(2)), new Location(world, loc2.get(0), loc2.get(1), loc1.get(2)),
                                        ownersPlayer, membersPlayer, getBoolean(regionName + ".pvp"), getBoolean(regionName + ".mob-spawning"),
                                        getBoolean(regionName + ".mob-damage"), getBoolean(regionName + ".use"), getBoolean(regionName + ".build"),
                                        getBoolean(regionName + ".invincible"), getBoolean(regionName + ".leaves-falling"), getBoolean(regionName + ".explosion"),
                                        getBoolean(regionName + ".item-drop"), getBoolean(regionName + ".entry"));
                            }
                        }
                    }
                });
            }
        }).start();
    }

    public void writeRegion(Region region) {
        new Thread(() -> {
            synchronized (configuration) {
                String key = mainKey + region.getName();
                configuration.set(key, null);
                key += ".";

                Location location1 = region.getLocation1();
                List<String> loc1 = new ArrayList<>();
                loc1.add(location1.getWorld().getName());
                loc1.add(location1.getBlockX() + ";" + location1.getBlockY() + ";" + location1.getBlockZ());
                configuration.set(key + "location1", loc1);

                Location location2 = region.getLocation2();
                List<String> loc2 = new ArrayList<>();
                loc2.add(location2.getBlockX() + ";" + location2.getBlockY() + ";" + location2.getBlockZ());
                configuration.set(key + "location2", loc2);

                List<String> ownersUUIDs = new ArrayList<>();
                region.getOwners().forEach(p -> ownersUUIDs.add(p.getUniqueId().toString()));
                configuration.set(key + "owners", ownersUUIDs);

                List<String> membersUUIDs = new ArrayList<>();
                region.getMembers().forEach(p -> membersUUIDs.add(p.getUniqueId().toString()));
                configuration.set(key + "members", membersUUIDs);

                configuration.set(key + "pvp", region.isPvp());
                configuration.set(key + "mob-spawning", region.isMobSpawning());
                configuration.set(key + "mob-damage", region.isMobDamage());
                configuration.set(key + "use", region.isUse());
                configuration.set(key + "build", region.isBuild());
                configuration.set(key + "invincible", region.isInvincible());
                configuration.set(key + "leaves-falling", region.isLeavesFalling());
                configuration.set(key + "explosion", region.isExplosion());
                configuration.set(key + "item-drop", region.isItemDrop());
                configuration.set(key + "entry", region.isEntry());

                try {
                    configuration.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void load() {
        try {
            configuration.load(file);
            Print.toConsole("Список приватов заружен!");
        } catch (IOException |org.bukkit.configuration.InvalidConfigurationException e) {
            Print.toConsole("Исключение при загрузке списка приватов! " + e.getMessage());
        }
        Bukkit.getOnlinePlayers().forEach(Region::addOnline);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> Region.getRegions().forEach(this::writeRegion), 300L, 300L);
    }
}
