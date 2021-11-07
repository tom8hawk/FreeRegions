package ru.siaw.free.regions.regions.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.regions.Main;

public class Print
{
    public static void toConsole(String msg) {
        System.out.println(msg);
    }

    public static void toSender(CommandSender sender, String msg) {
        sender.sendMessage(msg);
    }

    public static void toPlayer(Player p, String msg) {
        p.sendMessage(msg);
    }
}
