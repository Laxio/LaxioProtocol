package org.laxio.protocol.netty.stream;

import io.netty.buffer.ByteBuf;
import org.laxio.network.stream.LaxioInput;
import org.laxio.network.stream.LaxioOutput;
import org.laxio.protocol.netty.util.NumericConverterUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

public class LaxioByteBuf implements LaxioInput, LaxioOutput {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final ByteBuf buf;

    public LaxioByteBuf(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public short read() {
        return (short) Byte.toUnsignedInt(buf.readByte());
    }

    @Override
    public void write(int value) {
        buf.writeByte(value);
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @Override
    public void writeByte(byte value) {
        buf.writeByte(value);
    }

    @Override
    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return bytes;
    }

    @Override
    public void writeBytes(byte[] value) {
        buf.writeBytes(value);
    }

    @Override
    public void writeUnsignedByte(int value) {
        buf.writeByte(value);
    }

    @Override
    public int readUnsignedShort() {
        return buf.readUnsignedShort();
    }

    @Override
    public void writeUnsignedShort(int value) {
        buf.writeShort(NumericConverterUtil.toSignedShort(value));
    }

    @Override
    public float readFloat() {
        return buf.readFloat();
    }

    @Override
    public void writeFloat(float value) {
        buf.writeFloat(value);
    }

    @Override
    public double readDouble() {
        return buf.readDouble();
    }

    @Override
    public void writeDouble(double value) {
        buf.writeDouble(value);
    }

    @Override
    public short readShort() {
        return buf.readShort();
    }

    @Override
    public void writeShort(short value) {
        buf.writeShort(value);
    }

    @Override
    public int readInt() {
        return buf.readInt();
    }

    @Override
    public void writeInt(int value) {
        buf.writeInt(value);
    }

    @Override
    public long readLong() {
        return buf.readLong();
    }

    @Override
    public void writeLong(long value) {
        buf.writeLong(value);
    }

    @Override
    public boolean readBoolean() {
        return buf.readBoolean();
    }

    @Override
    public void writeBoolean(boolean value) {
        buf.writeBoolean(value);
    }

    @Override
    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    @Override
    public void writeUUID(UUID value) {
        writeLong(value.getMostSignificantBits());
        writeLong(value.getLeastSignificantBits());
    }

    @Override
    public UUID readDashedUUID() throws IOException {
        return UUID.fromString(readString());
    }

    @Override
    public void writeDashedUUID(UUID value) throws IOException {
        writeString(value.toString());
    }

    @Override
    public String readString() throws IOException {
        return readString(readVarInt());
    }

    @Override
    public void writeString(String value) throws IOException {
        byte[] content = value.getBytes(UTF_8);
        writeVarInt(content.length);
        writeBytes(content);
    }

    @Override
    public String readString(int length) {
        byte[] content = readBytes(length);
        return new String(content, UTF_8);
    }

    @Override
    public void writeStringContent(String value) {
        writeBytes(value.getBytes(UTF_8));
    }

    private long readVar(int maxWidth, String ioError) throws IOException {
        int numRead = 0;
        long result = 0;
        byte read;
        do {
            read = readByte();
            long value = (read & 0b01111111);
            result |= (value << (7 * numRead));

            numRead++;
            if (numRead > maxWidth)
                throw new IOException(ioError);
        } while ((read & 0b10000000) != 0);

        return result;
    }

    private void writeVar(long data) {
        long val = data;
        do {
            byte temp = (byte) (val & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            val >>>= 7;
            if (val != 0) {
                temp |= 0b10000000;
            }

            writeByte(temp);
        } while (val != 0);
    }

    @Override
    public int readVarInt() throws IOException {
        return (int) readVar(5, "VarInt is too big");
    }

    @Override
    public void writeVarInt(int value) {
        writeVar(value);
    }

    @Override
    public long readVarLong() throws IOException {
        return readVar(10, "VarLong is too big");
    }

    @Override
    public void writeVarLong(long value) {
        writeVar(value);
    }

    @Override
    public int readableBytes() {
        return buf.readableBytes();
    }

}
