package org.laxio.protocol.netty;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class ProtocolChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
        } catch (ChannelException ex) {
            // TODO: check if this can be ignored
        }

        // stage 1: input - timeout
        // disconnect after 30 secs of no io
        channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30));
    }

}
