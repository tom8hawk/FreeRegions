package ru.siaw.free.regions;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.utils.PlayerUtil;
import ru.siaw.free.regions.utils.Print;
import ru.siaw.free.regions.utils.config.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Region
{
    @Getter private static final List<Region> regions = new LinkedList<>(); // Все регионы
    @Getter private String name;
    @Getter private final Location location1, location2;
    @Getter private final ArrayList<Location> blocks = new ArrayList<>();
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

    public static Region getByName(String name) { // Для комманд с названием региона
        for (Region region : regions)
            if (region.getName().equalsIgnoreCase(name))
                return region;
        return null;
    }

    public static Region getByLocation(Location location) {
        for (Region region : regions)
            for (Location loc : region.getBlocks())
                if (location.equals(loc))
                    return region;
        return null;
    }

    public boolean isInRegion(Player player) {
        return owners.contains(player) || members.contains(player);
    }

    private Thread countThread;
    private void countBlocks(boolean add) {
        countThread = new Thread(() -> {
            synchronized (blocks) {
                int topBlockX = (Math.max(location1.getBlockX(), location2.getBlockX()));
                int bottomBlockX = (Math.min(location1.getBlockX(), location2.getBlockX()));

                int topBlockY = (Math.max(location1.getBlockY(), location2.getBlockY()));
                int bottomBlockY = (Math.min(location1.getBlockY(), location2.getBlockY()));

                int topBlockZ = (Math.max(location1.getBlockZ(), location2.getBlockZ()));
                int bottomBlockZ = (Math.min(location1.getBlockZ(), location2.getBlockZ()));

                for (int x = bottomBlockX; x <= topBlockX; x++)
                    for (int z = bottomBlockZ; z <= topBlockZ; z++)
                        for (int y = bottomBlockY; y <= topBlockY; y++)
                            blocks.add(new Location(location1.getWorld(), x, y, z));
                if (add)
                    regions.add(this);
            }
        });
        countThread.start();
    }

    private void validate(String name) {
        new Thread(() -> {
            synchronized (regions) {
                countBlocks(false);
                Player creator = (Player) this.creator;
                PlayerUtil util = new PlayerUtil(creator);

                try {
                    countThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int limitOfBlocks = util.getLimitOfBlocks();
                if (blocks.size() > limitOfBlocks) {
                    creator.sendMessage(Message.inst.getMessage("Create.BlocksLimit").replace("%limit", String.valueOf(limitOfBlocks)));
                    return;
                }

                int regionsCount = 0;
                for (Region rg : regions) {
                    if (rg.creator.equals(creator))
                        regionsCount++;

                    if (rg.name.equalsIgnoreCase(name)) {
                        Print.toPlayer(creator, Message.inst.getMessage("Create.Exists"));
                        return;
                    }

                    if (blocks.stream().anyMatch(rg.blocks::contains)) {
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
                Print.toPlayer(creator, Message.inst.getMessage("Create.Successfully").replace("%region", name).replace("%size", String.valueOf(blocks.size())));
            }
        }).start();
    }

    public void remove() {
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
