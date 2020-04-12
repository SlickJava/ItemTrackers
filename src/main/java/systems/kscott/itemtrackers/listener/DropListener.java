package systems.kscott.itemtrackers.listener;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import systems.kscott.itemtrackers.ItemTrackers;
import systems.kscott.itemtrackers.exceptions.InvalidMaterialException;
import systems.kscott.itemtrackers.exceptions.NoTrackerException;
import systems.kscott.itemtrackers.exceptions.TrackerAlreadyExistsException;
import systems.kscott.itemtrackers.tracker.Tracker;
import systems.kscott.itemtrackers.tracker.TrackerManager;
import systems.kscott.itemtrackers.util.Chat;

public class DropListener implements Listener {

    private ItemTrackers plugin;

    public DropListener(ItemTrackers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void clickHandler(InventoryClickEvent event) {
        if (event.getClick() == ClickType.LEFT) {
            TrackerManager manager = TrackerManager.getInstance();

            ItemStack cursorItem = event.getCursor();
            ItemStack inventoryItem = event.getCurrentItem();

            if (cursorItem == null || inventoryItem == null) {
                return;
            }

            if (cursorItem.getType().equals(Material.AIR) || inventoryItem.getType().equals(Material.AIR)) {
                return;
            }

            NBTItem cursorNbti = new NBTItem(cursorItem);

            String trackerType = cursorNbti.getString("tracker_id");

            ItemStack applicationItem = null;

            try {
                applicationItem = manager.getApplicationItem(trackerType).getItem();
            } catch (NoTrackerException e) {
                return;
            }

            /* Don't send incompatible message if combining stacks of same item */
            ItemStack inventoryClone = inventoryItem.clone();
            ItemStack applicationClone = applicationItem.clone();

            inventoryClone.setAmount(1);
            applicationClone.setAmount(1);

            if (inventoryClone == applicationClone) {
                return;
            }

            Tracker tracker = null;

            try {
                tracker = manager.getTracker(trackerType);
            } catch (NoTrackerException e) {
                return;
            }

            String prefix = plugin.getLangManager().getConfig().getString("prefix");

            if (!tracker.isValidItem(inventoryItem)) {
                if (!cursorItem.getType().equals(Material.AIR)) {
                    Chat.msg(event.getWhoClicked(), prefix+plugin.getLangManager().getConfig().getString("tracker-not-compatible"));
                }

                return;
            }

            if (cursorItem.equals(applicationItem)) {
                if (plugin.getConfigManager().getConfig().getBoolean("permission-based-application")) {
                    if (!event.getWhoClicked().hasPermission("itemtrackers.apply."+trackerType)) {
                        Chat.msg(event.getWhoClicked(), prefix+plugin.getLangManager().getConfig().getString("tracker-no-permission"));
                        return;
                    }
                }
                ItemStack newItem = null;
                try {
                    newItem = tracker.addToItem(inventoryItem);
                } catch (TrackerAlreadyExistsException | InvalidMaterialException e) {
                    Chat.msg(event.getWhoClicked(), prefix+plugin.getLangManager().getConfig().getString("tracker-already-on-item"));
                    return;
                }

                event.setCurrentItem(newItem);
                event.getWhoClicked().setItemOnCursor(null);
                event.setCancelled(true);
                Chat.msg(event.getWhoClicked(), prefix+plugin.getLangManager().getConfig().getString("tracker-added").replace("%name%", trackerType));
                return;
            }
        }
    }
}
