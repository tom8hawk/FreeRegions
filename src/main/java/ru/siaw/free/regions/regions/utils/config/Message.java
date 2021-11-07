package ru.siaw.free.regions.regions.utils.config;

import java.io.IOException;
import java.util.List;
import org.bukkit.ChatColor;
import ru.siaw.free.regions.regions.utils.Print;

public final class Message extends YAML
{
    public static Message inst;

    public Message() {
        Initialize("messages.yml");
        inst = this;
    }

    public String getMessage(String path) {
        String output = configuration.getString(mainKey + path);
        return ChatColor.translateAlternateColorCodes('&', output);
    }

    public List<String> getList(String path) {
        List<String> output = configuration.getStringList(mainKey + path);
        for (int i = 0; i < output.size(); i++) {
            output.set(i, ChatColor.translateAlternateColorCodes('&', output.get(i)));
        }
        return output;
    }

    @Override
    public void load() {
        try {
            configuration.load(file);
        } catch (IOException |org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Print.toConsole("Список сообщений сохранен.");
    }
}
