package ru.siaw.free.regions.Gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.config.Message;
import ru.siaw.free.regions.utils.Other;

import java.util.HashMap;
import java.util.Random;

public class FlagsProvider implements InventoryProvider
{
    @Getter private static final HashMap<Player, Region> regions = new HashMap<>();

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        Region region = regions.get(player);

        inventoryContents.set(1,1, ClickableItem.of(Other.createItemStackWithList(Material.DIAMOND_SWORD,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "PVP"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isPvp() ? "Remove" : "Add"))),
                e -> { region.setPvp(!region.isPvp()); init(player, inventoryContents); }
        ));
        inventoryContents.set(1,2, ClickableItem.of(Other.createItemStackWithList(Material.MOB_SPAWNER,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Mob Spawning"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isMobSpawning() ? "Remove" : "Add"))),
                e -> { region.setMobSpawning(!region.isMobSpawning()); init(player, inventoryContents); }
        ));
    }

    private boolean first = true;
    private byte ticks = 0;
    @Override
    public void update(Player player, InventoryContents inventoryContents) {
        if (ticks == 20 || first) {
            if (!Region.getRegions().contains(regions.get(player))) {
                Gui.getInventories().get(player).close(player);
                Gui.getInventories().remove(player);
                regions.remove(player);
                return;
            }
            init(player, inventoryContents);
            fill(inventoryContents);
            first = false;
            ticks = 0;
        }
        else ticks++;
    }

    private byte previousRandom;
    private void fill(InventoryContents ic) {
        byte random = (byte) (new Random().nextInt(13) + 1);
        if (random == previousRandom) fill(ic);
        ClickableItem ci = ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE, 1, random));
        ic.fillBorders(ci);
        ic.set(3,2,ci);
        ic.set(3,8,ci);
        previousRandom = random;
    }
}
