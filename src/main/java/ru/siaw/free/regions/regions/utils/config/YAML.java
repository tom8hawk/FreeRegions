package ru.siaw.free.regions.regions.utils.config;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.siaw.free.regions.regions.Main;

import java.io.File;

public abstract class YAML
{
    protected final Main plugin = Main.inst;

    protected File file;
    protected final YamlConfiguration configuration = new YamlConfiguration();
    protected String mainKey;

    public void Initialize(String fileName) {
        String path = plugin.getDataFolder() + "/" + fileName;
        file = new File(path);
        mainKey = (fileName.equals("data.yml") ? "Regions" : "Messages") + ".";

        if (!file.exists()) {
            plugin.saveResource(fileName, true);
        }
        load();
    }

    public abstract void load();
}
