package org.laxio.protocol.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.laxio.LaxioApplication;
import org.laxio.network.NetworkServer;
import org.laxio.protocol.netty.ProtocolChannelHandler;
import org.laxio.thread.LaxioThread;
import org.laxio.thread.LaxioThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

public class LaxioServerNettyServer extends LaxioThread implements NetworkServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LaxioServerNettyServer.class);

    private final InetSocketAddress bindAddress;

    private EventLoopGroup boss;
    private EventLoopGroup worker;

    public LaxioServerNettyServer(LaxioApplication application, InetSocketAddress bindAddress) {
        super(application);
        this.bindAddress = bindAddress;
    }

    public InetSocketAddress getBindAddress() {
        return bindAddress;
    }

    public boolean isRunning() {
        return false;
    }

    public void shutdown() {
        //
    }

    @Override
    public void run() {
        synchronized (bindAddress) {
            try {
                ThreadFactory bossFactory = new LaxioThreadFactory(getApplication(), "BOSS");
                ThreadFactory workerFactory = new LaxioThreadFactory(getApplication(), "WORKER");

                boss = new NioEventLoopGroup(0, bossFactory);
                worker = new NioEventLoopGroup(0, workerFactory);
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(boss, worker)
                         .channel(NioServerSocketChannel.class)
                         .childHandler(new ProtocolChannelHandler(getApplication()));

                bootstrap.bind(bindAddress).sync();
                System.out.println("Bound!");
            } catch (Exception ex) {
                LOGGER.error("Unable to bind server", ex);
            }
        }
    }

}
