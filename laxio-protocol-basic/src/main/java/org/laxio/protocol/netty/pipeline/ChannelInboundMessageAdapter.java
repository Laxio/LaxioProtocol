package org.laxio.protocol.netty.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class ChannelInboundMessageAdapter<T> extends ChannelInboundHandlerAdapter {

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);

        try {
            T cast = (T) msg;
            onMessage(ctx, cast);
        } catch (Exception ex) {
            exceptionCaught(ctx, ex);
        }
    }

    public abstract void onMessage(ChannelHandlerContext ctx, T msg) throws Exception;

}
