package org.laxio.protocol.netty.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.laxio.network.connection.Connection;
import org.laxio.protocol.netty.stream.LaxioByteBuf;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PacketInflater extends ByteToMessageDecoder {

    private final Connection connection;

    public PacketInflater(Connection connection) {
        this.connection = connection;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws IOException, DataFormatException {
        if (byteBuf.readableBytes() > 0) {
            LaxioByteBuf buffer = new LaxioByteBuf(byteBuf);
            if (connection.getCompression().isEnabled()) {
                int dlen = buffer.readVarInt(); // length of uncompressed data
                if (dlen >= connection.getCompression().getThreshold()) {
                    byte[] input = byteBuf.array();   // Converts buffer to an array of bytes
                    Inflater inflater = new Inflater();       // Creates a new inflater
                    inflater.setInput(input);                 // Sets the input of the inflater to the supplied bytes

                    byte[] output = new byte[dlen];           // Byte array the size of the uncompressed data
                    int resultLen = inflater.inflate(output); // Inflates the input into the output
                    inflater.end();                           // Closes the inflater

                    // Trim any extra bytes off the end
                    byte[] trim = new byte[resultLen];
                    System.arraycopy(output, 0, trim, 0, trim.length);


                    list.add(Unpooled.copiedBuffer(trim));
                    return;
                }
            }

            list.add(byteBuf.readBytes(byteBuf.readableBytes()));
        }
    }

}
