package ru.siaw.free.regions.regions.utils;

import org.bukkit.Material;
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
}
