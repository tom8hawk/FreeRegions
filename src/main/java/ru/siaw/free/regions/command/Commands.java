package ru.siaw.free.regions.command;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import ru.siaw.free.regions.Main;
import ru.siaw.free.regions.Region;
import ru.siaw.free.regions.Selection;
import ru.siaw.free.regions.config.Message;
import ru.siaw.free.regions.gui.Gui;
import ru.siaw.free.regions.utils.Other;
import ru.siaw.free.regions.utils.Print;

public class Commands implements CommandExecutor
{
    private static final Main plugin = Main.inst;
    private static final Message message = Message.inst;

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player player = null; if (sender instanceof Player) player = (Player)sender;
            switch(args[0].toLowerCase()) {
                case "wand":
                    if (validate(sender, true, "wand")) {
                        sender.sendMessage(Message.inst.getMessage("Wand"));
                        player.getInventory().addItem(Other.createItemStack(Material.ARROW, "§eВыделитель региона", " §7ЛКМ => 1 позиция", " §7ПКМ => 2 позиция"));
                    }
                    return false;
                case "pos1":
                    if (isPlayer(sender))
                        Selection.get(player).setPos1(player.getLocation(), player);
                    return false;
                case "pos2":
                    if (isPlayer(sender))
                        Selection.get(player).setPos2(player.getLocation(), player);
                    return false;
                case "create":
                    if (validate(sender, true, "create"))
                        if (args.length > 1)
                            Selection.get(player).create(args[1]);
                        else Print.toSender(sender, message.getMessage("Create.Usage"));
                    return false;
                case "remove":
                    if (validate(sender, false, "remove")) {
                        if (args.length > 1) {
                            Region region = Region.getByName(args[1]);
                            if (region != null) {
                                if (region.getOwners().contains(player) || validate(sender, false, "removeOther")) {
                                    region.remove();
                                    Print.toSender(sender, message.getMessage("Remove.Successfully").replace("%region", region.getName()));
                                }
                                else Print.toSender(sender, message.getMessage("Remove.YouArentOwner"));
                            }
                            else Print.toSender(sender, message.getMessage("Remove.NotExists"));
                        }
                        else Print.toSender(sender, message.getMessage("Remove.Usage"));
                    }
                    return false;
                case "info":
                    if (validate(sender, false, "info")) {
                        Region region = null;

                        if (args.length == 1 && isPlayer(sender))
                            region = Region.getByLocation(player.getLocation());

                        else if (args.length == 2 && validate(sender, false, "infoOther"))
                            region = Region.getByName(args[1]);

                        if (region != null) {
                            Region finalRegion = region;

                            message.getList("Info.Successfully").forEach(line -> Print.toSender(sender, Other.replaceRegionInfo(line, finalRegion)));
                        }
                        else Print.toSender(sender, message.getMessage("Info.NotExists"));
                    }
                    return false;
                case "flag":
                case "delflag":
                case "addflag":
                    if (args.length > 1) {
                        Region region = Region.getByName(args[1]);
                        if (region != null) {
                            if (region.isPlayerInRegion(player) || validate(sender, true, "changeOtherFlags")) {
                                Gui.flags(player, region);
                            } else Print.toSender(sender, message.getMessage("NotYourRegion"));
                        } else Print.toSender(sender, message.getMessage("ChangeFlag.NotExists"));
                    } else Print.toSender(sender, message.getMessage("ChangeFlag.Usage"));
                    return false;
                case "all":
                    if (validate(sender, true, "all"))
                        Gui.allRegions(player);
                    return false;
                case "delowner":
                    if (validate(sender, false, "delowner")) {
                        if (args.length > 2) {
                            Region region = Region.getByName(args[1]);
                            if (region != null) {
                                if (region.getOwners().size() > 1) {
                                    OfflinePlayer toRemove = null;
                                    for (OfflinePlayer owner : region.getOwners()) {
                                        if (owner.getName().equalsIgnoreCase(args[2])) {
                                            toRemove = owner;
                                            break;
                                        }
                                    }
                                    if (toRemove == null) {
                                        Print.toSender(sender, message.getMessage("RemoveRole.NotFound").replace("%role", "Владелец региона"));
                                        return false;
                                    }

                                    if (isPlayer(sender)) {
                                        if (!region.getOwners().contains(player) && !validate(sender, true, "delOtherOwner")) {
                                            Print.toSender(sender, message.getMessage("RemoveRole.NoPermissions"));
                                            return false;
                                        } else if (toRemove.equals(player)) {
                                            Print.toSender(sender, message.getMessage("RemoveRole.Yourself"));
                                            return false;
                                        }
                                    }
                                    region.removeOwner(toRemove);
                                    Print.toSender(sender, message.getMessage("RemoveRole.Successfully").replace("%player", toRemove.getName())
                                            .replace("%region", region.getName()));
                                }
                                else Print.toSender(sender, message.getMessage("RemoveRole.Single"));
                            }
                            else Print.toSender(sender, message.getMessage("RemoveRole.NotExists"));
                        }
                        else Print.toSender(sender, message.getMessage("RemoveRole.Usage").replace("%subcommand", "delowmer"));
                    }
                    return false;
                case "reload":
                    if (validate(sender, false, "reload")) {
                        plugin.onDisable();
                        plugin.enable();
                    }
                    return false;
                case "help":
                    Message.inst.getList("HelpPage").forEach(msg -> Print.toSender(sender, msg));
                    return false;
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
