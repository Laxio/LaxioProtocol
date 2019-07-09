package org.laxio.protocol.netty.packet;

import org.laxio.network.connection.Connection;
import org.laxio.packet.Packet;

public abstract class AbstractPacket implements Packet {

    private Connection connection;

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
