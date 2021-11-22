package ru.siaw.free.regions.utils.config;

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

    Thread readThread;
    public void readRegions() {
        readThread = new Thread(() -> {
            synchronized (configuration) {
                configuration.getKeys(true).forEach(key -> {
                    String[] splits = key.split("\\.");

                    if (splits.length == 2) {
                        String regionName = splits[1];

                        List<OfflinePlayer> owners = new ArrayList<>();
                        configuration.getStringList(mainKey + regionName + ".owners").forEach(uuid -> owners.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid))));

                        List<OfflinePlayer> members = new ArrayList<>();
                        configuration.getStringList(mainKey + regionName + ".members").forEach(uuid -> members.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid))));

                        World world = Bukkit.getWorld(configuration.getString(mainKey + regionName + ".world"));

                        List<Double> loc1 = configuration.getDoubleList(mainKey + regionName + ".location1");
                        List<Double> loc2 = configuration.getDoubleList(mainKey + regionName + ".location2");

                        new Region(regionName, new Location(world, loc1.get(0),  loc1.get(1), loc1.get(2)), new Location(world, loc2.get(0), loc2.get(1), loc1.get(2)),
                                Bukkit.getOfflinePlayer(UUID.fromString(configuration.getString(mainKey + regionName + ".creator"))), owners, members,
                                getBoolean(regionName + ".pvp"), getBoolean(regionName + ".mob-spawning"), getBoolean(regionName + ".mob-damage"),
                                getBoolean(regionName + ".use"), getBoolean(regionName + ".build"), getBoolean(regionName + ".invincible"),
                                getBoolean(regionName + ".leaves-falling"), getBoolean(regionName + ".explosion"), getBoolean(regionName + ".item-drop"),
                                getBoolean(regionName + ".entry"));
                    }
                });
            }
        });
        readThread.start();
    }

    public void writeRegion(Region region) {
        new Thread(() -> {
            synchronized (configuration) {
                if (region != null) {
                    String key = mainKey + region.getName() + ".";

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
        }).start();
    }

    private boolean getBoolean(String path) {
        return configuration.getBoolean(mainKey + path);
    }

    public static Thread writeThread;
    public void load() {
        try {
            configuration.load(file);
            Print.toConsole("База данных загружена!");
        } catch (IOException |org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            readRegions();
            Print.toConsole("Чтение регионов...");

            try {
                readThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Print.toConsole("Список регионов загружен!");
        }).start();

        if (writeThread == null) {
            writeThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(900000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Region.getRegions().forEach(DataBase.inst::writeRegion);
                }
            });
            writeThread.start();
        }
    }
}