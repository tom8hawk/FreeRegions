package ru.siaw.free.regions.Gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.config.Message;
import ru.siaw.free.regions.utils.Other;

import java.util.ArrayList;

public class AllRegions implements InventoryProvider
{
    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[Region.getRegions().size()];
        for(int i = 0; i < items.length; i++) {
            Region region = Region.getRegions().get(i);

            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();

            meta.setOwningPlayer(region.getCreator());
            meta.setDisplayName(Message.inst.getMessage("Guis.AllRegions.DisplayName").replace("%region", region.getName()));
            ArrayList<String> lore = new ArrayList<>();
            lore.add(Message.inst.getMessage("Guis.AllRegions.Status." + (region.isPlayerInRegion(region.getCreator().getPlayer()) ? "One" : "Two")));
            Message.inst.getList("Guis.AllRegions.Lores").forEach(line -> lore.add(Other.replaceRegionInfo(line, region)));

            meta.setLore(lore);
            skull.setItemMeta(meta);

            items[i] = ClickableItem.of(skull,
                    e -> {
                        contents.inventory().close(player);
                        player.chat("/rg flag " + region.getName());
                    });
        }
        pagination.setItems(items);
        pagination.setItemsPerPage(45);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

        contents.set(5, 3, ClickableItem.of(Other.createItemStack(Material.ARROW, String.format("На предыдущую страницу - %d", pagination.previous().getPage())),
                e -> contents.inventory().open(player, pagination.previous().getPage())));
        contents.set(5, 5, ClickableItem.of(Other.createItemStack(Material.ARROW, String.format("На следующую страницу - %d", pagination.previous().getPage())),
                e -> contents.inventory().open(player, pagination.next().getPage())));
    }

    private boolean first = true;
    private short ticks = 0;
    @Override
    public void update(Player player, InventoryContents inventoryContents) {
        if (ticks == 1200 || first) {
            init(player, inventoryContents);
            first = false;
            ticks = 0;
        }
        else ticks++;
    }
}
