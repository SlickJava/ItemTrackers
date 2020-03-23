package systems.kscott.itemtrackers.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;
import systems.kscott.itemtrackers.exceptions.NoTrackerException;
import systems.kscott.itemtrackers.tracker.Tracker;
import systems.kscott.itemtrackers.tracker.TrackerManager;

public class StatisticListener implements Listener {

    @EventHandler
    public void statisticHandler(PlayerStatisticIncrementEvent event) {
        Player player = event.getPlayer();
        Statistic statistic = event.getStatistic();
        ItemStack item = player.getInventory().getItemInMainHand();
        int index = player.getInventory().getHeldItemSlot();
        TrackerManager manager = TrackerManager.getInstance();

        ItemStack newItem = item.clone();

        NBTItem nbti = new NBTItem(item);

        String trackerString = nbti.getString("item_trackers");

        String[] trackerStrings = trackerString.split("\\|");

        for (String trackerString2 : trackerStrings) {
            Tracker tracker = null;

            try {
                tracker = manager.getTracker(trackerString2);
            } catch (NoTrackerException e) {
                continue;
            }

            if (statistic == tracker.getStatistic()) {
                /* Special cases */
                if (statistic == Statistic.MINE_BLOCK) {
                    Material material = event.getMaterial();

                    if (tracker.getExtraData().equals("*")) {
                        newItem = tracker.incrementTracker(newItem);
                    } else if (Material.valueOf(tracker.getExtraData()) == material) {
                        newItem = tracker.incrementTracker(newItem);
                    }

                } else if (statistic == Statistic.KILL_ENTITY) {
                    EntityType entityType = event.getEntityType();

                    if (tracker.getExtraData().equals("*")) {
                        newItem = tracker.incrementTracker(newItem);
                    } else if (EntityType.valueOf(tracker.getExtraData()) == entityType) {
                        newItem = tracker.incrementTracker(newItem);
                    }

                } else {
                    newItem = tracker.incrementTracker(newItem);
                }
                player.getInventory().setItem(index, newItem);
            }
        }
    }
}
