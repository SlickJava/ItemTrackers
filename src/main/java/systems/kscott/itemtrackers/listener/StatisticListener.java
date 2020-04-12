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

    private TrackerManager trackerManager;

    public StatisticListener() {
        trackerManager = TrackerManager.getInstance();
    }

    @EventHandler
    public void statisticHandler(PlayerStatisticIncrementEvent event) {
        Player player = event.getPlayer();

        /* Inventory trackers */
        for (int i = 0; i <= player.getInventory().getContents().length; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (!(player.getInventory().getItem(i) == null)) {
                if (!player.getInventory().getItem(i).equals(Material.AIR)) {
                    updateTrackers(event, i, player.getInventory().getItem(i));
                }
            }
        }
    }

    private void updateTrackers(PlayerStatisticIncrementEvent event, int index, ItemStack item) {
        Statistic statistic = event.getStatistic();
        Player player = event.getPlayer();

        ItemStack newItem = item.clone();

        NBTItem nbti = new NBTItem(item);

        String trackerString = nbti.getString("item_trackers");

        String[] trackerStrings = trackerString.split("\\|");

        for (String trackerString2 : trackerStrings) {
            Tracker tracker = null;

            try {
                tracker = trackerManager.getTracker(trackerString2);
            } catch (NoTrackerException e) {
                continue;
            }

            if (tracker.isRequiresHeld()) {
                if (player.getInventory().getHeldItemSlot() != index) {
                    continue;
                }
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
                } else if (statistic == Statistic.ANIMALS_BRED) {
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
