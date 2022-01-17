package ru.siaw.free.regions.config;

import org.bukkit.ChatColor;
import ru.siaw.free.regions.utils.Print;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        return configuration.getStringList(path).stream().map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
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
