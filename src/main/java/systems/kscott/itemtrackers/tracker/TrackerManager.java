package systems.kscott.itemtrackers.tracker;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import systems.kscott.itemtrackers.ItemTrackers;

public class TrackerManager {

    @Getter
    private static TrackerManager instance;

    public static void init(ItemTrackers plugin) {
        instance = new TrackerManager(plugin);
    }

    private ItemTrackers plugin;

    public TrackerManager(ItemTrackers plugin) {
        this.plugin = plugin;
        this.loadTrackers();
    }

    private void loadTrackers() {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        FileConfiguration trackers = plugin.getTrackersManager().getConfig();

        ConfigurationSection trackerSection = trackers.getConfigurationSection("trackers");


    }

}
