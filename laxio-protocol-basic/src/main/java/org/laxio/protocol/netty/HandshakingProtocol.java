package org.laxio.protocol.netty;

import org.laxio.protocol.PacketOrigin;
import org.laxio.protocol.ProtocolState;
import org.laxio.protocol.netty.packet.client.handshake.HandshakeClientHandshakePacket;
import org.laxio.protocol.netty.packet.client.status.StatusClientPingPacket;
import org.laxio.protocol.netty.packet.client.status.StatusClientRequestPacket;
import org.laxio.protocol.netty.packet.server.login.LoginServerDisconnectPacket;
import org.laxio.protocol.netty.packet.server.status.StatusServerPongPacket;
import org.laxio.protocol.netty.packet.server.status.StatusServerResponsePacket;
import org.laxio.protocol.netty.protocol.StatefulProtocol;

public class HandshakingProtocol extends StatefulProtocol {

    public static final HandshakingProtocol INSTANCE = new HandshakingProtocol();

    private HandshakingProtocol() {
        // handshaking protocol has no version
        super(0);
    }

    @Override
    protected void register() {
        register(PacketOrigin.CLIENT, ProtocolState.HANDSHAKE, 0x00, HandshakeClientHandshakePacket.class);

        register(PacketOrigin.CLIENT, ProtocolState.STATUS, 0x00, StatusClientRequestPacket.class);
        register(PacketOrigin.CLIENT, ProtocolState.STATUS, 0x01, StatusClientPingPacket.class);

        register(PacketOrigin.SERVER, ProtocolState.STATUS, 0x00, StatusServerResponsePacket.class);
        register(PacketOrigin.SERVER, ProtocolState.STATUS, 0x01, StatusServerPongPacket.class);

        register(PacketOrigin.SERVER, ProtocolState.LOGIN, 0x00, LoginServerDisconnectPacket.class);
    }

}
