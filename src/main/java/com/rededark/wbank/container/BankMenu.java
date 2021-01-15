package com.rededark.wbank.container;

import com.rededark.wbank.adapter.item.ItemAdapter;
import com.rededark.wbank.adapter.text.TextAdapter;
import com.rededark.wbank.tooling.container.Container;
import com.rededark.wbank.tooling.container.icon.Icon;
import com.rededark.wbank.tooling.container.size.Size;
import org.bukkit.configuration.ConfigurationSection;

public abstract class BankMenu extends Container {

    protected static final int[] CONTENT = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    }, FILL = {
            0, 1, 2, 3, 4, 5, 6, 7,
            8, 9,
            17, 18,
            26, 27,
            35, 36,
            37, 38, 39, 40, 41, 42, 43, 44,
    };


    public BankMenu(String name, Size size) {
        super(name, size);

        final ConfigurationSection section = TextAdapter.getSection("Container.Fill");

        for (int slot : FILL) {
            Icon.of(slot)
                    .stack(() -> ItemAdapter.toItem(section))
                    .build(this);
        }
    }
}
