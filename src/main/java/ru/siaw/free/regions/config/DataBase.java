package ru.siaw.free.regions.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.utils.Print;

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

    public void readRegions() {
        new Thread(() -> {
            synchronized (configuration) {
                configuration.getKeys(false).forEach(key -> {
                    List<OfflinePlayer> owners = new ArrayList<>();
                    configuration.getStringList(key + ".owners").forEach(uuid -> owners.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid))));

                    List<OfflinePlayer> members = new ArrayList<>();
                    configuration.getStringList(key + ".members").forEach(uuid -> members.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid))));

                    World world = Bukkit.getWorld(configuration.getString(key + ".world"));

                    List<Double> loc1 = configuration.getDoubleList(key + ".location1");
                    List<Double> loc2 = configuration.getDoubleList(key + ".location2");

                    new Region(key, new Location(world, loc1.get(0),  loc1.get(1), loc1.get(2)), new Location(world, loc2.get(0), loc2.get(1), loc2.get(2)),
                            Bukkit.getOfflinePlayer(UUID.fromString(configuration.getString(key + ".creator"))), owners, members, getBoolean(key + ".pvp"),
                            getBoolean(key + ".mob-spawning"), getBoolean(key + ".mob-damage"), getBoolean(key + ".use"), getBoolean(key + ".piston"),
                            getBoolean(key + ".build"), getBoolean(key + ".fire"), getBoolean(key + ".invincible"), getBoolean(key + ".leaves-falling"),
                            getBoolean(key + ".explosion"), getBoolean(key + ".item-drop"), getBoolean(key + ".entry"));
                });
            }
        }).start();
    }

    public void writeRegion(Region region) {
        if (region != null) {
            String key = region.getName() + ".";

            Location location1 = region.getLocation1();

            configuration.set(key + "world", location1.getWorld().getName());

            List<Double> loc1 = new ArrayList<>();
            loc1.add(location1.getX());
            loc1.add(location1.getY());
            loc1.add(location1.getZ());
            configuration.set(key + "location1", loc1);

            Location location2 = region.getLocation2();

            List<Double> loc2 = new ArrayList<>();
            loc2.add(location2.getX());
            loc2.add(location2.getY());
            loc2.add(location2.getZ());
            configuration.set(key + "location2", loc2);

            configuration.set(key + "creator", region.getCreator().getUniqueId().toString());

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
            configuration.set(key + "piston", region.isPiston());
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
    }

    public void cleanRegionData(String name) {
        configuration.set(name, null);
        String key = name + ".";

        configuration.set(key + "world", null);
        configuration.set(key + "location1", null);
        configuration.set(key + "location2", null);
        configuration.set(key + "creator", null);
        configuration.set(key + "owners", null);
        configuration.set(key + "members", null);
        configuration.set(key + "pvp", null);
        configuration.set(key + "mob-spawning", null);
        configuration.set(key + "mob-damage", null);
        configuration.set(key + "use", null);
        configuration.set(key + "piston", null);
        configuration.set(key + "build", null);
        configuration.set(key + "invincible", null);
        configuration.set(key + "leaves-falling", null);
        configuration.set(key + "explosion", null);
        configuration.set(key + "item-drop", null);
        configuration.set(key + "entry", null);

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    private static Thread scheduledWriteThread;
    public void load() {
        try {
            configuration.load(file);
            Print.toConsole("База данных загружена!");
        } catch (IOException |org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            Print.toConsole("Чтение регионов...");
            readRegions();

            List<String> keys = new ArrayList<>(configuration.getKeys(false));
            while (true) {
                List<Region> regions = new ArrayList<>(Region.getRegions());

                List<String> names = new ArrayList<>();
                regions.forEach(rg -> names.add(rg.getName()));

                if (names.containsAll(keys) && regions.stream().noneMatch(rg -> rg.getCountBlocks().isAlive()))
                    break;
            }

            Print.toConsole(String.format("Загружено регионов: %d", Region.getRegions().size()));
        }).start();

        if (scheduledWriteThread == null) {
            scheduledWriteThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(300000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Region.getRegions().forEach(DataBase.inst::writeRegion);
                }
            });
            scheduledWriteThread.start();
        }
    }
}