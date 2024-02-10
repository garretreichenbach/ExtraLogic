package thederpgamer.extralogic.data;

import org.schema.game.common.data.player.PlayerState;
import thederpgamer.extralogic.networking.client.ClientCacheData;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class LinkChannel implements ClientCacheData {

	private final String id;
	private String name;
	private String description;
	private final String owner;
	private int factionId;
	private String password;
	private boolean active;

	public LinkChannel(String id, String name, String description, PlayerState owner) {
		this.id = id;
		if(name.isEmpty()) name = owner.getName() + "'s New Channel";
		this.name = name;
		if(description.isEmpty()) description = "No description provided.";
		this.description = description;
		this.owner = owner.getName();
		factionId = owner.getFactionId();
		password = "";
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFactionId() {
		return factionId;
	}

	public void setFactionId(int factionId) {
		this.factionId = factionId;
	}

	public String getOwner() {
		return owner;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void resetPassword() {
		password = "";
	}

	public boolean hasPassword() {
		return !password.isEmpty();
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
}
