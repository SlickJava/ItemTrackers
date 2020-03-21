package systems.kscott.itemtrackers.tracker;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Statistic;

import javax.annotation.Nullable;

public class TrackerItem {

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

    @Nullable private Material material;

    public TrackerItem(String id, String nameFormat, boolean displayName, String loreFormat, boolean displayLore, String statistic, @Nullable String material) {
        this.id = id;
        this.nameFormat = nameFormat;
        this.displayName = displayName;
        this.loreFormat = loreFormat;
        this.displayLore = displayLore;

        this.statistic = Statistic.valueOf("statistic");
        if (material != null) {
            this.material = Material.valueOf(material);
        } else {
            this.material = null;
        }
    }
}
