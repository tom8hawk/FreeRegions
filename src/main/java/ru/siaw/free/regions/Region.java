package ru.siaw.free.regions;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.utils.PlayerUtil;
import ru.siaw.free.regions.utils.Print;
import ru.siaw.free.regions.utils.config.DataBase;
import ru.siaw.free.regions.utils.config.Message;

import java.util.ArrayList;
import java.util.List;

public class Region
{
    @Getter private static final List<Region> regions = new ArrayList<>(); // Все регионы
    @Getter private String name;
    @Getter private final Location location1, location2;
    @Getter private int numOfBlocks = 0;
    @Getter private final OfflinePlayer creator;
    @Getter private List<OfflinePlayer> owners = new ArrayList<>(), members = new ArrayList<>();
    @Getter @Setter private boolean pvp, mobSpawning, mobDamage, use, piston, build, invincible, leavesFalling, explosion, itemDrop, entry;

    public Region(String name, Location location1, Location location2, OfflinePlayer creator, List<OfflinePlayer> owners, List<OfflinePlayer> members, boolean pvp, boolean mobSpawning, boolean mobDamage,
                  boolean use, boolean piston, boolean build, boolean invincible, boolean leavesFalling, boolean explosion, boolean itemDrop, boolean entry) {
        this.name = name;
        this.location1 = location1;
        this.location2 = location2;
        this.creator = creator;
        this.owners = owners;
        this.members = members;

        this.pvp = pvp;
        this.mobSpawning = mobSpawning;
        this.mobDamage = mobDamage;
        this.use = use;
        this.piston = piston;
        this.build = build;
        this.invincible = invincible;
        this.leavesFalling = leavesFalling;
        this.explosion = explosion;
        this.itemDrop = itemDrop;
        this.entry = entry;

        countBlocks(true);
    }

    public Region(String name, Location location1, Location location2, Player creator, boolean pvp, boolean mobSpawning, boolean mobDamage,
                  boolean use, boolean piston, boolean build, boolean invincible, boolean leavesFalling, boolean explosion, boolean itemDrop, boolean entry) {
        this.location1 = location1;
        this.location2 = location2;
        this.creator = creator;
        owners.add(creator);

        this.pvp = pvp;
        this.mobSpawning = mobSpawning;
        this.mobDamage = mobDamage;
        this.use = use;
        this.piston = piston;
        this.build = build;
        this.invincible = invincible;
        this.leavesFalling = leavesFalling;
        this.explosion = explosion;
        this.itemDrop = itemDrop;
        this.entry = entry;

        validate(name);
    }

    public static Region getByName(String name) {
        for (Region region : regions)
            if (region.getName().equalsIgnoreCase(name))
                return region;
        return null;
    }

    public static Region getByLocation(Location location) {
        for (Region region : regions)
            if (region.isLocInRegion(location))
                return region;
        return null;
    }

    public boolean isPlayerInRegion(Player player) {
        return owners.contains(player) || members.contains(player);
    }

    public boolean isLocInRegion(Location loc) {
        int maxX = (Math.max(location1.getBlockX(), location2.getBlockX()));
        int minX = (Math.min(location1.getBlockX(), location2.getBlockX()));

        int maxY = (Math.max(location1.getBlockY(), location2.getBlockY()));
        int minY = (Math.min(location1.getBlockY(), location2.getBlockY()));

        int maxZ = (Math.max(location1.getBlockZ(), location2.getBlockZ()));
        int minZ = (Math.min(location1.getBlockZ(), location2.getBlockZ()));

        return loc.getX() >= minX && loc.getX() <= maxX && loc.getY() >= minY && loc.getY() <= maxY && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }

    public Thread countBlocks;
    private void countBlocks(Boolean add) {
        countBlocks = new Thread(() -> {
            synchronized (add) {
                int minX = (Math.min(location1.getBlockX(), location2.getBlockX()));
                int maxX = (Math.max(location1.getBlockX(), location2.getBlockX()));

                int minY = (Math.min(location1.getBlockY(), location2.getBlockY()));
                int maxY = (Math.max(location1.getBlockY(), location2.getBlockY()));

                int minZ = (Math.min(location1.getBlockZ(), location2.getBlockZ()));
                int maxZ = (Math.max(location1.getBlockZ(), location2.getBlockZ()));

                for (int x = minX; x <= maxX; x++)
                    for (int z = minZ; z <= maxZ; z++)
                        for (int y = minY; y <= maxY; y++)
                            numOfBlocks++;
            }
        });
        countBlocks.start();
        if (add) regions.add(this);
    }

    private void validate(String name) {
        new Thread(() -> {
            synchronized (regions) {
                Player creator = (Player) this.creator;

                if (!location1.getWorld().equals(location2.getWorld())) {
                    Print.toPlayer(creator, Message.inst.getMessage("Positions.DifferentWorlds"));
                }

                PlayerUtil util = new PlayerUtil(creator);
                int limitOfBlocks = util.getLimitOfBlocks();

                countBlocks(false);
                try {
                    countBlocks.join();
                    if (numOfBlocks > limitOfBlocks) {
                        Print.toPlayer(creator, Message.inst.getMessage("Create.BlocksLimit"));
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int regionsCount = 0;
                for (Region rg : regions) {
                    if (rg.creator.equals(creator))
                        regionsCount++;

                    if (rg.name.equalsIgnoreCase(name)) {
                        Print.toPlayer(creator, Message.inst.getMessage("Create.Exists"));
                        return;
                    }

                    if (rg.isLocInRegion(location1) || rg.isLocInRegion(location2)) {
                        Print.toPlayer(creator, Message.inst.getMessage("Create.OtherRegions").replace("%other", rg.getName()));
                        return;
                    }
                }

                if (regionsCount > util.getLimitOfRegions()) {
                    Print.toPlayer(creator, Message.inst.getMessage("Create.RegionCountLimit"));
                    return;
                }

                this.name = name;
                regions.add(this);
                Print.toPlayer(creator, Message.inst.getMessage("Create.Successfully").replace("%region", name).replace("%size", String.valueOf(numOfBlocks)));
            }
        }).start();
    }

    public void remove() {
        DataBase.inst.cleanRegionData(name);
        regions.remove(this);
    }

    // Добавление в списки

    public void addOwner(OfflinePlayer owner) {
        owners.add(owner);
    }

    public void removeOwner(OfflinePlayer owner) {
        owners.remove(owner);
    }

    public void addMember(OfflinePlayer member) {
        members.add(member);
    }

    public void removeMember(OfflinePlayer member) {
        members.remove(member);
    }
}
