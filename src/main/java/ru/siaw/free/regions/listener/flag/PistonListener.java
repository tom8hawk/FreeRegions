package ru.siaw.free.regions.listener.flag;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.utils.Print;
import ru.siaw.free.regions.utils.config.Message;

import java.util.HashMap;

public class PistonListener implements Listener
{
    private static final HashMap<Block, Player> placedPistons = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlacePiston(BlockPlaceEvent e) {
        if (!e.isCancelled() && (e.getBlock().getType() == Material.PISTON_BASE || e.getBlock().getType() == Material.PISTON_STICKY_BASE)
                && Region.getByLocation(e.getBlock().getLocation()) == null)
            placedPistons.put(e.getBlockPlaced(), e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreakPiston(BlockBreakEvent e) {
        if (!e.isCancelled() && (e.getBlock().getType() == Material.PISTON_BASE || e.getBlock().getType() == Material.PISTON_STICKY_BASE)
                && Region.getByLocation(e.getBlock().getLocation()) == null)
            placedPistons.remove(e.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent e) {
        if (!e.isCancelled())
            e.blockList().forEach(block -> {
                if ((block.getType() == Material.PISTON_BASE || block.getType() == Material.PISTON_STICKY_BASE) &&
                        Region.getByLocation(block.getLocation()) == null)
                    placedPistons.remove(block);
            });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!e.isCancelled())
            e.blockList().forEach(block -> {
                if ((block.getType() == Material.PISTON_BASE || block.getType() == Material.PISTON_STICKY_BASE) &&
                        Region.getByLocation(block.getLocation()) == null)
                    placedPistons.remove(block);
            });
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent e) {
        Region region = null;

        for (Block block : e.getBlocks()) {
            Region rg = Region.getByLocation(block.getLocation());

            if (rg != null) {
                region = rg;
                break;
            }
        }

        if (region != null && !region.isPiston()) {
            if (placedPistons.containsKey(e.getBlock())) {
                Player player = placedPistons.get(e.getBlock());

                if (region.isInRegion(player))
                    return;

                Print.toPlayer(player, Message.inst.getMessage("Flags.NotUse"));
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        Region region = null;
        if (e.getBlocks().size() < 1) return;

        for (Block block : e.getBlocks()) {
            Region rg = Region.getByLocation(block.getLocation());

            if (rg != null) {
                region = rg;
                break;
            }
        }

        Block last = e.getBlocks().get(e.getBlocks().size() - 1);
        radius:
            for (int x = 2; x >= -2; x--) {
                for (int y = 2; y >= -2; y--) {
                    for (int z = 2; z >= -2; z--) {
                        Region rg = Region.getByLocation(last.getRelative(x, y, z).getLocation());

                        if (rg != null) {
                            region = rg;
                            break radius;
                        }
                    }
                }
            }

        if (region != null && !region.isPiston()) {
            if (placedPistons.containsKey(e.getBlock())) {
                Player player = placedPistons.get(e.getBlock());

                if (region.isInRegion(player))
                    return;

                Print.toPlayer(player, Message.inst.getMessage("Flags.NotUse"));
            }
            e.setCancelled(true);
        }
    }
}
