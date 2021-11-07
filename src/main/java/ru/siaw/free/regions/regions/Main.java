package ru.siaw.free.regions.regions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.siaw.free.regions.regions.command.Commands;
import ru.siaw.free.regions.regions.listener.PlayerListener;
import ru.siaw.free.regions.regions.utils.Print;
import ru.siaw.free.regions.regions.utils.config.Message;
import ru.siaw.free.regions.regions.utils.config.DataBase;

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
        Bukkit.getScheduler().cancelTasks(this);
        Region.getRegions().forEach(DataBase.inst::writeRegion);
        Print.toConsole("До новых встреч! :0");
    }
}
