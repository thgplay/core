package com.thgplugins.domination.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class handling the serialization and deserialization of {@link Location}.
 */
public class UtilLocation {

    private UtilLocation() { }

    /**
     * Deserialize a {@link Location} from the given string.
     *
     * @param string the string to deserialize
     *
     * @return the deserialized location
     */
    @Nullable
    public static Location unserializer(@Nullable String string) {
        try {
            if (string == null || string.isEmpty()) return null;
            String[] split = string.split(":");
            World world = Bukkit.getWorld(split[0]);
            assert world != null;
            double x = Double.parseDouble(split[1]);
            double y = Double.parseDouble(split[2]);
            double z = Double.parseDouble(split[3]);
            float yaw = 0, pitch = 0;
            if (split.length > 4) {
                yaw = Float.parseFloat(split[4]);
                pitch = Float.parseFloat(split[5]);
            }
            return new Location(world, x, y, z, yaw, pitch);
        }catch (Exception ex){
            return null;
        }
    }

    /**
     * Serialize a {@link Location} as a string.
     *
     * @param location the location to serialize
     * @param decimal true to include decimals, false if values should be floored
     * @param direction true to include pitch and yaw, false to omit them
     *
     * @return the serialized string
     */
    @Nullable
    public static String serializer(@Nullable Location location, boolean decimal, boolean direction) {
        if (location == null || location.getWorld() == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(location.getWorld().getName().trim()).append(":");
        sb.append(decimal ? location.getX() : location.getBlockX()).append(":");
        sb.append(decimal ? location.getY() : location.getBlockY()).append(":");
        sb.append(decimal ? location.getZ() : location.getBlockZ());
        if (direction)
            sb.append(":").append(location.getYaw()).append(":").append(location.getPitch());
        return sb.toString();
    }

}
