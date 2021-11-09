package ru.siaw.free.regions.regions.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.regions.Main;
import ru.siaw.free.regions.regions.Region;
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
                case "wand":
                    if (validate(sender, true, "wand")) {
                        sender.sendMessage(Message.inst.getMessage("Wand"));
                        player.getInventory().addItem(Other.createItemStack(Material.ARROW, "§eВыделитель региона", " §7ЛКМ => 1 позиция", " §7ПКМ => 2 позиция"));
                    }
                    break;
                case "pos1":
                    if (isPlayer(sender))
                        Selection.get(player).setPos1(player.getLocation(), player);
                    break;
                case "pos2":
                    if (isPlayer(sender))
                        Selection.get(player).setPos2(player.getLocation(), player);
                    break;
                case "create":
                    if (validate(sender, true, "create")) { //todo: Проверка разрешенного количества регионов игрока в праве
                        if (args.length > 1) {
                            Selection selection = Selection.get(player);
                            selection.create(args[1]);
                        }
                        else Print.toSender(sender, message.getMessage("Create.Usage"));
                    }
                    break;
                case "remove":
                    if (validate(sender, false, "remove")) {
                        if (args.length > 1) {
                            Region region = Region.getByName(args[1]);
                            if (region != null) {
                                if (region.getOwners().contains(player) || validate(sender, false, "removeAny")) {
                                    region.remove();
                                    Print.toSender(sender, message.getMessage("Remove.Successfully"));
                                }
                                else Print.toSender(sender, message.getMessage("Remove.YouArentOwner"));
                            }
                            else Print.toSender(sender, message.getMessage("Remove.NotExists"));
                        }
                        else Print.toSender(sender, message.getMessage("Remove.Usage"));
                    }
                    break;
                case "reload":
                    if (validate(sender, false, "reload")) {
                        plugin.onDisable();
                        plugin.enable();
                    }
                    break;
                case "help":
                    Message.inst.getList("HelpPage").forEach(msg -> Print.toSender(sender, msg));
                    break;
            }
        }
        Print.toSender(sender, message.getMessage("unknownCommand"));
        return false;
    }

    private static final String permPrefix = "freerg.";
    private boolean validate(CommandSender sender, boolean needPlayer, String permission) {
        if (needPlayer && !(isPlayer(sender))) {
            Print.toSender(sender, message.getMessage("NotPlayer"));
            return false;
        }

        if (!sender.hasPermission(permPrefix + permission) && !sender.hasPermission(permPrefix + "*") && !sender.isOp() && !(sender instanceof ConsoleCommandSender)) {
            Print.toSender(sender, message.getMessage("NoPermissions"));
            return false;
        }
        return true;
    }

    private boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

}
