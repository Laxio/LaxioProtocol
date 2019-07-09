package org.laxio.protocol.netty.authentication;

import org.laxio.authentication.ConnectionProfile;
import org.laxio.network.connection.Connection;

public class BasicConnectionProfile implements ConnectionProfile {

    private final Connection connection;
    private final String username;

    public BasicConnectionProfile(Connection connection, String username) {
        this.connection = connection;
        this.username = username;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean login() {
        return false;
    }

}
