package ru.siaw.free.regions.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import ru.siaw.free.regions.Main;
import ru.siaw.free.regions.Selection;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener
{
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Selection.getSelections().remove(e.getPlayer());
    }

    private static final List<Player> timer = new ArrayList<>();
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent e) {
        Main.executor.execute(() -> {
            Player p = e.getPlayer();
            if (p == null || timer.contains(p)) return;
            PlayerInventory inventory = p.getInventory();
            if (inventory == null) return;
            ItemStack itemStack = inventory.getItemInMainHand();
            if (itemStack == null) return;
            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) return;
            String displayName = meta.getDisplayName();
            if (displayName == null) return;

            if (itemStack.getType() == Material.ARROW && displayName.equals("§eВыделитель региона")) {
                Action action = e.getAction();
                if (action == null) return;

                Selection selection = Selection.get(p);

                Block block = e.getClickedBlock();
                if (block == null || block.getType() == Material.AIR) return;

                Location location = block.getLocation();
                if (location == null) return;

                if (action == Action.LEFT_CLICK_BLOCK)
                    selection.setPos1(location, p);
                else if (action == Action.RIGHT_CLICK_BLOCK)
                    selection.setPos2(location, p);

                timer.add(p);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.inst, () -> timer.remove(p), 5L);
            }
        });
    }
}
