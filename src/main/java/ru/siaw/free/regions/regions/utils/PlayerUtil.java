package ru.siaw.free.regions.regions.utils;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PlayerUtil {
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
}