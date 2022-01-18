package ru.siaw.free.regions;

import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.siaw.free.regions.command.Commands;
import ru.siaw.free.regions.config.DataBase;
import ru.siaw.free.regions.config.Message;
import ru.siaw.free.regions.listener.DispenseListener;
import ru.siaw.free.regions.listener.FlagListener;
import ru.siaw.free.regions.listener.PistonListener;
import ru.siaw.free.regions.listener.PlayerListener;
import ru.siaw.free.regions.utils.Print;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Main extends JavaPlugin
{
    public static Main inst;
    public static InventoryManager inventoryManager;
    public static final Executor executor = Executors.newCachedThreadPool();

    public Main() {
        inst = this;
    }

    @Override
    public void onEnable() {
        enable();

        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new FlagListener(), this);
        Bukkit.getPluginManager().registerEvents(new PistonListener(), this);
        Bukkit.getPluginManager().registerEvents(new DispenseListener(), this);

        PistonListener.scheduling();
        DispenseListener.scheduling();

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();
    }

    public void enable() {
        Print.toConsole("Запуск! :>");

        File dataFolder = getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdir();

        new DataBase();
        new Message();

        getCommand("rg").setExecutor(new Commands());
    }

    @Override
    public void onDisable() {
        Region.getRegions().parallelStream().forEach(DataBase.inst::writeRegion);
        Region.getRegions().clear();

        Print.toConsole("До новых встреч! :0");
    }
}
