package systems.kscott.itemtrackers.tracker;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import systems.kscott.itemtrackers.ItemTrackers;
import systems.kscott.itemtrackers.exceptions.InvalidMaterialException;
import systems.kscott.itemtrackers.exceptions.InvalidTrackerException;
import systems.kscott.itemtrackers.exceptions.NoTrackerException;
import systems.kscott.itemtrackers.exceptions.TrackerAlreadyExistsException;

import javax.annotation.Nullable;
import javax.transaction.InvalidTransactionException;
import java.util.ArrayList;
import java.util.List;

public class TrackerManager {

    @Getter
    private static TrackerManager instance;

    public static void init(ItemTrackers plugin) {
        instance = new TrackerManager(plugin);
    }

    private ItemTrackers plugin;

    @Getter
    private List<Tracker> trackers;

    @Getter
    private List<ApplicationItem> applicationItems;

    public TrackerManager(ItemTrackers plugin) {
        this.plugin = plugin;
        this.loadTrackers();
        this.loadApplicationItems();
    }

    public Tracker getTracker(String id) throws NoTrackerException {
        for (Tracker t : trackers) {
            if (t.getId().equals(id)) {
                return t;
            }
        }

        throw new NoTrackerException();
    }

    public ItemStack addTracker(ItemStack item, String trackerId) throws NoTrackerException, InvalidMaterialException, TrackerAlreadyExistsException {
        Tracker t = getTracker(trackerId);
        return t.addToItem(item);
    }

    public ApplicationItem getApplicationItem(String trackerId) throws NoTrackerException {
        for (ApplicationItem applicationItem : applicationItems) {
            if (applicationItem.getId().equals(trackerId)) {
                return applicationItem;
            }
        }
        throw new NoTrackerException();
    }

    private void loadTrackers() {

        trackers = new ArrayList<>();

        FileConfiguration config = plugin.getConfigManager().getConfig();
        FileConfiguration trackerConfig = plugin.getTrackersManager().getConfig();

        ConfigurationSection section = trackerConfig.getConfigurationSection("trackers");

        for (String trackerId : section.getKeys(false)) {
            ConfigurationSection trackerSection = section.getConfigurationSection(trackerId);
            String nameFormat = trackerSection.getString("name.name_format");
            boolean displayName = trackerSection.getBoolean("name.display_in_name");
            String loreFormat = trackerSection.getString("lore.lore_format");
            boolean displayLore = trackerSection.getBoolean("lore.display_in_lore");
            String statistic = trackerSection.getString("statistic.name");

            String extraData = null;

            if (trackerSection.contains("statistic.data.material")) {
                extraData = trackerSection.getString("statistic.data.material");
            } else if (trackerSection.contains("statistic.data.entity_type")) {
                extraData = trackerSection.getString("statistic.data.entity_type");
            }

            String incrementType = "";

            List<String> supportedItems = trackerSection.getStringList("supported_items");

            Tracker tracker = new Tracker(trackerId, nameFormat, displayName, loreFormat, displayLore, statistic, extraData, incrementType, supportedItems);

            trackers.add(tracker);
        }
    }

    private void loadApplicationItems() {

        applicationItems = new ArrayList<>();

        FileConfiguration config = plugin.getConfigManager().getConfig();
        FileConfiguration trackers = plugin.getTrackersManager().getConfig();

        ConfigurationSection section = trackers.getConfigurationSection("trackers");

        for (String trackerId : section.getKeys(false)) {
            ConfigurationSection trackerSection = section.getConfigurationSection(trackerId).getConfigurationSection("application_item");

            String name = trackerSection.getString("name");

            List<String> lore = trackerSection.getStringList("lore");

            Material material = Material.valueOf(trackerSection.getString("material"));

            ApplicationItem applicationItem = new ApplicationItem(trackerId, name, lore, material);

            applicationItems.add(applicationItem);
        }
    }

    public void reload() {
        this.loadTrackers();
        this.loadApplicationItems();
    }

}
