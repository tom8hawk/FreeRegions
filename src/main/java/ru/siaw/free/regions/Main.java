package ru.siaw.free.regions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.siaw.free.regions.command.Commands;
import ru.siaw.free.regions.listener.PlayerListener;
import ru.siaw.free.regions.listener.flag.FlagListener;
import ru.siaw.free.regions.listener.flag.PistonsLimiter;
import ru.siaw.free.regions.utils.Print;
import ru.siaw.free.regions.utils.config.DataBase;
import ru.siaw.free.regions.utils.config.Message;

import java.io.File;

public final class Main extends JavaPlugin
{
    public static Main inst;

    public Main() {
        inst = this;
    }

    @Override
    public void onEnable() {
        enable();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new FlagListener(), this);
        Bukkit.getPluginManager().registerEvents(new PistonsLimiter(), this);

        getCommand("rg").setExecutor(new Commands());
    }

    public void enable() {
        Print.toConsole("Запуск! :>");
        File dataFolder = getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdir();
        new Message();
        new DataBase();
    }

    @Override
    public void onDisable() {
        Region.getRegions().forEach(DataBase.inst::writeRegion);

        Bukkit.getOnlinePlayers().forEach(Selection::remove);

        Print.toConsole("До новых встреч! :0");
    }
}
