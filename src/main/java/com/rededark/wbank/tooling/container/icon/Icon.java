package com.rededark.wbank.tooling.container.icon;

import com.rededark.wbank.tooling.container.Container;
import com.rededark.wbank.tooling.container.holder.ContainerHolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Icon {

    public static Builder of(int position) {
        return new Builder(position);
    }

    private int position;

    private final Supplier<ItemStack> current;
    private final BiConsumer<Icon, InventoryClickEvent> consumer;

    protected Icon(int position, Supplier<ItemStack> stack, BiConsumer<Icon, InventoryClickEvent> event) {
        this.position = position; this.current = stack; this.consumer = event;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public BiConsumer<Icon, InventoryClickEvent> getConsumer() {
        return consumer;
    }

    public Supplier<ItemStack> getCurrent() {
        return current;
    }

    public void update(Inventory inventory) {
        inventory.setItem(position, current.get());
    }

    public static class Builder {

        private int position;
        private BiConsumer<Icon, InventoryClickEvent> consumer;
        private Supplier<ItemStack> itemStack;

        public Builder(int position) {
            this.position = position;
        }

        public Builder position(int position) {
            this.position = position;
            return this;
        }

        public Builder handle(BiConsumer<Icon, InventoryClickEvent> consumer) {
            this.consumer = consumer;
            return this;
        }

        public Builder stack(Supplier<ItemStack> itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public Icon build(Container container) {
            final Icon icon = new Icon(position, itemStack, consumer);

            final ContainerHolder holder = container.getHolder();
            final Inventory inventory = holder.getInventory();

            container.put(position, icon);

            inventory.setItem(position, itemStack.get());

            return icon;
        }

    }

}
