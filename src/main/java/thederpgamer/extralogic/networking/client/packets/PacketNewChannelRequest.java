package thederpgamer.extralogic.networking.client.packets;

import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.extralogic.data.LinkChannel;
import thederpgamer.extralogic.systems.WirelessLinkModule;

import java.io.IOException;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class PacketNewChannelRequest extends Packet {

	private LinkChannel channel;

	public PacketNewChannelRequest() {

	}

	public PacketNewChannelRequest(LinkChannel channel) {
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
		WirelessLinkModule.addChannel(channel);
	}
}
