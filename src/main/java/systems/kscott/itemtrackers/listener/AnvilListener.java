package systems.kscott.itemtrackers.listener;
;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import systems.kscott.itemtrackers.ItemTrackers;

import java.util.Objects;

public class AnvilListener implements Listener {

    private ItemTrackers plugin;

    public AnvilListener(ItemTrackers plugin) {
        this.plugin = plugin;
    }

    //@EventHandler
    public void onAnvilRename(PrepareAnvilEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL) {
            if (Objects.isNull(event.getInventory().getItem(0))) {
                return;
            }

            if (event.getInventory().getItem(0).getType().equals(Material.AIR)) {
                return;
            }
            NBTItem nbtItem = new NBTItem(event.getInventory().getItem(0));
            if (nbtItem.hasKey("tracker_id")) {
                event.setResult(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void anvilRename(InventoryClickEvent event) {
        if (plugin.getConfigManager().getConfig().getBoolean("disable-application-item-rename")) {
            if (event.getInventory().getType().equals(InventoryType.ANVIL)) {
                if (event.getSlotType().equals(InventoryType.SlotType.RESULT)) {

                    ItemStack firstItem = event.getInventory().getItem(0);

                    if (Objects.isNull(firstItem) || firstItem.getType().equals(Material.AIR)) {
                        return;
                    }

                    NBTItem nbtItem = new NBTItem(firstItem);

                    if (nbtItem.hasKey("tracker_id")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
