package com.foxtailgames.pocketrunner.utilities;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by rdelfin on 1/6/15.
 */
public class Util {
    public static UUID UUIDFromByteArray(byte[] arr) {
        ByteBuffer bb = ByteBuffer.wrap(arr);
        return new UUID(bb.getLong(), bb.getLong());
    }

    public static byte[] byteArrayFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
