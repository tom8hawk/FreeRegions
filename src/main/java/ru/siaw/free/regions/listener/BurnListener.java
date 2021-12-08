package ru.siaw.free.regions.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.config.Message;
import ru.siaw.free.regions.utils.Print;

public class BurnListener implements Listener
{
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        Region region = Region.getByLocation(e.getBlock().getLocation());

        if (region != null && !region.isFire() && e.getPlayer() != null && !region.isPlayerInRegion(e.getPlayer())) {
            e.setCancelled(true);
            Print.toPlayer(e.getPlayer(), Message.inst.getMessage("Flags.NotFire"));
        }
    }
}
