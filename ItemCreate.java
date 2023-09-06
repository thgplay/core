package com.thgplugins.domination.core;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.tr7zw.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class ItemCreate {

    @Language("RegExp")
    private static final String COLOR_REGEX = "([&§](#[0-9a-fA-Fk-oK-O]{6}|[0-9a-fA-Fk-oK-O]))+";
    private static final Pattern COLOR_PATTERN = Pattern.compile(COLOR_REGEX);
    private static final NamespacedKey RANDOM_MODIFIER_KEY = new NamespacedKey("commons", "random_modifier");
    private static final LegacyComponentSerializer LEGACY_AMPERSAND_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
    private ItemStack item;
    private ItemMeta meta;

    public ItemCreate(@NotNull ItemStack item) {
        this.item = item;
    }

    public ItemCreate() {
        this.item = new ItemStack(Material.AIR, 1);
    }

    public ItemCreate(@NotNull Material material) {
        this.item = new ItemStack(material, 1);
    }

    private ItemCreate(@NotNull ItemStack item, @Nullable ItemMeta meta) {
        this.item = item;
        this.meta = meta;
    }

    @NotNull
    public static ItemCreate create(@NotNull Material material) {
        return new ItemCreate(material);
    }

    @NotNull
    public static ItemCreate create(@NotNull Material material, int amount) {
        return new ItemCreate(new ItemStack(material, amount));
    }

    @NotNull
    public static ItemCreate create(@NotNull ItemStack itemStack) {
        return new ItemCreate(itemStack);
    }



    @NotNull
    public ItemCreate setItem(@NotNull ItemStack itemStack) {
        this.item = itemStack;
        return this;
    }

    @NotNull
    public ItemCreate setMeta(@NotNull ItemMeta itemMeta) {
        this.meta = itemMeta;
        return this;
    }

    @NotNull
    public ItemStack getItem() {
        if (meta != null) {
            this.item.setItemMeta(meta);
        }

        AtomicReference<@NotNull ItemStack> itemStack = new AtomicReference<>(this.item.clone());
        return item.clone();
    }

    @NotNull
    public ItemStack getGlow() {
        return glow().getItem();
    }

    @NotNull
    public ItemCreate hideAttributes() {
        this.getItemMeta().addItemFlags(ItemFlag.values());
        return this;
    }
    @NotNull
    public ItemCreate glow() {
        ItemMeta meta = getItemMeta();

        meta.addEnchant(Enchantment.LUCK, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        return this;
    }

    @NotNull
    public ItemCreate type(@NotNull Material material) {
        this.item.setType(material);
        return this;
    }

    @NotNull
    public ItemCreate amount(int amount, boolean bound) {
        if (bound)
            amount = Math.max(0, Math.min(amount, item.getMaxStackSize()));
        this.item.setAmount(amount);
        return this;
    }

    @NotNull
    public ItemCreate amount(int amount) {
        return amount(amount, true);
    }


    @NotNull
    public ItemCreate name(@Nullable Component component) {
        this.getItemMeta().displayName(component);
        return this;
    }

    @NotNull
    public ItemCreate name(@Nullable String name) {
        this.getItemMeta().setDisplayName(name != null ? colorize(name) : null);
        return this;
    }

    @NotNull
    public static String colorize(@NotNull String string) {
        Matcher matcher = HEX_PATTERN.matcher(ChatColor.translateAlternateColorCodes('&', string));
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            try {
                matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return matcher.appendTail(buffer).toString();
    }

    @NotNull
    public ItemCreate lore() {
        this.getItemMeta().setLore(null);
        return this;
    }

    @NotNull
    public ItemCreate lore(@NotNull String @NotNull ... lore) {
        return lore(true, lore);
    }

    @NotNull
    public ItemCreate lore(boolean clear, @NotNull String @NotNull ... lore) {
        ItemMeta meta = getItemMeta();
        List<String> itemLore = new ArrayList<>();

        // If the item already has lore and we're not clearing lore, add it
        List<String> existingLore = meta.getLore();
        if (!clear && existingLore != null) {
            itemLore.addAll(existingLore);
        }

        for (String inputLine : lore) {
            if (!doesStringStartWithColorCode(inputLine)) {
                inputLine = "&7" + inputLine;
            }

            itemLore.add(colorize(inputLine));
        }

        meta.setLore(itemLore);
        return this;
    }

    @NotNull
    public ItemCreate addNbt(@NotNull String key, @Nullable Object value) {
        this.item.setItemMeta(getItemMeta());

        NBTItem nbtItem = new NBTItem(item);
        if (value == null) {
            nbtItem.removeKey(key);
        } else if (value instanceof String) {
            nbtItem.setString(key, value.toString());
        } else if (value instanceof Integer) {
            nbtItem.setInteger(key, (Integer) value);
        } else if (value instanceof Float) {
            nbtItem.setFloat(key, (Float) value);
        } else if (value instanceof Double) {
            nbtItem.setDouble(key, (Double) value);
        } else if (value instanceof Long) {
            nbtItem.setLong(key, (Long) value);
        } else if (value instanceof Short) {
            nbtItem.setShort(key, (Short) value);
        } else if (value instanceof UUID) {
            nbtItem.setUUID(key, (UUID) value);
        } else if (value instanceof Boolean) {
            nbtItem.setBoolean(key, (Boolean) value);
        } else if (value instanceof ItemStack) {
            nbtItem.setItemStack(key, (ItemStack) value);
            // TODO: Maybe array support?
        } else {
            nbtItem.setObject(key, value);
        }

        this.item = nbtItem.getItem();
        this.meta = null;

        return this;
    }


    /**
     * Create a new player skull {@link ItemStack} with the given texture.
     *
     * @param textureURL the texture URL
     * @return the ItemBuilder instance
     */
    @NotNull
    public static ItemCreate createCustomSkull(@NotNull String textureURL) {
        if (!textureURL.startsWith("https://textures.minecraft.net/texture/")) {
            textureURL = "https://textures.minecraft.net/texture/" + textureURL;
        }

        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        applyTextureURL(textureURL, item, meta);
        return new ItemCreate(item);
    }

    /**
     * Create a new player skull {@link ItemStack} with the given texture.
     *
     * @param textureURL the texture URL in bytes.
     * @return the ItemBuilder instance
     */
    @NotNull
    public static ItemCreate createCustomSkull(byte[] textureURL) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        applyTextureBytes(textureURL, item, meta);
        return new ItemCreate(item);
    }

    private static void applyTextureURL(@NotNull String textureURL, @NotNull ItemStack item, @NotNull SkullMeta meta) {
        byte[] encodedData = Base64.getEncoder().encode(
                String.format("{textures:{SKIN:{url:\"%s\"}}}", textureURL).getBytes());
        applyTextureBytes(encodedData, item, meta);
    }


    private static void applyTextureBytes(byte[] encodedData, @NotNull ItemStack item, @NotNull SkullMeta meta) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        item.setItemMeta(meta);
    }

    @NotNull
    @Deprecated
    public ItemCreate owner(@NotNull String name) {
        if (item == null || item.getType() != Material.PLAYER_HEAD) {
            this.type(Material.PLAYER_HEAD);
        }

        ((SkullMeta) getItemMeta()).setOwner(name);
        return this;
    }
    @NotNull
    public ItemCreate owner(@NotNull OfflinePlayer player) {
        if (item == null || item.getType() != Material.PLAYER_HEAD) {
            this.type(Material.PLAYER_HEAD);
        }

        SkullMeta meta = (SkullMeta) getItemMeta();
        meta.setOwningPlayer(player);

        return this;
    }

    @NotNull
    public ItemCreate lore(int index, @NotNull String line) {
        ItemMeta meta = getItemMeta();
        if (!meta.hasLore()) {
            return this;
        }

        List<String> itemLore = Objects.requireNonNull(meta).getLore();
        if (itemLore == null || index < 0 || index >= itemLore.size()) {
            return this;
        }

        if (line != null && !doesStringStartWithColorCode(line)) {
            line = "&7" + line;
        }

        itemLore.set(index, line != null ? colorize(line) : null);

        meta.setLore(itemLore);
        return this;
    }

    @NotNull
    public <T extends ItemMeta> ItemCreate meta(@NotNull Class<T> metaClass, @NotNull Consumer<T> metaApplier) {
        ItemMeta meta = getItemMeta();

        if (!metaClass.isInstance(meta)) {
            throw new IllegalArgumentException(
                    "Cannot apply meta of type " + metaClass.getName() + " to meta of type " + meta.getClass().getName());
        }

        metaApplier.accept(metaClass.cast(meta));

        return this;
    }

    @NotNull
    public ItemCreate model(int data) {
        try {
            this.getItemMeta().setCustomModelData((data != 0) ? data : null);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return this;
    }

    @NotNull
    private ItemMeta getItemMeta() {
        if (meta == null) {
            this.meta = item.getItemMeta();

            if (meta == null) {
                throw new IllegalStateException("Cannot operate on item if type is " + item.getType());
            }
        }

        return meta;
    }

    private boolean doesStringStartWithColorCode(@NotNull String string) {
        if (string.isEmpty()) {
            return false;
        }

        char firstChar = string.charAt(0);
        return firstChar == '&' || firstChar == ChatColor.COLOR_CHAR;
    }

}