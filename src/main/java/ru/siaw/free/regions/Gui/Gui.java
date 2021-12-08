package ru.siaw.free.regions.Gui;

import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import ru.siaw.free.regions.Main;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.config.Message;

import java.util.HashMap;

public class Gui
{
    @Getter private static final HashMap<Player, SmartInventory> inventories = new HashMap<>();

    public static void flags(Player p, Region rg) {
        FlagsProvider.getRegions().put(p, rg);
        SmartInventory inventory = SmartInventory.builder()
                .provider(new FlagsProvider())
                .manager(Main.inventoryManager)
                .listener(new InventoryListener<>(InventoryCloseEvent.class, e -> {
                    inventories.remove((Player) e.getPlayer());
                    FlagsProvider.getRegions().remove((Player) e.getPlayer());
                }))
                .size(4, 9)
                .title(Message.inst.getMessage("Guis.ChangeFlag.Title").replace("%region", rg.getName()))
                .build();
        inventories.put(p, inventory);
        inventory.open(p);
    }
}
