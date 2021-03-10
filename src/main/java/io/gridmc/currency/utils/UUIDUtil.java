package io.gridmc.currency.utils;

import java.util.UUID;

public class UUIDUtil {

    /**
     * Strip a uuid down and remove dashes.
     * @param uuid - uuid.
     * @return UUID
     */
    public static String strip(UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    /**
     * Build a uuid that has no dashes.
     * @param uuid - uuid.
     * @return UUID
     */
    public static UUID build(String uuid) {
        uuid = uuid.toLowerCase();
        StringBuilder builder = new StringBuilder();
        char[] array = uuid.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (i == 8 | i == 12 | i == 16 | i == 20) {
                builder.append("-");
            }
            builder.append(array[i]);
        }

        return UUID.fromString(builder.toString());
    }
}
