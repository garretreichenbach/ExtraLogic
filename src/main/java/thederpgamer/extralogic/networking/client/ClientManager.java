package thederpgamer.extralogic.networking.client;

import api.network.packets.PacketUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.schema.game.common.data.player.PlayerState;
import thederpgamer.extralogic.data.linkmodule.LinkChannel;
import thederpgamer.extralogic.networking.client.packets.*;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class ClientManager {

	private static final ObjectArrayList<LinkChannel> linkChannelCache = new ObjectArrayList<>();

	public static void initializePackets() {
		PacketUtil.registerPacket(PacketNewChannelRequest.class);
		PacketUtil.registerPacket(PacketGetChannelRequest.class);
		PacketUtil.registerPacket(PacketUpdateChannelRequest.class);
		PacketUtil.registerPacket(PacketRemoveChannelRequest.class);
		PacketUtil.registerPacket(PacketGetAllChannelsRequest.class);
	}

	/**
	 * Sends a Packet requesting the creation of a new LinkChannel to the server.
	 */
	public static void requestNewChannel(LinkChannel channel) {
		addChannel(channel);
		PacketUtil.sendPacketToServer(new PacketNewChannelRequest(channel));
	}

	public static void addChannel(LinkChannel channel) {
		linkChannelCache.add(channel);
	}

	/**
	 * Adds a new ClientCacheData object to the client cache.
	 *
	 * @param id the id of the object
	 * @return ClientCacheData
	 */
	public static LinkChannel getChannel(String id) {
		for(LinkChannel channel : linkChannelCache) {
			if(channel != null) {
				if(channel.getId().equals(id)) return channel;
			}
		}
		PacketUtil.sendPacketToServer(new PacketGetChannelRequest(id));
		return null;
	}

	public static void removeChannel(LinkChannel channel) {
		linkChannelCache.remove(channel);
		ObjectArrayList<LinkChannel> toRemove = new ObjectArrayList<>();
		for(LinkChannel c : linkChannelCache) {
			if(c.getId().equals(channel.getId())) toRemove.add(c);
		}
		linkChannelCache.removeAll(toRemove);
	}

	public static void updateChannel(LinkChannel channel) {
		removeChannel(channel);
		addChannel(channel);
		PacketUtil.sendPacketToServer(new PacketUpdateChannelRequest(channel));
	}

	public static void requestChannelRemove(LinkChannel channel) {
		removeChannel(channel);
		PacketUtil.sendPacketToServer(new PacketRemoveChannelRequest(channel));
	}

	public static void createNewChannel(PlayerState player, String channelID, String channelName, String channelDescription) {
		requestNewChannel(new LinkChannel(channelID, channelName, channelDescription, player));
	}

	public static ObjectArrayList<LinkChannel> getClientAccessibleChannels() {
		return linkChannelCache;
	}

	public static void requestChannelsFromServer() {
		PacketUtil.sendPacketToServer(new PacketGetAllChannelsRequest());
	}
}
