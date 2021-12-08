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
import ru.siaw.free.regions.utils.Print;

import java.util.HashMap;
import java.util.Random;

public class FlagsProvider implements InventoryProvider
{
    @Getter private static final HashMap<Player, Region> regions = new HashMap<>();

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        Region region = regions.get(player);

        inventoryContents.set(0,0, ClickableItem.of(Other.createItemStackWithList(Material.DIAMOND_SWORD,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "PVP"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isPvp() ? "Remove" : "Add"))),
                e -> { region.setPvp(!region.isPvp()); init(player, inventoryContents); }
        ));
        inventoryContents.set(0,1, ClickableItem.of(Other.createItemStackWithList(Material.MOB_SPAWNER,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Mob Spawning"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isMobSpawning() ? "Remove" : "Add"))),
                e -> { region.setMobSpawning(!region.isMobSpawning()); init(player, inventoryContents); }
        ));
        inventoryContents.set(0,2, ClickableItem.of(Other.createItemStackWithList(Material.PORK,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Mob Damage"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isMobDamage() ? "Remove" : "Add"))),
                e -> { region.setMobDamage(!region.isMobDamage()); init(player, inventoryContents); }
        ));
        inventoryContents.set(0,3, ClickableItem.of(Other.createItemStackWithList(Material.LEVER,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Use"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isUse() ? "Remove" : "Add"))),
                e -> { region.setUse(!region.isUse()); init(player, inventoryContents); }
        ));
        inventoryContents.set(0,4, ClickableItem.of(Other.createItemStackWithList(Material.PISTON_STICKY_BASE,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Piston"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isPiston() ? "Remove" : "Add"))),
                e -> { region.setPiston(!region.isPiston()); init(player, inventoryContents); }
        ));
        inventoryContents.set(0,5, ClickableItem.of(Other.createItemStackWithList(Material.WOOD,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Build"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isBuild() ? "Remove" : "Add"))),
                e -> { region.setBuild(!region.isBuild()); init(player, inventoryContents); }
        ));
        inventoryContents.set(0,6, ClickableItem.of(Other.createItemStackWithList(Material.FLINT_AND_STEEL,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Fire"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isFire() ? "Remove" : "Add"))),
                e -> { region.setFire(!region.isFire()); init(player, inventoryContents); }
        ));
        inventoryContents.set(0,7, ClickableItem.of(Other.createItemStackWithList(Material.TOTEM,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Invincible"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isInvincible() ? "Remove" : "Add"))),
                e -> {
                    if (!player.hasPermission("freerg.invincible") && !player.isOp()) {
                        Gui.getInventories().get(player).close(player);
                        Print.toPlayer(player, Message.inst.getMessage("NoPermissions"));
                        return;
                    }
                    region.setInvincible(!region.isInvincible()); init(player, inventoryContents);
                }
        ));
        inventoryContents.set(0,8, ClickableItem.of(Other.createItemStackWithList(Material.LEAVES,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Leaves Falling"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isLeavesFalling() ? "Remove" : "Add"))),
                e -> { region.setLeavesFalling(!region.isLeavesFalling()); init(player, inventoryContents); }
        ));
        inventoryContents.set(1,3, ClickableItem.of(Other.createItemStackWithList(Material.TNT,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Explosion"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isExplosion() ? "Remove" : "Add"))),
                e -> { region.setExplosion(!region.isExplosion()); init(player, inventoryContents); }
        ));
        inventoryContents.set(1,4, ClickableItem.of(Other.createItemStackWithList(Material.SEEDS,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Item Drop"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isItemDrop() ? "Remove" : "Add"))),
                e -> { region.setItemDrop(!region.isItemDrop()); init(player, inventoryContents); }
        ));
        inventoryContents.set(1,5, ClickableItem.of(Other.createItemStackWithList(Material.LEATHER_BOOTS,
                        Message.inst.getMessage("Guis.ChangeFlag.DisplayName").replace("%flag", "Entry"),
                        Message.inst.getList("Guis.ChangeFlag." + (region.isEntry() ? "Remove" : "Add"))),
                e -> { region.setEntry(!region.isEntry()); init(player, inventoryContents); }
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

        ic.fillBorders(ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE, 1, random)));

        previousRandom = random;
    }
}
