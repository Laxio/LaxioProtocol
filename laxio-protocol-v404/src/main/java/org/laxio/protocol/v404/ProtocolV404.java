package org.laxio.protocol.v404;

import org.laxio.Application;
import org.laxio.protocol.PacketOrigin;
import org.laxio.protocol.ProtocolState;
import org.laxio.protocol.netty.packet.server.login.LoginServerDisconnectPacket;
import org.laxio.protocol.netty.protocol.StatefulProtocol;
import org.laxio.protocol.v404.listener.LoginListener;
import org.laxio.protocol.v404.packet.client.login.LoginClientEncryptionResponse;
import org.laxio.protocol.v404.packet.client.login.LoginClientLoginStartPacket;
import org.laxio.protocol.v404.packet.server.login.LoginServerEncryptionRequest;
import org.laxio.protocol.v404.packet.server.login.LoginServerLoginSuccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolV404 extends StatefulProtocol {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolV404.class);

    public static final int VERSION = 404;

    public ProtocolV404() {
        super(VERSION);
    }

    @Override
    protected void register() {
        super.register();
        register(PacketOrigin.SERVER, ProtocolState.LOGIN, 0x00, LoginServerDisconnectPacket.class);
        register(PacketOrigin.SERVER, ProtocolState.LOGIN, 0x01, LoginServerEncryptionRequest.class);
        register(PacketOrigin.SERVER, ProtocolState.LOGIN, 0x02, LoginServerLoginSuccess.class);
        // register(PacketOrigin.SERVER, ProtocolState.LOGIN, 0x03, LoginServerDisconnectPacket.class);
        // register(PacketOrigin.SERVER, ProtocolState.LOGIN, 0x04, LoginServerDisconnectPacket.class);

        register(PacketOrigin.CLIENT, ProtocolState.LOGIN, 0x00, LoginClientLoginStartPacket.class);
        register(PacketOrigin.CLIENT, ProtocolState.LOGIN, 0x01, LoginClientEncryptionResponse.class);
        // register(PacketOrigin.CLIENT, ProtocolState.LOGIN, 0x02, LoginServerDisconnectPacket.class);
    }

    @Override
    public void onProtocolEnable(Application application) {
        application.getListenerManager().register(this, new LoginListener(application));
    }

    @Override
    public void onProtocolDisable(Application application) {
        application.getListenerManager().unregister(this);
    }

}
