package org.laxio.protocol.netty;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.laxio.LaxioApplication;
import org.laxio.protocol.netty.pipeline.PacketDecoder;
import org.laxio.protocol.netty.pipeline.PacketDeflator;
import org.laxio.protocol.netty.pipeline.PacketEncoder;
import org.laxio.protocol.netty.pipeline.PacketInflater;
import org.laxio.protocol.netty.pipeline.PacketPrepender;
import org.laxio.protocol.netty.pipeline.PacketSplitter;
import org.laxio.protocol.netty.server.LaxioServerNettyClient;

public class ProtocolChannelHandler extends ChannelInitializer<SocketChannel> {

    private final LaxioApplication application;

    public ProtocolChannelHandler(LaxioApplication application) {
        this.application = application;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
        } catch (ChannelException ex) {
            // TODO: check if this can be ignored
        }

        LaxioServerNettyClient connection = new LaxioServerNettyClient(application);

        //
        // ========================
        //
        //         INBOUND
        //
        // ========================
        //

        // stage 1: input - timeout
        // disconnect after 30 secs of no io
        channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30));

        // stage 2: input - splitter
        // reads the length of the packet and stores it in a bytebuf
        channel.pipeline().addLast("splitter", new PacketSplitter());

        // stage 3: input - inflater
        // inflates the buffer if compression is enabled
        channel.pipeline().addLast("inflater", new PacketInflater(connection));

        // stage 4: input - decoder
        // decodes the data into a readable packet
        channel.pipeline().addLast("decoder", new PacketDecoder(connection));

        // stage 6: input - handle
        // handle the native packet
        channel.pipeline().addLast("client", connection);

        //
        // ========================
        //
        //         OUTBOUND
        //
        // ========================
        //

        // stage 4: output - deflater
        // deflater - compresses the packet
        channel.pipeline().addLast("deflater", new PacketDeflator(connection));

        // stage 3: output - prepender
        // prepender - prepends packet length onto bytes before sending
        channel.pipeline().addLast("prepender", new PacketPrepender());

        // stage 2: output - encode
        // encodes the data into a client readable
        channel.pipeline().addLast("encoder", new PacketEncoder(connection));
    }

}
