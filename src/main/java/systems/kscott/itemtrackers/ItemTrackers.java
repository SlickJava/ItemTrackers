package systems.kscott.itemtrackers;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import systems.kscott.itemtrackers.tracker.TrackerManager;
import systems.kscott.itemtrackers.util.ConfigFile;

public final class ItemTrackers extends JavaPlugin {

    @Getter
    private ConfigFile configManager;

    @Getter
    private ConfigFile trackersManager;

    @Override
    public void onEnable() {

        configManager = new ConfigFile(this, "config.yml");
        trackersManager = new ConfigFile(this, "trackers.yml");

        // Plugin startup logic
        TrackerManager.init(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
