package ru.siaw.free.regions.Gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.siaw.free.regions.Main;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.config.Message;
import ru.siaw.free.regions.utils.Other;
import ru.siaw.free.regions.utils.Print;

import java.util.HashMap;
import java.util.Random;

public class Flags implements InventoryProvider
{
    @Getter private static final HashMap<Player, Region> regions = new HashMap<>();

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        Main.executor.execute(() -> {
            Region region = regions.get(player);

            inventoryContents.set(1,1, ClickableItem.of(Other.createItemStackWithList(Material.DIAMOND_SWORD,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "PVP"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isPvp() ? "Add" : "Remove"))),
                    e -> { region.setPvp(!region.isPvp()); init(player, inventoryContents); }
            ));
            inventoryContents.set(1,2, ClickableItem.of(Other.createItemStackWithList(Material.MOB_SPAWNER,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Mob Spawning"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isMobSpawning() ? "Add" : "Remove"))),
                    e -> { region.setMobSpawning(!region.isMobSpawning()); init(player, inventoryContents); }
            ));
            inventoryContents.set(1,3, ClickableItem.of(Other.createItemStackWithList(Material.PORK,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Mob Damage"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isMobDamage() ? "Add" : "Remove"))),
                    e -> { region.setMobDamage(!region.isMobDamage()); init(player, inventoryContents); }
            ));
            inventoryContents.set(1,4, ClickableItem.of(Other.createItemStackWithList(Material.LEVER,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Use"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isUse() ? "Add" : "Remove"))),
                    e -> { region.setUse(!region.isUse()); init(player, inventoryContents); }
            ));
            inventoryContents.set(1,5, ClickableItem.of(Other.createItemStackWithList(Material.PISTON_STICKY_BASE,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Piston"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isPiston() ? "Add" : "Remove"))),
                    e -> { region.setPiston(!region.isPiston()); init(player, inventoryContents); }
            ));
            inventoryContents.set(1,6, ClickableItem.of(Other.createItemStackWithList(Material.WOOD,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Build"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isBuild() ? "Add" : "Remove"))),
                    e -> { region.setBuild(!region.isBuild()); init(player, inventoryContents); }
            ));
            inventoryContents.set(1,7, ClickableItem.of(Other.createItemStackWithList(Material.FLINT_AND_STEEL,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Fire"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isFire() ? "Add" : "Remove"))),
                    e -> { region.setFire(!region.isFire()); init(player, inventoryContents); }
            ));
            inventoryContents.set(2,2, ClickableItem.of(Other.createItemStackWithList(Material.TOTEM,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Invincible"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isInvincible() ? "Add" : "Remove"))),
                    e -> {
                        if (!player.hasPermission("freerg.invincible") && !player.isOp()) {
                            inventoryContents.inventory().close(player);
                            Print.toPlayer(player, Message.inst.getMessage("NoPermissions"));
                            return;
                        }
                        region.setInvincible(!region.isInvincible()); init(player, inventoryContents);
                    }
            ));
            inventoryContents.set(2,3, ClickableItem.of(Other.createItemStackWithList(Material.LEAVES,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Leaves Falling"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isLeavesFalling() ? "Add" : "Remove"))),
                    e -> { region.setLeavesFalling(!region.isLeavesFalling()); init(player, inventoryContents); }
            ));
            inventoryContents.set(2,4, ClickableItem.of(Other.createItemStackWithList(Material.TNT,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Explosion"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isExplosion() ? "Add" : "Remove"))),
                    e -> { region.setExplosion(!region.isExplosion()); init(player, inventoryContents); }
            ));
            inventoryContents.set(2,5, ClickableItem.of(Other.createItemStackWithList(Material.SEEDS,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Item Drop"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isItemDrop() ? "Add" : "Remove"))),
                    e -> { region.setItemDrop(!region.isItemDrop()); init(player, inventoryContents); }
            ));
            inventoryContents.set(2,6, ClickableItem.of(Other.createItemStackWithList(Material.LEATHER_BOOTS,
                            Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Entry"),
                            Message.inst.getList("Guis.ChangeFlag." + (region.isEntry() ? "Add" : "Remove"))),
                    e -> { region.setEntry(!region.isEntry()); init(player, inventoryContents); }
            ));
        });
    }

    private boolean first = true;
    private byte ticks = 0;
    @Override
    public void update(Player player, InventoryContents inventoryContents) {
        Main.executor.execute(() -> {
            if (ticks == 20 || first) {
                if (!Region.getRegions().contains(regions.get(player))) {
                    inventoryContents.inventory().close(player);
                    regions.remove(player);
                    return;
                }
                init(player, inventoryContents);
                fill(inventoryContents);
                first = false;
                ticks = 0;
            }
            else ticks++;
        });
    }

    private byte previousRandom;
    private void fill(InventoryContents ic) {
        Main.executor.execute(() -> {
            byte random = (byte) (new Random().nextInt(13) + 1);
            if (random == previousRandom) fill(ic);

            ic.fillBorders(ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE, 1, random)));

            previousRandom = random;
        });
    }
}
