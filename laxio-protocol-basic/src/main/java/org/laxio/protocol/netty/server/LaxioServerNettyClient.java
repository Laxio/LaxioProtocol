package org.laxio.protocol.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.laxio.LaxioApplication;
import org.laxio.chat.ChatColor;
import org.laxio.network.connection.Compression;
import org.laxio.network.connection.Connection;
import org.laxio.packet.Packet;
import org.laxio.protocol.Protocol;
import org.laxio.protocol.ProtocolState;
import org.laxio.protocol.netty.HandshakingProtocol;
import org.laxio.protocol.netty.connection.NettyCompression;
import org.laxio.protocol.netty.packet.client.handshake.HandshakeClientHandshakePacket;
import org.laxio.protocol.netty.packet.client.status.StatusClientPingPacket;
import org.laxio.protocol.netty.packet.client.status.StatusClientRequestPacket;
import org.laxio.protocol.netty.packet.server.status.StatusServerPongPacket;
import org.laxio.protocol.netty.packet.server.status.StatusServerResponsePacket;
import org.laxio.protocol.netty.pipeline.ChannelInboundMessageAdapter;
import org.laxio.protocol.status.ServerListResponse;
import org.laxio.protocol.status.ServerListResponseDescription;
import org.laxio.protocol.status.ServerListResponsePlayers;
import org.laxio.protocol.status.ServerListResponsePlayersRecord;
import org.laxio.protocol.status.ServerListResponseVersion;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LaxioServerNettyClient extends ChannelInboundMessageAdapter<Packet> implements Connection {

    private final LaxioApplication application;
    private final Compression compression;

    private InetSocketAddress address;
    private ChannelHandlerContext context;
    private Channel channel;

    private ProtocolState protocolState;
    private Protocol protocol;

    public LaxioServerNettyClient(LaxioApplication application) {
        this.application = application;
        this.compression = new NettyCompression();
        this.protocolState = ProtocolState.HANDSHAKE;
        this.protocol = HandshakingProtocol.INSTANCE;
    }

    @Override
    public LaxioApplication getApplication() {
        return application;
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public Compression getCompression() {
        return compression;
    }

    @Override
    public ProtocolState getProtocolState() {
        return protocolState;
    }

    @Override
    public void setProtocolState(ProtocolState protocolState) {
        this.protocolState = protocolState;
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        this.channel = ctx.channel();
        this.context = ctx;
        this.address = (InetSocketAddress) this.channel.remoteAddress();

        System.out.println("Connected! " + this.address);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        System.out.println("Disconnected! " + this.address);
    }

    @Override
    public void onMessage(ChannelHandlerContext ctx, Packet packet) throws Exception {
        // TODO: change this completely
        System.out.println("IN! " + packet);

        if (packet instanceof HandshakeClientHandshakePacket) {
            setProtocolState(ProtocolState.values()[((HandshakeClientHandshakePacket) packet).getNextState()]);
        } else if (packet instanceof StatusClientRequestPacket) {
            List<ServerListResponsePlayersRecord> playersRecordList = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                playersRecordList.add(new ServerListResponsePlayersRecord("Player" + i, UUID.randomUUID()));
            }

            String text = ChatColor.AQUA + application.getName() + ChatColor.WHITE + " |-| " + ChatColor.GRAY + "My Cool Server!";
            sendPacket(new StatusServerResponsePacket(
                new ServerListResponse(
                    new ServerListResponseVersion("Test", 404),
                    new ServerListResponsePlayers(
                            0,
                            application.getAddress().getPort(),
                            playersRecordList
                    ),
                    new ServerListResponseDescription(text),
                    null
                )
            ));
        } else if (packet instanceof StatusClientPingPacket) {
            sendPacket(new StatusServerPongPacket(((StatusClientPingPacket) packet).getPayload()));
        }
    }

    @Override
    public void sendPacket(Packet packet) {
        System.out.println("OUT! " + packet);

        if (channel.eventLoop().inEventLoop()) {
            channel.writeAndFlush(packet);
        } else {
            channel.eventLoop().execute(() -> channel.writeAndFlush(packet));
        }
    }

}
