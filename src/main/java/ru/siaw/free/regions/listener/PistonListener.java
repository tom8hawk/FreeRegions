package ru.siaw.free.regions.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import ru.siaw.free.regions.Main;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.config.Message;
import ru.siaw.free.regions.utils.Print;

import java.util.*;

public class PistonListener implements Listener
{
    private static final HashMap<Block, Player> placedPistons = new HashMap<>();

    public static void scheduling() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            public void run() {
                List<Block> toRemove = new ArrayList<>();

                placedPistons.entrySet().parallelStream().forEach(entry -> {
                    Block nowBlock = entry.getKey().getWorld().getBlockAt(entry.getKey().getLocation());

                    if (nowBlock.getType() != Material.PISTON_BASE && nowBlock.getType() != Material.PISTON_STICKY_BASE)
                        toRemove.add(entry.getKey());
                });
                toRemove.forEach(placedPistons::remove);
            }
        }, 300000L, 300000L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlacePiston(BlockPlaceEvent e) {
        Main.executor.execute(() -> {
            if (!e.isCancelled() && (e.getBlock().getType() == Material.PISTON_BASE || e.getBlock().getType() == Material.PISTON_STICKY_BASE)
                    && Region.getByLocation(e.getBlock().getLocation()) == null)
                placedPistons.put(e.getBlockPlaced(), e.getPlayer());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreakPiston(BlockBreakEvent e) {
        Main.executor.execute(() -> {
            if (!e.isCancelled() && (e.getBlock().getType() == Material.PISTON_BASE || e.getBlock().getType() == Material.PISTON_STICKY_BASE)
                    && Region.getByLocation(e.getBlock().getLocation()) == null)
                placedPistons.remove(e.getBlock());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent e) {
        Main.executor.execute(() -> {
            if (!e.isCancelled())
                e.blockList().parallelStream().filter(block -> (block.getType() == Material.PISTON_BASE || block.getType() == Material.PISTON_STICKY_BASE) &&
                                Region.getByLocation(block.getLocation()) == null).forEach(placedPistons::remove);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent e) {
        Main.executor.execute(() -> {
            if (!e.isCancelled())
                e.blockList().parallelStream().filter(block -> (block.getType() == Material.PISTON_BASE || block.getType() == Material.PISTON_STICKY_BASE) &&
                        Region.getByLocation(block.getLocation()) == null).forEach(placedPistons::remove);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPistonRetract(BlockPistonRetractEvent e) {
        Optional<Region> result = e.getBlocks().parallelStream().map(block -> Region.getByLocation(block.getLocation())).findFirst();

        if (result.isPresent() && !result.get().isPiston()) {
            if (placedPistons.containsKey(e.getBlock())) {
                Player player = placedPistons.get(e.getBlock());

                if (result.get().isPlayerInRegion(player))
                    return;

                Print.toPlayer(player, Message.inst.getMessage("Flags.NotUse"));
            }
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        if (e.getBlocks().isEmpty()) return;
        Region region = e.getBlocks().parallelStream()
                .map(block -> Region.getByLocation(block.getLocation()))
                .findFirst().orElse(getRegionByLast(e.getBlocks().get(e.getBlocks().size() - 1)));

        if (region != null && !region.isPiston()) {
            if (placedPistons.containsKey(e.getBlock())) {
                Player player = placedPistons.get(e.getBlock());

                if (region.isPlayerInRegion(player))
                    return;

                Print.toPlayer(player, Message.inst.getMessage("Flags.NotUse"));
            }
            e.setCancelled(true);
        }
    }

    private Region getRegionByLast(Block last) {
        for (int x = 2; x >= -2; x--) {
            for (int y = 2; y >= -2; y--) {
                for (int z = 2; z >= -2; z--) {
                    Region region = Region.getByLocation(last.getRelative(x, y, z).getLocation());

                    if (region != null)
                        return region;
                }
            }
        }
        return null;
    }
}
