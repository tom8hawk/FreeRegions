package ru.siaw.free.regions.regions.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.regions.Region;
import ru.siaw.free.regions.regions.utils.config.Message;

import java.util.HashMap;

public class Selection
{
    private static final HashMap<Player, Selection> selections = new HashMap<>();

    private Location pos1, pos2;

    public Selection(Player player) {
        selections.put(player, this);
    }

    public void create(String name) {
        new Thread(() -> {
            synchronized (selections) {
                selections.forEach((player, selection) -> {
                    if (selection.equals(this)) {
                        if (pos1 == null || pos2 == null) {
                            Print.toPlayer(player, Message.inst.getMessage("Positions.NoSelectRegion"));
                            return;
                        }

                        new Region(name, pos1, pos2, player, false, true, false, false, false, false, true, false, false, true);
                    }
                });
            }
        }).start();
    }

    public static Selection get(Player player) {
        Selection selection = selections.get(player);
        return selection == null ? new Selection(player) : selection;
    }

    public static void remove(Player player) {
        selections.remove(player);
    }

    // Геттеры, сеттеры

    public void setPos1(Location pos1, Player p) {
        this.pos1 = pos1;
        Print.toPlayer(p, Message.inst.getMessage("Positions.Successfully").replace("%pos", String.format("%d, %d, %d", (int) pos1.getX(), (int) pos1.getY(), (int) pos1.getZ())));
    }

    public void setPos2(Location pos2, Player p) {
        this.pos2 = pos2;
        Print.toPlayer(p, Message.inst.getMessage("Positions.Successfully").replace("%pos", String.format("%d, %d, %d", (int) pos2.getX(), (int) pos2.getY(), (int) pos2.getZ())));
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }
}