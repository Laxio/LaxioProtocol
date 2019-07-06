package org.laxio.protocol.netty.util;

public final class NumericConverterUtil {

    private NumericConverterUtil() {
        // private constructor
    }

    public static short toSignedShort(int value) {
        return (short) ((value << 16) >>> 16);
    }

}
