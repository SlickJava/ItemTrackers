package systems.kscott.itemtrackers.tracker;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTCompoundList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import systems.kscott.itemtrackers.exceptions.InvalidMaterialException;
import systems.kscott.itemtrackers.exceptions.NoTrackerException;
import systems.kscott.itemtrackers.exceptions.TrackerAlreadyExistsException;
import systems.kscott.itemtrackers.util.Chat;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Tracker {

    @Getter
    private String id;

    @Getter
    private String nameFormat;

    @Getter
    private boolean displayName;

    @Getter
    private String loreFormat;

    @Getter
    private boolean displayLore;

    @Getter
    private Statistic statistic;

    @Getter
    @Nullable private String extraData;

    @Getter
    private IncrementType incrementType;

    @Getter
    private List<Material> supportedItems;

    public Tracker(String id, String nameFormat, boolean displayName, String loreFormat, boolean displayLore, String statistic, @Nullable String extraData, String incrementType, List<String> supportedItems) {
        this.id = id;
        this.nameFormat = nameFormat;
        this.displayName = displayName;
        this.loreFormat = loreFormat;
        this.displayLore = displayLore;

        this.statistic = Statistic.valueOf(statistic);
        if (extraData != null) {
            this.extraData = extraData;
        } else {
            this.extraData = null;
        }

        this.incrementType = IncrementType.valueOf(incrementType);

        List<Material> items = new ArrayList<>();

        for (String material : supportedItems) {
            items.add(Material.valueOf(material));
        }

        this.supportedItems = items;
    }

    public boolean isValidItem(ItemStack item) {
        boolean valid = false;
        for (Material material : supportedItems) {
            if (item.getType().equals(material)) {
                valid = true;
            }
        }
        return valid;
    }

    public ItemStack addToItem(ItemStack item) throws TrackerAlreadyExistsException, InvalidMaterialException {
        if (!supportedItems.contains(item.getType())) {
            throw new InvalidMaterialException();
        }
        NBTItem nbti = new NBTItem(item);

        if (!nbti.hasKey("item_trackers")) {
            nbti.setString("item_trackers", id);
        } else {
            String[] trackerNames = nbti.getString("item_trackers").split("\\|");

            for (String trackerName : trackerNames) {
                if (trackerName.equals(id)) {
                    throw new TrackerAlreadyExistsException();
                }
            }

            nbti.setString("item_trackers", nbti.getString("item_trackers")+"|"+id);
        }
        nbti.setInteger(id+"_count", 0);

        ItemStack newItem = nbti.getItem();
        ItemMeta meta = null;

        if (newItem.hasItemMeta()) {
            meta = newItem.getItemMeta();
        }

        if (meta == null) {
            Bukkit.getLogger().info("Meta is null");
            meta = Bukkit.getItemFactory().getItemMeta(newItem.getType());
        }

        if (displayName) {
            String currentName = "&r"+item.getI18NDisplayName();

            if (meta.hasDisplayName()) {
                currentName = meta.getDisplayName();
            }

            String newName = nameFormat.replace("%current_name%", currentName);

            newName = newName.replace("%number%", Integer.toString(nbti.getInteger(id+"_count")));

            newName = Chat.color(newName);

            meta.setDisplayName(newName);
        } else if (displayLore) {
            List<String> lore = null;
            if (meta.hasLore()) {
                lore = meta.getLore();
            }
            if (lore == null) {
                lore = new ArrayList<>();
            }

            String newLore = loreFormat.replace("%number%", Integer.toString(nbti.getInteger(id+"_count")));
            newLore = Chat.color(newLore);

            lore.add(newLore);

            meta.setLore(lore);
        }

        newItem.setItemMeta(meta);

        return newItem;
    }

    public ItemStack incrementTracker(ItemStack item) {
        NBTItem nbti = new NBTItem(item);

        int count = nbti.getInteger(id+"_count");

        nbti.setInteger(id+"_count", count+1);

        ItemStack newItem = nbti.getItem();

        ItemMeta meta = newItem.getItemMeta();

        if (displayName) {
            String displayWithoutName = Chat.color(nameFormat.replace("%current_name%", ""));
            displayWithoutName = displayWithoutName.replace("%number%", Integer.toString(count));

            String nameWithoutCount = meta.getDisplayName().replace(Chat.color(displayWithoutName), "");

            String formattedName = Chat.color(nameFormat.replace("%current_name%", nameWithoutCount).replace("%number%", Integer.toString(count+1)));

            meta.setDisplayName(formattedName);
        }

        if (displayLore) {
            String newLore = Chat.color(loreFormat.replace("%number%", Integer.toString(count)));
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                for (int i = 0; i < lore.size(); i++) {
                    String loreString = lore.get(i);

                    if (loreString.equals(newLore)) {
                        lore.set(i, Chat.color(loreFormat.replace("%number%", Integer.toString(count+1))));
                    }
                }

                meta.setLore(lore);
            }
        }

        newItem.setItemMeta(meta);
        return newItem;
    }
}
