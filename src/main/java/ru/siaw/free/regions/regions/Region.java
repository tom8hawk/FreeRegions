package ru.siaw.free.regions.regions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.regions.utils.Other;
import ru.siaw.free.regions.regions.utils.PlayerUtil;
import ru.siaw.free.regions.regions.utils.Print;
import ru.siaw.free.regions.regions.utils.config.Message;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Region
{
    private static final List<Region> regions = new LinkedList<>(); // Все регионы

    private String name;
    private final Location location1, location2;
    private final ArrayList<Location> blocks = new ArrayList<>();
    private final Player creator;
    private List<Player> owners = new ArrayList<>(), members = new ArrayList<>();
    private boolean pvp, mobSpawning, mobDamage, use, build, invincible, leavesFalling, explosion, itemDrop, entry;

    public Region(String name, Location location1, Location location2, Player creator, List<Player> owners, List<Player> members, boolean pvp, boolean mobSpawning, boolean mobDamage,
                  boolean use, boolean build, boolean invincible, boolean leavesFalling, boolean explosion, boolean itemDrop, boolean entry) {
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
        this.build = build;
        this.invincible = invincible;
        this.leavesFalling = leavesFalling;
        this.explosion = explosion;
        this.itemDrop = itemDrop;
        this.entry = entry;

        regions.add(this);
        countBlocks();
    }

    public Region(String name, Location location1, Location location2, Player creator, boolean pvp, boolean mobSpawning, boolean mobDamage,
                  boolean use, boolean build, boolean invincible, boolean leavesFalling, boolean explosion, boolean itemDrop, boolean entry) {
        this.location1 = location1;
        this.location2 = location2;
        this.creator = creator;
        owners.add(creator);

        this.pvp = pvp;
        this.mobSpawning = mobSpawning;
        this.mobDamage = mobDamage;
        this.use = use;
        this.build = build;
        this.invincible = invincible;
        this.leavesFalling = leavesFalling;
        this.explosion = explosion;
        this.itemDrop = itemDrop;
        this.entry = entry;

        validate(name);
    }

    public static Region getByName(String name) { // Для комманд с названием региона
        String lowerName = name.toLowerCase();
        Region result = null;

        for (Region region : regions) {
            if (region.getName().toLowerCase().equals(lowerName))
                result = region;
        }
        return result;
    }

    public static List<Region> getByLocation(Location... location) { //todo: Чуть позже
        List<Region> toReturn = new ArrayList<>();
        regions.forEach(region -> region.getBlocks().forEach(block -> {
            for (Location loc : location)
                if (loc.equals(block))
                    toReturn.add(region);
        }));

        return toReturn;
    }
    
    private boolean counted = false;
    private void countBlocks() {
        new Thread(() -> {
            synchronized (blocks) {
                double xMin, yMin, zMin;
                double xMax, yMax, zMax;
                double x, y, z;

                xMin = Math.min(location1.getBlockX(), location2.getBlockX());
                yMin = Math.min(location1.getBlockY(), location2.getBlockY());
                zMin = Math.min(location1.getBlockZ(), location2.getBlockZ());

                xMax = Math.max(location1.getBlockX(), location2.getBlockX());
                yMax = Math.max(location1.getBlockY(), location2.getBlockY());
                zMax = Math.max(location1.getBlockZ(), location2.getBlockZ());

                World w = location1.getWorld();
                for (x = xMin; x <= xMax; x ++)
                    for (y = yMin; y <= yMax; y ++)
                        for (z = zMin; z <= zMax; z ++)
                            blocks.add(new Location(w, x, y, z));
                counted = true;
            }
        }).start();
    }

    private void validate(String name) {
        new Thread(() -> {
            synchronized (regions) {
                if (!counted) {
                    countBlocks();
                    while (!counted) {
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                PlayerUtil util = new PlayerUtil(creator);
                int limitOfBlocks = util.getLimitOfBlocks();

                if (blocks.size() > limitOfBlocks) {
                    creator.sendMessage(Message.inst.getMessage("Create.BlocksLimit").replace("%limit", String.valueOf(limitOfBlocks)));
                    return;
                }

                int regionsCount = 0;
                String lowerName = name.toLowerCase();

                for (Region rg : regions) {
                    if (rg.getCreator().equals(creator))
                        regionsCount++;

                    String rgLowerName = rg.getName().toLowerCase();
                    if (rgLowerName.equals(lowerName)) {
                        Print.toPlayer(creator, Message.inst.getMessage("Create.Exists"));
                        return;
                    }
                    if (blocks.stream().anyMatch(element -> rg.getBlocks().contains(element))) {
                        Print.toPlayer(creator, Message.inst.getMessage("Create.OtherRegions").replace("%other", name));
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

    public void addOwner(Player owner) {
        owners.add(owner);
    }

    public void removeOwner(Player owner) {
        owners.remove(owner);
    }

    public void addMember(Player member) {
        members.add(member);
    }

    public void removeMember(Player member) {
        members.remove(member);
    }

    // Геттеры, сеттеры

    public static List<Region> getRegions() {
        return regions;
    }

    public String getName() {
        return name;
    }

    public Location getLocation1() {
        return location1;
    }

    public Location getLocation2() {
        return location2;
    }

    public ArrayList<Location> getBlocks() {
        return blocks;
    }

    public Player getCreator() {
        return creator;
    }

    public List<Player> getOwners() {
        return owners;
    }

    public void setOwners(List<Player> owners) {
        this.owners = owners;
    }

    public List<Player> getMembers() {
        return members;
    }

    public void setMembers(List<Player> members) {
        this.members = members;
    }

    public boolean isPvp() {
        return pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isMobSpawning() {
        return mobSpawning;
    }

    public void setMobSpawning(boolean mobSpawning) {
        this.mobSpawning = mobSpawning;
    }

    public boolean isMobDamage() {
        return mobDamage;
    }

    public void setMobDamage(boolean mobDamage) {
        this.mobDamage = mobDamage;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public boolean isBuild() {
        return build;
    }

    public void setBuild(boolean build) {
        this.build = build;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public boolean isLeavesFalling() {
        return leavesFalling;
    }

    public void setLeavesFalling(boolean leavesFalling) {
        this.leavesFalling = leavesFalling;
    }

    public boolean isExplosion() {
        return explosion;
    }

    public void setExplosion(boolean explosion) {
        this.explosion = explosion;
    }

    public boolean isItemDrop() {
        return itemDrop;
    }

    public void setItemDrop(boolean itemDrop) {
        this.itemDrop = itemDrop;
    }

    public boolean isEntry() {
        return entry;
    }

    public void setEntry(boolean entry) {
        this.entry = entry;
    }
}
