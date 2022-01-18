package ru.siaw.free.regions.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import ru.siaw.free.regions.Main;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.config.Message;
import ru.siaw.free.regions.utils.Print;

import java.util.*;

public class DispenseListener implements Listener {
    private static final HashMap<Block, Player> placedDispensers = new HashMap<>();

    public static void scheduling() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            public void run() {
                List<Block> toRemove = new ArrayList<>();

                placedDispensers.entrySet().parallelStream().forEach(entry -> {
                    Block nowBlock = entry.getKey().getWorld().getBlockAt(entry.getKey().getLocation());

                    if (nowBlock.getType() != Material.DISPENSER)
                        toRemove.add(entry.getKey());
                });
                toRemove.forEach(placedDispensers::remove);
            }
        }, 300000L, 300000L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlaceDispenser(BlockPlaceEvent e) {
        Main.executor.execute(() -> {
            if (!e.isCancelled() && (e.getBlock().getType() == Material.DISPENSER)
                    && Region.getByLocation(e.getBlock().getLocation()) == null)
                placedDispensers.put(e.getBlockPlaced(), e.getPlayer());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreakDispenser(BlockBreakEvent e) {
        Main.executor.execute(() -> {
            if (!e.isCancelled() && (e.getBlock().getType() == Material.DISPENSER)
                    && Region.getByLocation(e.getBlock().getLocation()) == null)
                placedDispensers.remove(e.getBlock());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent e) {
        Main.executor.execute(() -> {
            if (!e.isCancelled())
                e.blockList().forEach(block -> {
                    if ((block.getType() == Material.DISPENSER) &&
                            Region.getByLocation(block.getLocation()) == null)
                        placedDispensers.remove(block);
                });
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent e) {
        Main.executor.execute(() -> {
            if (!e.isCancelled())
                e.blockList().forEach(block -> {
                    if ((block.getType() == Material.DISPENSER) &&
                            Region.getByLocation(block.getLocation()) == null)
                        placedDispensers.remove(block);
                });
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDispense(BlockDispenseEvent e) {
        Main.executor.execute(() -> {
            if (!e.isCancelled()) {
                Region region = Region.getByLocation(e.getBlock().getLocation());

                if (region != null && !region.isItemDrop()) {
                    if (placedDispensers.containsKey(e.getBlock())) {
                        Player player = placedDispensers.get(e.getBlock());

                        if (region.isPlayerInRegion(player))
                            return;

                        Print.toPlayer(player, Message.inst.getMessage("Flags.NotUse"));
                    }
                    e.setCancelled(true);
                }
            }
        });
    }
}
