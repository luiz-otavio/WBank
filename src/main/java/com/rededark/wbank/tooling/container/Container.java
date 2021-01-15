package com.rededark.wbank.tooling.container;

import com.rededark.wbank.tooling.container.holder.ContainerHolder;
import com.rededark.wbank.tooling.container.icon.Icon;
import com.rededark.wbank.tooling.container.size.Size;
import org.bukkit.entity.Player;

public abstract class Container {

    private final ContainerHolder holder;

    private final String name;
    private final Size size;

    private final Icon[] icons;

    public Container(String name, Size size) {
        this.name = name;
        this.size = size;
        holder = new ContainerHolder(this);
        this.icons = new Icon[size.getAmount()];
    }

    public void open(Player player) {
        player.openInventory(holder.getInventory());
    }

    public void update() {
        for (Icon icon : icons) {
            if(icon != null) icon.update(holder.getInventory());
        }
    }

    public ContainerHolder getHolder() {
        return holder;
    }

    public String getName() {
        return name;
    }

    public Size getSize() {
        return size;
    }

    public void put(int slot, Icon icon) {
        if(slot < icons.length) icons[slot] = icon;
    }

    public Icon with(int slot) {
        return slot < icons.length ? icons[slot] : null;
    }
}
