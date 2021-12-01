package ru.siaw.free.regions.listener.flag;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.utils.Print;
import ru.siaw.free.regions.utils.config.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BurnListener implements Listener
{
    private static final HashMap<Player, List<Block>> fireToRemove = new HashMap<>();

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        Region region = Region.getByLocation(e.getBlock().getLocation());

        if (e.getIgnitingBlock() != null) {
            Player player = e.getPlayer() != null ? e.getPlayer() : getByBlock(e.getIgnitingBlock());

            if (player != null) {
                if (region != null && !region.isFire() && !region.isPlayerInRegion(player)) {
                    e.setCancelled(true);
                    Print.toPlayer(player, Message.inst.getMessage("Flags.NotFire"));
                } else {
                    put(player, e.getBlock());
                }
            }
        } else if (e.getPlayer() != null) {
            put(e.getPlayer(), e.getBlock());
        }
    }

    private void put(Player p, Block b) {
        fireToRemove.putIfAbsent(p, new ArrayList<>());

        List<Block> list = fireToRemove.get(p);
        list.add(b);

        fireToRemove.put(p, list);
    }

    private Player getByBlock(Block block) {
        Player[] player = new Player[1];
        fireToRemove.forEach((p, blocks) -> { if (blocks.contains(block)) player[0] = p; });
        return player[0];
    }
}
