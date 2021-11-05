package ru.siaw.free.regions.regions.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.siaw.free.regions.regions.Main;
import ru.siaw.free.regions.regions.utils.config.Message;
import ru.siaw.free.regions.regions.utils.Print;

public class Commands implements CommandExecutor
{
    private static final Main plugin = Main.inst;
    private static final Message message = Message.inst;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch(args[0].toLowerCase()) {
                case "reload":
                    if (hasPermission(sender, "reload")) {
                        Bukkit.getScheduler().cancelTasks(plugin);
                        plugin.getPluginLoader().disablePlugin(plugin);
                        plugin.getPluginLoader().enablePlugin(plugin);
                        sender.sendMessage("Успешно перезагружен");
                    }
                    break;
            }
        }
        return false;
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission("freerg" + permission))
            return true;
        Print.toSender(sender, message.getMessage("NoPermissions"));
        return false;
    }
}
