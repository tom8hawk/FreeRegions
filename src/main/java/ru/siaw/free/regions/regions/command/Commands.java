package ru.siaw.free.regions.regions.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.regions.Main;
import ru.siaw.free.regions.regions.utils.Other;
import ru.siaw.free.regions.regions.utils.Selection;
import ru.siaw.free.regions.regions.utils.config.Message;
import ru.siaw.free.regions.regions.utils.Print;

public class Commands implements CommandExecutor
{
    private static final Main plugin = Main.inst;
    private static final Message message = Message.inst;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player player = null; if (sender instanceof Player) player = (Player)sender;
            switch(args[0].toLowerCase()) {
                case "reload":
                    if (validate(sender, false, "reload")) {
                        plugin.onDisable();
                        plugin.enable();
                    }
                    break;
                case "wand":
                    if (validate(sender, true, "wand")) {
                        sender.sendMessage(Message.inst.getMessage("Wand"));
                        player.getInventory().addItem(Other.createItemStack(Material.ARROW, "§eВыделитель региона", " §7ЛКМ => 1 позиция", " §7ПКМ => 2 позиция"));
                    }
                    break;
                case "create":
                    if (validate(sender, true, "create")) { //todo: Проверка разрешенного количества регионов игрока в праве
                        if (args.length > 1) {
                            Selection selection = Selection.get(player);
                            if (selection == null) {
                                player.sendMessage(Message.inst.getMessage("Positions.NoSelectRegion"));
                                return false;
                            }

                            selection.create(args[1]);
                        }
                        else Print.toSender(sender, message.getMessage("Create.Usage"));
                    }
                    break;
                case "help":
                    Message.inst.getList("HelpPage").forEach(msg -> Print.toSender(sender, msg));
                    break;
                default:
                    Print.toSender(sender, message.getMessage("unknownCommand"));
            }
        }
        return false;
    }

    private static final String permPrefix = "freerg.";
    private boolean validate(CommandSender sender, boolean needPlayer, String permission) {
        if (needPlayer && !(sender instanceof Player)) {
            Print.toSender(sender, message.getMessage("NotPlayer"));
            return false;
        }

        if (!sender.hasPermission(permPrefix + permission) && !sender.hasPermission(permPrefix + "*") && !sender.isOp() && !(sender instanceof ConsoleCommandSender)) {
            Print.toSender(sender, message.getMessage("NoPermissions"));
            return false;
        }
        return true;
    }
}
