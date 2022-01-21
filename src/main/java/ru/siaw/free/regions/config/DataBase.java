package ru.siaw.free.regions.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import ru.siaw.free.regions.Main;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.utils.Print;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class DataBase extends YAML
{
    public static DataBase inst;
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Timer task = new Timer();

    public DataBase() {
        Initialize("data.yml");
        inst = this;
    }

    public void writeRegion(Region region) {
        if (region != null) {
            String key = region.getName() + ".";

            configuration.set(key + "world", region.getLocation1().getWorld().getName());

            List<Double> loc1 = new ArrayList<>();
            loc1.add(region.getLocation1().getX());
            loc1.add(region.getLocation1().getY());
            loc1.add(region.getLocation1().getZ());
            configuration.set(key + "location1", loc1);

            List<Double> loc2 = new ArrayList<>();
            loc2.add(region.getLocation2().getX());
            loc2.add(region.getLocation2().getY());
            loc2.add(region.getLocation2().getZ());
            configuration.set(key + "location2", loc2);

            configuration.set(key + "creator", region.getCreator().getUniqueId().toString());
            configuration.set(key + "owners", region.getOwners().parallelStream().map(OfflinePlayer::getUniqueId).map(String::valueOf).collect(Collectors.toList()));
            configuration.set(key + "members", region.getMembers().parallelStream().map(OfflinePlayer::getUniqueId).map(String::valueOf).collect(Collectors.toList()));

            configuration.set(key + "blocks", region.getNumOfBlocks());

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
        Main.executor.execute(() -> {
            String key = name + ".";

            configuration.set(key + "world", null);
            configuration.set(key + "location1", null);
            configuration.set(key + "location2", null);
            configuration.set(key + "creator", null);
            configuration.set(key + "owners", null);
            configuration.set(key + "members", null);
            configuration.set(key + "blocks", null);
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
        });
    }

    public void load() {
        Main.executor.execute(() -> {
            try {
                configuration.load(file);
                Print.toConsole("База данных загружена!");
            } catch (IOException |org.bukkit.configuration.InvalidConfigurationException e) {
                e.printStackTrace();
            }

            Print.toConsole("Чтение регионов...");
            List<Region> regions = new ArrayList<>();

            configuration.getKeys(false).parallelStream().forEach(key -> {
                List<OfflinePlayer> owners = getList(key + ".owners").parallelStream().map(uuid -> Bukkit.getOfflinePlayer(UUID.fromString(uuid))).collect(Collectors.toList());
                List<OfflinePlayer> members = getList(key + ".members").parallelStream().map(uuid -> Bukkit.getOfflinePlayer(UUID.fromString(uuid))).collect(Collectors.toList());

                World world = Bukkit.getWorld(configuration.getString(key + ".world"));
                List<Double> loc1 = configuration.getDoubleList(key + ".location1");
                List<Double> loc2 = configuration.getDoubleList(key + ".location2");
                
                regions.add(new Region(key, new Location(world, loc1.get(0),  loc1.get(1), loc1.get(2)), new Location(world, loc2.get(0), loc2.get(1), loc2.get(2)),
                        Bukkit.getOfflinePlayer(UUID.fromString(configuration.getString(key + ".creator"))), owners, members, getInt(key + ".blocks"),
                        getBoolean(key + ".pvp"), getBoolean(key + ".mob-spawning"), getBoolean(key + ".mob-damage"),
                        getBoolean(key + ".use"), getBoolean(key + ".piston"), getBoolean(key + ".build"), getBoolean(key + ".fire"),
                        getBoolean(key + ".invincible"), getBoolean(key + ".leaves-falling"), getBoolean(key + ".explosion"),
                        getBoolean(key + ".item-drop"), getBoolean(key + ".entry")));
            });

            while (!Region.getRegions().containsAll(regions));
            Print.toConsole(String.format("Загружено регионов: %d", Region.getRegions().size()));

            task.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Region.getRegions().forEach(DataBase.inst::writeRegion);
                }
            }, 300000L, 300000L);
        });
    }

    private List<String> getList(String path) {
        return configuration.getStringList(path);
    }

    private boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    private int getInt(String path) {
        return configuration.getInt(path);
    }
}