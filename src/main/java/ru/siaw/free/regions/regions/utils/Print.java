package ru.siaw.free.regions.regions.utils;

import org.bukkit.command.CommandSender;

public class Print
{
    public static void toConsole(String msg) {
        System.out.println("§6FreeRegion §8| §f" + msg);
    }

    public static void toSender(CommandSender sender, String msg) {
        sender.sendMessage(msg);
    }
}
