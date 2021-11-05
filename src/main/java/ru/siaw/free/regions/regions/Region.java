package ru.siaw.free.regions.regions;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.regions.utils.config.DataBase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Region
{
    private static final HashSet<Region> regions = new HashSet<>(); // Все регионы

    private final List<Player> onlinePlayers = new ArrayList<>(); // Игроки онлайн с региона

    private final String name;
    private final Location location1, location2;
    private List<Player> owners = new ArrayList<>(), members = new ArrayList<>();
    private boolean pvp, mobSpawning, mobDamage, use, build, invincible, leavesFalling, explosion, itemDrop, entry;

    public Region(String name, Location location1, Location location2, List<Player> owners, List<Player> members, boolean pvp, boolean mobSpawning, boolean mobDamage,
                  boolean use, boolean build, boolean invincible, boolean leavesFalling, boolean explosion, boolean itemDrop, boolean entry) {
        this.name = format(name);
        this.location1 = location1;
        this.location2 = location2;
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
    }

    public Region(String name, Location location1, Location location2, Player creator, boolean pvp, boolean mobSpawning, boolean mobDamage,
                  boolean use, boolean build, boolean invincible, boolean leavesFalling, boolean explosion, boolean itemDrop, boolean entry) {
        this.name = format(name);
        this.location1 = location1;
        this.location2 = location2;
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

        regions.add(this);
    }

    public static Region getByName(String name) {
        String inFormat = format(name);
        Region result = null;

        for (Region region : regions) {
            if (region.name.equals(inFormat))
                result = region;
        }
        return result;
    }

    private static String format(String string) {
        String lowerCase = string.toLowerCase();
        String at0 = String.valueOf(lowerCase.charAt(0));
        return lowerCase.replace(at0, at0.toUpperCase()) ;
    }

    private static final Boolean addSync = true; // Оп оп потокобезопасность
    public static void addOnline(Player p) {
        List<Boolean> find = new ArrayList<>();
        new Thread(() -> {
            synchronized(addSync) {
                regions.forEach(rg -> {
                    if (rg.members.contains(p) || rg.owners.contains(p)) { // Где-то есть наш игрок?
                        rg.onlinePlayers.add(p);
                        find.add(true);
                    }
                });
                if (find.isEmpty()) // Нигде нету?
                    DataBase.inst.readRegion(p); // Читаем приват, связанный с этим игроком
            }
        }).start();
    }

    private static final Boolean removeSync = true;
    public static void removeOnline(Player p) { // На выходе игрока с сервера
        new Thread(() -> {
            synchronized (removeSync) {
                regions.forEach(rg -> {
                    if (rg.onlinePlayers.contains(p)) { // В каком привате есть наш игрок?
                        rg.onlinePlayers.remove(p);

                        if (rg.onlinePlayers.isEmpty()) { // Остались ли игроки онлайн в этом привате?
                            DataBase.inst.writeRegion(rg);
                            regions.remove(rg);
                        }
                    }
                });
            }
        }).start();
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

    public static HashSet<Region> getRegions() {
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

    public List<Player> getOnlinePlayers() {
        return onlinePlayers;
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
