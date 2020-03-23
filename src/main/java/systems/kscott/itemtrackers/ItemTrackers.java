package systems.kscott.itemtrackers;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import systems.kscott.itemtrackers.commands.CommandItemTrackers;
import systems.kscott.itemtrackers.listener.DropListener;
import systems.kscott.itemtrackers.listener.StatisticListener;
import systems.kscott.itemtrackers.tracker.Tracker;
import systems.kscott.itemtrackers.tracker.TrackerManager;
import systems.kscott.itemtrackers.util.ConfigFile;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
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
        getServer().getPluginManager().registerEvents(new DropListener(this), this);
    }
}
