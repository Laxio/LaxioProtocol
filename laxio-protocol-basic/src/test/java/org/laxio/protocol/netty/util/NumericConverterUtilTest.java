package org.laxio.protocol.netty.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumericConverterUtilTest {

    @Test
    void toUnsignedByte() {
        assertEquals(-31769, NumericConverterUtil.toSignedShort(33767));
    }

}
