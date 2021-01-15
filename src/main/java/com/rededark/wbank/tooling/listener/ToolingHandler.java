package com.rededark.wbank.tooling.listener;

import com.rededark.wbank.tooling.container.Container;
import com.rededark.wbank.tooling.container.holder.ContainerHolder;
import com.rededark.wbank.tooling.container.icon.Icon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ToolingHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onClick(InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        if(event.getSlotType() != InventoryType.SlotType.CONTAINER) return;

        final InventoryHolder holder = inventory.getHolder();

        if(!(holder instanceof ContainerHolder)) return;

        event.setCancelled(true);

        final ContainerHolder containerHolder = (ContainerHolder) holder;

        final Container container = containerHolder.getContainer();
        final Icon icon = container.with(event.getRawSlot());

        if(icon != null && icon.getConsumer() != null) icon.getConsumer().accept(icon, event);

        container.update();
    }
}
