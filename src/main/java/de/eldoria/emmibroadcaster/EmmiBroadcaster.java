package de.eldoria.emmibroadcaster;

import de.eldoria.emmi.converter.JsonConverter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EmmiBroadcaster extends JavaPlugin {
    private FileConfiguration broadcastConfig;

    @Override
    public void onEnable() {
        createCustomConfig();
        startBroadcasters();
    }

    public void startBroadcasters() {
        BukkitScheduler s = this.getServer().getScheduler();
        Random randomGenerator = new Random();
        
        List<Map<String, Object>> l = (List<Map<String, Object>>) this.getBroadcastConfig().getList("messages");

        for (Map<String, Object> o : l) {
            int interval = (int) ((Double) o.get("interval") * 1200);
            s.scheduleSyncRepeatingTask(this, () -> {
                for (Player p : this.getServer().getOnlinePlayers()) {
                    if (p.hasPermission((String)o.get("permission"))) {
                        List<String> messages = (List<String>) o.get("messages");
                        String broadcast = messages.get(randomGenerator.nextInt(messages.size()));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + p.getName() + " " + JsonConverter.convert(broadcast));
                    }
                }
            }, interval, interval);
        }
    }

    public FileConfiguration getBroadcastConfig() {
        return this.broadcastConfig;
    }

    private void createCustomConfig() {
        File broadcastConfigFile = new File(getDataFolder(), "broadcasts.yml");
        if (!broadcastConfigFile.exists()) {
            broadcastConfigFile.getParentFile().mkdirs();
            saveResource("broadcasts.yml", false);
        }

        broadcastConfig = new YamlConfiguration();
        try {
            broadcastConfig.load(broadcastConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
