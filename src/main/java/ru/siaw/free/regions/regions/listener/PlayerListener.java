package ru.siaw.free.regions.regions.listener;

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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import ru.siaw.free.regions.regions.Main;
import ru.siaw.free.regions.regions.Region;
import ru.siaw.free.regions.regions.utils.Selection;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener
{
    protected final Main plugin = Main.inst;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        Region.addOnline(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Region.removeOnline(player);
        Selection.remove(player);
    }

    List<Player> timer = new ArrayList<>();
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent e) {
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
            if (selection == null) selection = new Selection(p);
            Block block = e.getClickedBlock();
            if (block == null || block.getType() == Material.AIR) return;
            Location location = block.getLocation();
            if (location == null) return;

            if (action == Action.LEFT_CLICK_BLOCK)
                selection.setPos1(location, p);
            else if (action == Action.RIGHT_CLICK_BLOCK)
                selection.setPos2(location, p);

            timer.add(p);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> timer.remove(p), 2L);
        }
    }
}
