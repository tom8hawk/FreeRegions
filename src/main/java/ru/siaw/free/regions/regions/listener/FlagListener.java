package ru.siaw.free.regions.regions.listener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.siaw.free.regions.regions.Region;
import ru.siaw.free.regions.regions.utils.Print;
import ru.siaw.free.regions.regions.utils.config.Message;

public class FlagListener implements Listener
{
    @EventHandler
    public void onPVP(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player))
            return;

        Region region = Region.getByLocation(entity.getLocation());

        if (region != null) {
            Entity damager = e.getDamager();

            if (damager instanceof Player) {
                Player pDamager = (Player) damager;

                if (!bypass(pDamager) && !region.isPvp() && !region.isInRegion(pDamager)) {
                    e.setCancelled(true);
                    e.getDamager().sendMessage(Message.inst.getMessage("Flags.NotPvP"));
                }
            } else if (damager instanceof Monster && !region.isMobDamage())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Region region = Region.getByLocation(e.getBlockPlaced().getLocation());

        if (region != null) {
            Player builder = e.getPlayer();

            if (!bypass(builder) && !region.isBuild() && !region.isInRegion(builder)) {
                e.setCancelled(true);
                Print.toPlayer(builder, Message.inst.getMessage("Flags.notBuild"));
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Region region = Region.getByLocation(e.getBlock().getLocation());

        if (region != null) {
            Player builder = e.getPlayer();

            if (!bypass(builder) && !region.isBuild() && !region.isInRegion(builder)) {
                e.setCancelled(true);
                Print.toPlayer(builder, Message.inst.getMessage("Flags.notBuild"));
            }
        }
    }

    @EventHandler
    public void onEntry(PlayerMoveEvent e) {
        Region region = Region.getByLocation(e.getTo());

        if (region != null) {
            Player player = e.getPlayer();

            if (!bypass(player) && !region.isEntry() && !region.isInRegion(player)) {
                e.getPlayer().sendMessage(Message.inst.getMessage("Flags.NotMove"));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInvincible(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;

        Player player = (Player)e.getEntity();
        Region region = Region.getByLocation(player.getLocation());

        if (region != null) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL && !bypass(player) && !region.isInRegion(player) && !region.isInvincible())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTnt(EntityExplodeEvent e) {
        Region region = Region.getByLocation(e.getLocation());

        if (region != null && !region.isExplosion()) {
            Entity entity = e.getEntity();

            if (entity instanceof TNTPrimed) {
                Entity source = ((TNTPrimed) entity).getSource();

                if (source != null && source.isValid() && source instanceof Player) {
                    Player player = (Player) source;
                    if (!region.isInRegion(player) && !bypass(player))
                        player.sendMessage(Message.inst.getMessage("Flags.NotBuild"));
                    else return;
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Region region = Region.getByLocation(e.getItemDrop().getLocation());
        Player ejector = e.getPlayer();

        if (region != null && !region.isItemDrop() && !region.isInRegion(ejector) && !bypass(ejector)) {
            e.getPlayer().sendMessage(Message.inst.getMessage("Flags.NotDrop"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null)
            return;

        Region region = Region.getByLocation(e.getClickedBlock().getLocation());
        Player player = e.getPlayer();

        if (region != null && !region.isInRegion(player) && !bypass(player) && !region.isUse()) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Message.inst.getMessage("Flags.NotUse"));
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e) {
        Region region = Region.getByLocation(e.getLocation());

        if (region != null && !region.isMobSpawning())
            e.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        Region region = Region.getByLocation(e.getBlock().getLocation());

        if (region != null && !region.isLeavesFalling())
            e.setCancelled(true);
    }

    private boolean bypass(Player player) {
        return player.hasPermission("freerg.*") || player.hasPermission("freerg.bypass");
    }
}