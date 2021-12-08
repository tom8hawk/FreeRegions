package ru.siaw.free.regions.utils;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Other
{
    public static ItemStack createItemStack(Material material, String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(lore[0]);

        meta.setLore(Arrays.asList(lore).subList(1, lore.length));
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack createItemStackWithList(Material material, String name, List<String> lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public static String playersToString(List<OfflinePlayer> list) {
        StringBuilder builder = new StringBuilder(list.isEmpty() ? "Нет" : list.get(0).getName());
        if (list.size() > 1) {
            list.remove(0);
            list.forEach(p -> builder.append(", ").append(p.getName()));
        }
        return builder.toString();
    }
}