package org.laxio.protocol.netty.stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LaxioByteBufTest {
    
    private static final String NO_MORE_CONTENT = "No more content on the stream";

    @Test
    void bufBasic() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        int write = 247;
        lbf.write(write);

        int value = lbf.read();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufByte() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        byte write = 123;
        lbf.writeByte(write);

        byte value = lbf.readByte();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufBytes() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        byte[] write = new byte[]{1, 123, 4, 8};
        lbf.writeBytes(write);

        byte[] value = lbf.readBytes(write.length);
        assertArrayEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufUnsignedByte() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        int write = 247;
        lbf.writeUnsignedByte(write);

        int value = lbf.readUnsignedByte();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufUnsignedShort() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        int write = Short.MAX_VALUE + 1000;
        lbf.writeUnsignedShort(write);

        int value = lbf.readUnsignedShort();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufFloat() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        float write = 0.786F;
        lbf.writeFloat(write);

        float value = lbf.readFloat();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufDouble() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        double write = 0.786D;
        lbf.writeDouble(write);

        double value = lbf.readDouble();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufShort() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        short write = 16837;
        lbf.writeShort(write);

        short value = lbf.readShort();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufInt() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        int write = Integer.MAX_VALUE - 1;
        lbf.writeInt(write);

        int value = lbf.readInt();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufLong() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        long write = Long.MAX_VALUE - 1;
        lbf.writeLong(write);

        long value = lbf.readLong();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufBoolean() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        lbf.writeBoolean(false);
        assertFalse(lbf.readBoolean());

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);

        lbf.writeBoolean(true);
        assertTrue(lbf.readBoolean());

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufUUID() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        UUID write = UUID.randomUUID();
        lbf.writeUUID(write);

        UUID value = lbf.readUUID();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufDashedUUID() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        UUID write = UUID.randomUUID();
        lbf.writeDashedUUID(write);

        UUID value = lbf.readDashedUUID();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufString() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        String write = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456780";
        lbf.writeString(write);

        String value = lbf.readString();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufStringContent() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        Charset utf8 = Charset.forName("UTF-8");
        String write = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456780";
        byte[] content = write.getBytes(utf8);
        lbf.writeStringContent(write);

        String value = lbf.readString(content.length);
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

    @Test
    void bufVarInt() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        int write = 17612;
        lbf.writeVarInt(write);

        int value = lbf.readVarInt();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);

        ByteBuf buf1 = Unpooled.copiedBuffer(new byte[]{16, 0, 0});
        LaxioByteBuf lbf1 = new LaxioByteBuf(buf1);
        int readValue = lbf.readVarInt();
        System.out.println("Read Value: " + readValue);
    }

    @Test
    void bufVarLong() throws IOException {
        ByteBuf buf = Unpooled.buffer(0);
        LaxioByteBuf lbf = new LaxioByteBuf(buf);

        long write = 17612342423L;
        lbf.writeVarLong(write);

        long value = lbf.readVarLong();
        assertEquals(write, value);

        assertThrows(IndexOutOfBoundsException.class, lbf::read, NO_MORE_CONTENT);
    }

}
