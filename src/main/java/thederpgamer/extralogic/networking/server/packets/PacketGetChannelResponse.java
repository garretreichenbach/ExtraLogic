package thederpgamer.extralogic.networking.server.packets;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.extralogic.data.linkmodule.LinkChannel;
import thederpgamer.extralogic.networking.client.ClientManager;

import java.io.IOException;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class PacketGetChannelResponse extends Packet {

	private LinkChannel linkChannel;

	public PacketGetChannelResponse() {

	}

	public PacketGetChannelResponse(LinkChannel linkChannel) {
		this.linkChannel = linkChannel;
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		linkChannel = packetReadBuffer.readObject(LinkChannel.class);
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeObject(linkChannel);
	}

	@Override
	public void processPacketOnClient() {
		ClientManager.addChannel(linkChannel);
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {

	}
}
