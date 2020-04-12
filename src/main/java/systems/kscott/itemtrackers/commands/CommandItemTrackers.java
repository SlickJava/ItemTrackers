package systems.kscott.itemtrackers.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import systems.kscott.itemtrackers.ItemTrackers;
import systems.kscott.itemtrackers.exceptions.InvalidMaterialException;
import systems.kscott.itemtrackers.exceptions.NoTrackerException;
import systems.kscott.itemtrackers.exceptions.TrackerAlreadyExistsException;
import systems.kscott.itemtrackers.tracker.ApplicationItem;
import systems.kscott.itemtrackers.tracker.Tracker;
import systems.kscott.itemtrackers.tracker.TrackerManager;
import systems.kscott.itemtrackers.util.Chat;

import java.util.List;

@CommandAlias("itemtrackers|it")
@CommandPermission("itemtrackers.main")
public class CommandItemTrackers extends BaseCommand {

    private ItemTrackers plugin;

    public CommandItemTrackers(ItemTrackers plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandAlias("help")
    public void help(CommandSender sender) {
        Chat.msg(sender, "&3[&eItemTrackers&3] &7v"+plugin.getDescription().getVersion()+" by &b89apt89");
        Chat.msg(sender, "&b/it list &8- &7Get a list of all trackers.");
        Chat.msg(sender, "&b/it info <tracker> &8- &7Get info on a tracker");
        Chat.msg(sender, "&b/it add <tracker> &8- &7Add a tracker to the item you're currently holding");
        Chat.msg(sender, "&b/it give <tracker> [amount] [playerName] &8- &7Get a tracker application item");
    }

    @Subcommand("list")
    public void list(CommandSender sender) {

        List<Tracker> trackers = TrackerManager.getInstance().getTrackers();

        int size = trackers.size();
        String trackerString = "";

        for (int i = 0; i < size; i++) {
            Tracker tracker = trackers.get(i);
            trackerString = "&b"+trackerString + tracker.getId();
            if (i+1 != size) {
                trackerString = trackerString + "&7, ";
            }
        }

        Chat.msg(sender, "&3[&eItemTrackers&3] &7There are currently &b"+size+" &7trackers:");
        Chat.msg(sender, trackerString);
    }

    @Subcommand("info")
    @CommandCompletion("@trackers")
    @Syntax("&7/it info &b<tracker>")
    public void info(CommandSender sender, String trackerName) {

        String prefix = plugin.getLangManager().getConfig().getString("prefix");

        Tracker t = null;
        try {
            t = TrackerManager.getInstance().getTracker(trackerName);
        } catch (NoTrackerException e) {
            Chat.msg(sender, prefix+" &cA tracker by that ID cannot be found!");
            return;
        }

        if (t != null) {
            Chat.msg(sender, prefix+" &7Information for the tracker &b" + trackerName + "&7:");
            Chat.msg(sender, prefix+" &7Statistic: &b"+t.getStatistic());
            if (t.getExtraData() != null) {
                Chat.msg(sender, prefix+" &7Extra data: &b"+t.getExtraData());
            }
            Chat.msg(sender, prefix+" &7Display in name: &b"+t.isDisplayName());
            if (t.isDisplayName()) {
                Chat.msg(sender, prefix+" &7Name format: &b"+t.getNameFormat());
            }
            Chat.msg(sender, prefix+" &7Display in lore: &b"+t.isDisplayLore());
            if (t.isDisplayLore()) {
                Chat.msg(sender, prefix+" &7Name format: &b"+t.getLoreFormat());
            }
            Chat.msg(sender, prefix+" &7Use &b/it info application" + trackerName + " &7to see info on the application item.");
        }
    }

    @Subcommand("info application")
    @Syntax("&7/it info application &b<tracker>")
    public void infoApplication(CommandSender sender, String trackerName) {
        Chat.msg(sender, "&3TODO");
    }

    @Subcommand("add")
    @CommandCompletion("@trackers")
    @Syntax("&7/it add <tracker>")
    public void addTracker(Player player, String trackerName) {
        String prefix = plugin.getLangManager().getConfig().getString("prefix");

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        int index = player.getInventory().getHeldItemSlot();

        ItemStack newItem = null;

        try {
            newItem = TrackerManager.getInstance().addTracker(heldItem, trackerName);
        } catch (NoTrackerException e) {
            Chat.msg(player, prefix+"&cA tracker by that ID cannot be found.");
        } catch (InvalidMaterialException e) {
            Chat.msg(player, prefix + plugin.getLangManager().getConfig().getString("tracker-not-compatible"));
        } catch (TrackerAlreadyExistsException e) {
            Chat.msg(player, prefix +"&cThis item already has that tracker.");
        }

        if (newItem != null) {
            player.getInventory().setItem(index, newItem);
            Chat.msg(player, prefix+plugin.getLangManager().getConfig().getString("tracker-added").replace("%name%", trackerName));
        }
    }

    @Subcommand("reload")
    public void reload(Player player) {
        String prefix = plugin.getLangManager().getConfig().getString("prefix");

        plugin.getConfigManager().reload();
        plugin.getLangManager().reload();
        plugin.getTrackersManager().reload();
        TrackerManager.getInstance().reload();

        Chat.msg(player, prefix+"&7Successfully reloaded &3config.yml&7, &3trackers.yml&7, and &3lang.yml&7.");
    }

    @Subcommand("get|give")
    @CommandCompletion("@trackers")
    @Syntax("&7/it get &b<tracker> [amount] [player]")
    public void getApplicationItem(Player player, String trackerName) {
        String prefix = plugin.getLangManager().getConfig().getString("prefix");
        TrackerManager manager = TrackerManager.getInstance();

        ApplicationItem applicationItem = null;

        try {
            applicationItem = manager.getApplicationItem(trackerName);
        } catch (NoTrackerException e) {
            Chat.msg(player, prefix+"&cA tracker by that ID cannot be found.");
            return;
        }

        if (applicationItem != null) {
            ItemStack itemStack = applicationItem.getItem();
            player.getInventory().addItem(itemStack);
            Chat.msg(player, prefix+plugin.getLangManager().getConfig().getStringList("tracker-given-personal"));
        }
    }

    @Subcommand("get|give")
    @CommandCompletion("@trackers @players")
    @Syntax("&7/it get &b<tracker> [amount] [player]")
    public void getApplicationItem(Player player, String trackerName, Player playerToGive) {
        String prefix = plugin.getLangManager().getConfig().getString("prefix");
        TrackerManager manager = TrackerManager.getInstance();

        ApplicationItem applicationItem = null;

        try {
            applicationItem = manager.getApplicationItem(trackerName);
        } catch (NoTrackerException e) {
            Chat.msg(player, prefix+"&cA tracker by that ID cannot be found.");
            return;
        }

        if (applicationItem != null) {
            ItemStack itemStack = applicationItem.getItem();
            playerToGive.getInventory().addItem(itemStack);
            Chat.msg(player, prefix+plugin.getLangManager().getConfig().getStringList("tracker-given-personal"));
        }
    }

    @Subcommand("get|give")
    @CommandCompletion("@trackers @nothing @players")
    @Syntax("&7/it get &b<tracker> [amount] [player]")
    public void getApplicationItem(Player player, String trackerName, Player playerToGive, int count) {
        String prefix = plugin.getLangManager().getConfig().getString("prefix");
        TrackerManager manager = TrackerManager.getInstance();

        ApplicationItem applicationItem = null;

        try {
            applicationItem = manager.getApplicationItem(trackerName);
        } catch (NoTrackerException e) {
            Chat.msg(player, prefix+"&cA tracker by that ID cannot be found.");
            return;
        }

        if (applicationItem != null) {
            ItemStack itemStack = applicationItem.getItem();
            itemStack.setAmount(count);
            playerToGive.getInventory().addItem(itemStack);
            Chat.msg(player, prefix+plugin.getLangManager().getConfig().getStringList("tracker-given-personal"));
        }
    }
}
