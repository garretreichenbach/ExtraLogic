package thederpgamer.extralogic.networking.client.packets;

import api.common.GameServer;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.extralogic.data.LinkChannel;
import thederpgamer.extralogic.networking.server.packets.PacketRemoveChannelResponse;

import java.io.IOException;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class PacketRemoveChannelRequest extends Packet {

	private LinkChannel channel;

	public PacketRemoveChannelRequest() {
	}

	public PacketRemoveChannelRequest(LinkChannel channel) {
		this.channel = channel;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		channel = packetReadBuffer.readObject(LinkChannel.class);
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeObject(channel);
	}

	@Override
	public void processPacketOnClient() {

	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {
		for(PlayerState player : GameServer.getServerState().getPlayerStatesByName().values()) {
			if(!player.equals(playerState)) {
				PacketUtil.sendPacket(player, new PacketRemoveChannelResponse(channel));
			}
		}
	}
}
