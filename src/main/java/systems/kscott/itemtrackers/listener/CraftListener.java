package systems.kscott.itemtrackers.listener;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import systems.kscott.itemtrackers.ItemTrackers;
import systems.kscott.itemtrackers.tracker.ApplicationItem;
import systems.kscott.itemtrackers.tracker.TrackerManager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CraftListener implements Listener {

    private ItemTrackers plugin;

    public CraftListener(ItemTrackers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCraft(PrepareItemCraftEvent event) {
        if (plugin.getConfigManager().getConfig().getBoolean("disable-application-item-craft")) {
            ItemStack[] items = event.getInventory().getMatrix();

            for (ApplicationItem applicationItem : TrackerManager.getInstance().getApplicationItems()) {
                for (ItemStack item : items) {
                    if (Objects.isNull(item) || item.getType().equals(Material.AIR)) {
                        continue;
                    }
                    ItemStack clone = item.clone();
                    clone.setAmount(1);
                    if (clone.equals(applicationItem.getItem())) {
                        event.getInventory().setResult(new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

}
