package systems.kscott.itemtrackers.tracker;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import systems.kscott.itemtrackers.util.Chat;

import java.util.List;

public class ApplicationItem {

    @Getter
    private String id;

    @Getter
    private String name;

    @Getter
    private List<String> lore;

    @Getter
    private Material material;

    public ApplicationItem(String id, String name, List<String> lore, Material material) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.material = material;
    }

    public ItemStack getItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(Chat.color(name));

        itemMeta.setLore(Chat.color(lore));

        itemStack.setItemMeta(itemMeta);

        NBTItem nbti = new NBTItem(itemStack);
        nbti.setString("tracker_id", id);
        itemStack = nbti.getItem();

        return itemStack;
    }

    public static boolean checkIfApplicationItem(ApplicationItem applicationItem, ItemStack item) {
        boolean materialEquals = false;
        boolean nameEquals = false;
        boolean loreEquals = false;

        ItemMeta meta = item.getItemMeta();

        return (item.getType() == applicationItem.material) && (meta.getDisplayName().equals(applicationItem.getName())) && (meta.getLore() == applicationItem.getLore());
    }
}
