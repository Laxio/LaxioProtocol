package org.laxio.protocol.netty;

import org.laxio.LaxioApplication;
import org.laxio.protocol.netty.server.LaxioServerNettyServer;

import java.net.InetSocketAddress;

public class TestNettyServer {

    public static void main(String[] args) {
        LaxioApplication testApplication = new TestLaxioApplication("test-app");
        InetSocketAddress bindAddress = new InetSocketAddress("0.0.0.0", 25565);
        LaxioServerNettyServer server = new LaxioServerNettyServer(testApplication, bindAddress);
        server.start();
    }

}
