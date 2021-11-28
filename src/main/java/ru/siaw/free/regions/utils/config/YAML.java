package ru.siaw.free.regions.utils.config;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.siaw.free.regions.Main;

import java.io.File;

public abstract class YAML
{
    protected static final Main plugin = Main.inst;

    protected File file;
    protected final YamlConfiguration configuration = new YamlConfiguration();

    public void Initialize(String fileName) {
        String path = plugin.getDataFolder() + "/" + fileName;
        file = new File(path);

        if (!file.exists()) {
            plugin.saveResource(fileName, true);
        }
        load();
    }

    public abstract void load();
}
