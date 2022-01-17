package ru.siaw.free.regions.utils;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import ru.siaw.free.regions.Main;
import ru.siaw.free.regions.Selection;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtil
{
    private final Player player;

    public PlayerUtil(Player player) {
        this.player = player;
    }

    public int getLimitOfBlocks() {
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if (permission.getPermission().startsWith("freerg.limit.blocks.")) {
                String[] split = permission.getPermission().split("\\.");
                return Integer.parseInt(split[3]);
            }
        }
        return Integer.MAX_VALUE;
    }

    public int getLimitOfRegions() {
        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if (permission.getPermission().startsWith("freerg.limit.region.")) {
                String[] split = permission.getPermission().split("\\.");
                return Integer.parseInt(split[3]);
            }
        }
        return Integer.MAX_VALUE;
    }

    public void showEffect(Location pos1, Location pos2) {
        Main.executor.execute(() -> {
            ArrayList<Location> initialLocations = new ArrayList<>();

            int xMin = Math.min(pos1.getBlockX(), pos2.getBlockX());
            int yMin = Math.min(pos1.getBlockY(), pos2.getBlockY());
            int zMin = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

            int xMax = Math.max(pos1.getBlockX(), pos2.getBlockX());
            int yMax = Math.max(pos1.getBlockY(), pos2.getBlockY());
            int zMax = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

            World w = pos1.getWorld();
            for (int nowX = xMin; nowX <= xMax; nowX++) {
                initialLocations.add(new Location(w, nowX, yMin, zMin));
                initialLocations.add(new Location(w, nowX, yMin, zMax));
            }
            for (int nowZ = zMin; nowZ <= zMax; nowZ++) {
                initialLocations.add(new Location(w, xMin, yMin, nowZ));
                initialLocations.add(new Location(w, xMax, yMin, nowZ));
            }

            List<Location> effect = new ArrayList<>(initialLocations);
            initialLocations.forEach(loc -> {
                for (double nowY = yMin + 1; nowY <= yMax; nowY++)
                    effect.add(new Location(w, loc.getX(), nowY, loc.getZ()));
            });

            List<Location> corners = new ArrayList<>();
            for (int num = 1; num <= 2; num++) {
                int y = num == 1 ? yMin : yMax;

                corners.add(new Location(w, xMin, y, zMin));
                corners.add(new Location(w, xMin, y, zMax));
                corners.add(new Location(w, xMax, y, zMin));
                corners.add(new Location(w, xMax, y, zMax));
            }
            effect.removeAll(corners);

            while (true) {
                Selection selection = Selection.get(player);
                Location loc1 = selection.getPos1();
                Location loc2 = selection.getPos2();

                if (loc1 == null || loc2 == null || !loc1.equals(pos1) || !loc2.equals(pos2)) break;

                effect.parallelStream().forEach(loc -> player.spigot().playEffect(loc, Effect.FLAME, 0, 0, 0, 0, 0, 0, 1, 100));
                corners.parallelStream().forEach(corner -> player.spigot().playEffect(corner, Effect.CLOUD, 0, 0, 0, 0, 0, 0, 30, 100));

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}