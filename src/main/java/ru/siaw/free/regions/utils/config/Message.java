package ru.siaw.free.regions.utils.config;

import org.bukkit.ChatColor;
import ru.siaw.free.regions.utils.Print;

import java.io.IOException;
import java.util.List;

public final class Message extends YAML
{
    public static Message inst;

    public Message() {
        Initialize("messages.yml");
        inst = this;
    }

    public String getMessage(String path) {
        String result = configuration.getString(path);
        return ChatColor.translateAlternateColorCodes('&', result != null ? result : "");
    }

    public List<String> getList(String path) {
        List<String> output = configuration.getStringList(path);
        for (int i = 0; i < output.size(); i++) {
            output.set(i, ChatColor.translateAlternateColorCodes('&', output.get(i)));
        }
        return output;
    }

    @Override
    public void load() {
        try {
            configuration.load(file);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
        Print.toConsole("Список сообщений сохранен.");
    }
}
