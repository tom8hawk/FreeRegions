package ru.siaw.free.regions.Gui;

import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.Main;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.config.Message;

public class Gui
{
    public static void flags(Player p, Region rg) {
        Flags.getRegions().put(p, rg);
        SmartInventory inventory = SmartInventory.builder()
                .provider(new Flags())
                .manager(Main.inventoryManager)
                .size(4, 9)
                .title(Message.inst.getMessage("Guis.ChangeFlag.Title").replace("%region", rg.getName()))
                .build();
        inventory.open(p);
    }

    public static void allRegions(Player p) {
        AllRegions.getRegions().put(p, Region.getRegions());
        SmartInventory inventory = SmartInventory.builder()
                .provider(new AllRegions())
                .manager(Main.inventoryManager)
                .title(Message.inst.getMessage("Guis.AllRegions.Title"))
                .build();
        inventory.open(p);
    }
}