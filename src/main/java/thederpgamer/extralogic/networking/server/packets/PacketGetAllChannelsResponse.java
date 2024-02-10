package thederpgamer.extralogic.networking.server.packets;

import api.mod.config.PersistentObjectUtil;
import api.network.Packet;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.extralogic.ExtraLogic;
import thederpgamer.extralogic.data.LinkChannel;
import thederpgamer.extralogic.networking.client.ClientManager;

import java.io.IOException;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class PacketGetAllChannelsResponse extends Packet {

	private LinkChannel[] channels;

	public PacketGetAllChannelsResponse() {

	}

	public PacketGetAllChannelsResponse(PlayerState playerState) {
		ObjectArrayList<LinkChannel> channelList = new ObjectArrayList<>();
		for(LinkChannel channel : PersistentObjectUtil.getCopyOfObjects(ExtraLogic.getInstance().getSkeleton(), LinkChannel.class)) {
			if(playerState.getFactionId() == channel.getFactionId() || playerState.isAdmin()) channelList.add(channel);
		}
		channels = channelList.toArray(new LinkChannel[0]);
	}

	@Override
	public void readPacketData(PacketReadBuffer packetReadBuffer) throws IOException {
		channels = new LinkChannel[packetReadBuffer.readInt()];
		for(int i = 0; i < channels.length; i ++) {
			channels[i] = packetReadBuffer.readObject(LinkChannel.class);
		}
	}

	@Override
	public void writePacketData(PacketWriteBuffer packetWriteBuffer) throws IOException {
		packetWriteBuffer.writeInt(channels.length);
		for(LinkChannel channel : channels) {
			packetWriteBuffer.writeObject(channel);
		}
	}

	@Override
	public void processPacketOnClient() {
		for(LinkChannel channel : channels) ClientManager.addChannel(channel);
	}

	@Override
	public void processPacketOnServer(PlayerState playerState) {

	}
}
