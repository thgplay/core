package com.thgplugins.domination.core;

import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * A utility class to convert items to and from Base64 and other supported formats, as
 * well as other general item utilities.
 */
public final class UtilItem {

    private UtilItem() {
    }

    /**
     * Convert the given {@link ItemStack ItemStacks} to a Base64 string.
     *
     * @param contents the contents to convert
     * @return the serialized Base64 string
     */
    @NotNull
    public static String toBase64(final @Nullable ItemStack... contents) {
        try {
            if (contents == null || contents.length == 0) return "";
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(contents.length);
            for (final ItemStack stack : contents) {
                dataOutput.writeObject(stack);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Convert the given string to an array of {@link ItemStack ItemStacks}.
     *
     * @param data the string from which to parse items
     * @return the parsed ItemStacks
     */
    @Nullable
    @SneakyThrows
    public static ItemStack[] stacksFromBase64(final @Nullable String data) {
        if (data == null || data.isEmpty()) return null;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        final ItemStack[] stacks = new ItemStack[dataInput.readInt()];
        for (int i = 0; i < stacks.length; ++i) {
            stacks[i] = (ItemStack) dataInput.readObject();
        }
        dataInput.close();
        return stacks;
    }

    /**
     * Convert the given String to an {@link ItemStack}.
     *
     * @param data the string from which to parse an item
     * @return the parsed ItemStack
     */
    @Nullable
    @SneakyThrows
    public static ItemStack stackFromBase64(final @Nullable String data) {
        if (data == null || data.isEmpty()) return null;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        dataInput.readInt();
        final ItemStack stacks = (ItemStack) dataInput.readObject();
        dataInput.close();
        return stacks;
    }

}
