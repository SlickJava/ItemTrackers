package systems.kscott.itemtrackers;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import systems.kscott.itemtrackers.commands.CommandItemTrackers;
import systems.kscott.itemtrackers.listener.AnvilListener;
import systems.kscott.itemtrackers.listener.CraftListener;
import systems.kscott.itemtrackers.listener.DropListener;
import systems.kscott.itemtrackers.listener.StatisticListener;
import systems.kscott.itemtrackers.tracker.Tracker;
import systems.kscott.itemtrackers.tracker.TrackerManager;
import systems.kscott.itemtrackers.util.ConfigFile;
import systems.kscott.itemtrackers.util.Metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ItemTrackers extends JavaPlugin {

    @Getter
    private ConfigFile configManager;

    @Getter
    private ConfigFile trackersManager;

    @Getter
    private ConfigFile langManager;

    @Override
    public void onEnable() {

        configManager = new ConfigFile(this, "config.yml");
        trackersManager = new ConfigFile(this, "trackers.yml");
        langManager = new ConfigFile(this, "lang.yml");

        TrackerManager.init(this);

        registerCommands();
        registerEvents();

        new Metrics(this, 7101);
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        try {
            manager.getLocales().loadYamlLanguageFile("lang.yml", Locale.ENGLISH);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        manager.registerCommand(new CommandItemTrackers(this));
        manager.getCommandCompletions().registerAsyncCompletion("trackers", c -> {
            TrackerManager trackerManager = TrackerManager.getInstance();
            List<String> trackerList = new ArrayList<>();
            for (Tracker t : trackerManager.getTrackers()) {
                trackerList.add(t.getId());
            }
            return trackerList;
        });
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new StatisticListener(), this);
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftListener(this), this);
        getServer().getPluginManager().registerEvents(new DropListener(this), this);
    }
}
