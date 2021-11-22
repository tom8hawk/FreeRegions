package ru.siaw.free.regions.utils;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Other
{
    public static ItemStack createItemStack(Material material, String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(lore[0]);

        List<String> loreList = new ArrayList<>(Arrays.asList(lore).subList(1, lore.length));

        meta.setLore(loreList);

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
