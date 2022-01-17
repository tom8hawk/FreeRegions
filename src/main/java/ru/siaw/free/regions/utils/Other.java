package ru.siaw.free.regions.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.siaw.free.regions.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Other
{
    public static ItemStack createItemStack(Material material, String... lore) {
        List<String> list = Stream.of(lore).collect(Collectors.toList());

        return createItemStackWithList(material, list.get(0), list.size() > 1 ? list.subList(1, list.size()) : new ArrayList<>());
    }

    public static ItemStack createItemStackWithList(Material material, String name, List<String> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        stack.setItemMeta(meta);
        return stack;
    }

    public static String replaceRegionInfo(String line, Region region) {
        Location pos1 = region.getLocation1();
        Location pos2 = region.getLocation2();

        return line.replace("%region", region.getName())
                .replace("%pos1", String.format("%d, %d, %d", (int) pos1.getX(), (int) pos1.getY(), (int) pos1.getZ()))
                .replace("%pos2", String.format("%d, %d, %d", (int) pos2.getX(), (int) pos2.getY(), (int) pos2.getZ()))
                .replace("%flags", String.format("PVP: %b, Mob spawning: %b, Mob damage %b, Use: %b, Build: %b, Invincible: %b, Leaves falling: %b, Explosion: %b, Entry: %b",
                        region.isPvp(), region.isMobSpawning(), region.isMobDamage(), region.isUse(), region.isBuild(), region.isInvincible(), region.isLeavesFalling(),
                        region.isExplosion(), region.isEntry())).replace("%owners", Other.playersToString(region.getOwners()))
                .replace("%members", Other.playersToString(region.getMembers())).replace("%creator", region.getCreator().getName())
                .replace("%size", String.valueOf(region.getNumOfBlocks()));
    }

    private static String playersToString(List<OfflinePlayer> list) {
        return list.isEmpty() ? "Нет" : list.parallelStream().map(OfflinePlayer::getName).collect(Collectors.joining(", "));
    }
}