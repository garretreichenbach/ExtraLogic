package thederpgamer.extralogic.networking.server.packets;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.extralogic.data.LinkChannel;
import thederpgamer.extralogic.networking.client.ClientManager;

import java.io.IOException;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class PacketRemoveChannelResponse extends Packet {

	private LinkChannel channel;

	public PacketRemoveChannelResponse() {

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
		ClientManager.removeChannel(channel);
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {

	}

	public PacketRemoveChannelResponse(LinkChannel channel) {
		this.channel = channel;
	}
}
