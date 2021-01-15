package com.rededark.wbank.tooling.item;

import com.rededark.wbank.tooling.util.Try;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material, int amount, int data) {
        this(new ItemStack(material, amount, (short) data));
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, 0);
    }

    public ItemBuilder(Material material) {
        this(material, 1, 0);
    }

    public ItemBuilder setMeta(Consumer<ItemMeta> consumer) {
        final ItemMeta meta = itemStack.getItemMeta();

        consumer.accept(meta);

        itemStack.setItemMeta(meta); return this;
    }

    public ItemBuilder name(String name) {
        return setMeta(meta -> meta.setDisplayName(name));
    }

    public ItemBuilder lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder lore (List<String> lore) {
        return setMeta(meta -> meta.setLore(lore));
    }

    public ItemBuilder texture(String target) {
        return setMeta(meta -> {
            final SkullMeta skullMeta = (SkullMeta) meta;

            final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);

            gameProfile.getProperties().put("textures", new Property("textures", target));

            Try.of(() -> {
                final Field field = skullMeta.getClass().getDeclaredField("profile");

                field.setAccessible(true);

                field.set(skullMeta, gameProfile);
            });
        });
    }

    public ItemBuilder head(OfflinePlayer player) {
        return setMeta(meta -> ((SkullMeta) meta).setOwner(player.getName()));
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return setMeta(meta -> meta.addEnchant(enchantment, level, true));
    }

    public ItemStack build() {
        return itemStack;
    }

}
