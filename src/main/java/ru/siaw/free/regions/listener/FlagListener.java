package ru.siaw.free.regions.listener;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.config.Message;
import ru.siaw.free.regions.utils.Print;

import java.util.ArrayList;
import java.util.List;

public class FlagListener implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player))
            return;

        Region region = Region.getByLocation(entity.getLocation());

        if (region != null) {
            Entity damager = e.getDamager();

            if (damager instanceof Player) {
                Player pDamager = (Player) damager;

                if (!bypass(pDamager) && !region.isPvp() && !region.isPlayerInRegion(pDamager)) {
                    e.setCancelled(true);
                    e.getDamager().sendMessage(Message.inst.getMessage("Flags.NotPvP"));
                }
            } else if (!region.isMobDamage())
                e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent e) {
        Region region = Region.getByLocation(e.getBlockPlaced().getLocation());

        if (region != null) {
            Player builder = e.getPlayer();

            if (!bypass(builder) && !region.isBuild() && !region.isPlayerInRegion(builder)) {
                e.setCancelled(true);
                Print.toPlayer(builder, Message.inst.getMessage("Flags.NotBuild"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMultiPlace(BlockMultiPlaceEvent e) {
        Region region = Region.getByLocation(e.getBlockPlaced().getLocation());

        if (region != null) {
            Player builder = e.getPlayer();

            if (!bypass(builder) && !region.isBuild() && !region.isPlayerInRegion(builder)) {
                e.setCancelled(true);
                Print.toPlayer(builder, Message.inst.getMessage("Flags.NotBuild"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        Region region = Region.getByLocation(e.getBlock().getLocation());

        if (region != null) {
            Player builder = e.getPlayer();

            if (!bypass(builder) && !region.isBuild() && !region.isPlayerInRegion(builder)) {
                e.setCancelled(true);
                Print.toPlayer(builder, Message.inst.getMessage("Flags.NotBuild"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntry(PlayerMoveEvent e) {
        Region region = Region.getByLocation(e.getTo());

        if (region != null) {
            Player player = e.getPlayer();

            if (!bypass(player) && !region.isEntry() && !region.isPlayerInRegion(player)) {
                e.getPlayer().teleport(e.getFrom());
                e.getPlayer().sendMessage(Message.inst.getMessage("Flags.NotMove"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInvincible(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player) || e.isCancelled())
            return;

        Player player = (Player)e.getEntity();
        Region region = Region.getByLocation(player.getLocation());

        e.setCancelled(region != null && e.getCause() == EntityDamageEvent.DamageCause.FALL && !bypass(player) && !region.isPlayerInRegion(player) && !region.isInvincible());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent e) {
        List<Block> toRemove = new ArrayList<>();

        e.blockList().parallelStream().forEach(block -> {
            Region region = Region.getByLocation(block.getLocation());

            if (region != null && !region.isExplosion())
                toRemove.add(block);
        });
        toRemove.forEach(e.blockList()::remove);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent e) {
        List<Block> toRemove = new ArrayList<>();

        e.blockList().parallelStream().forEach(block -> {
            Region region = Region.getByLocation(block.getLocation());

            if (region != null && !region.isExplosion()) {
                Entity entity = e.getEntity();

                if (entity instanceof TNTPrimed) {
                    Entity source = ((TNTPrimed) entity).getSource();

                    if (source != null && source.isValid() && source instanceof Player) {
                        Player player = (Player) source;

                        if (region.isPlayerInRegion(player) || bypass(player)) return;

                        player.sendMessage(Message.inst.getMessage("Flags.NotBuild"));
                    }
                }
                toRemove.add(block);
            }
        });
        toRemove.forEach(e.blockList()::remove);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent e) {
        Region region = Region.getByLocation(e.getItemDrop().getLocation());
        Player ejector = e.getPlayer();

        if (region != null && !region.isItemDrop() && !region.isPlayerInRegion(ejector) && !bypass(ejector)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Message.inst.getMessage("Flags.NotDrop"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null)
            return;

        Region region = Region.getByLocation(e.getClickedBlock().getLocation());
        Player player = e.getPlayer();

        if (region != null && !region.isPlayerInRegion(player) && !bypass(player) && !region.isUse()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Message.inst.getMessage("Flags.NotUse"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobSpawn(CreatureSpawnEvent e) {
        Region region = Region.getByLocation(e.getLocation());

        if (region != null && !region.isMobSpawning())
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockIgnite(BlockIgniteEvent e) {
        Region region = Region.getByLocation(e.getBlock().getLocation());

        if (region != null && !region.isFire() && e.getPlayer() != null && !region.isPlayerInRegion(e.getPlayer())) {
            e.setCancelled(true);
            Print.toPlayer(e.getPlayer(), Message.inst.getMessage("Flags.NotFire"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeavesDecay(LeavesDecayEvent e) {
        Region region = Region.getByLocation(e.getBlock().getLocation());

        if (region != null && !region.isLeavesFalling())
            e.setCancelled(true);
    }

    private boolean bypass(Player player) {
        return player.hasPermission("freerg.*") || player.hasPermission("freerg.bypass");
    }
}
