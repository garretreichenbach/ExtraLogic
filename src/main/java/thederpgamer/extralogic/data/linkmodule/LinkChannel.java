package thederpgamer.extralogic.data.linkmodule;

import org.schema.game.common.data.player.PlayerState;

import java.io.Serializable;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class LinkChannel implements Serializable {

	private final String id;
	private String name;
	private String description;
	private final String owner;
	private int factionId;
	private boolean active;

	public LinkChannel(String id, String name, String description, PlayerState owner) {
		this.id = id;
		if(name == null || name.isEmpty()) name = owner.getName() + "'s New Channel";
		this.name = name;
		if(description == null || description.isEmpty()) description = "No description provided.";
		this.description = description;
		this.owner = owner.getName();
		factionId = owner.getFactionId();
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

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
}
