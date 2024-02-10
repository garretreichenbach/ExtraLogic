package thederpgamer.extralogic.element.blocks;

import api.config.BlockConfig;
import org.schema.game.common.data.element.ElementCategory;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.game.common.data.element.ElementKeyMap;
import thederpgamer.extralogic.ExtraLogic;

public abstract class Block {
	protected ElementInformation blockInfo;

	protected Block(String name, ElementCategory category) {
		blockInfo = BlockConfig.newElement(ExtraLogic.getInstance(), name, new short[6]);
		BlockConfig.setElementCategory(blockInfo, category);
	}

	public void addController(short id) {
		if(ElementKeyMap.isValidType(id)) {
			blockInfo.controlling.add(id);
			ElementKeyMap.getInfo(id).controlledBy.add(blockInfo.getId());
		}
	}

	public void addControlling(short id) {
		if(ElementKeyMap.isValidType(id)) {
			blockInfo.controlling.add(id);
			ElementKeyMap.getInfo(id).controlledBy.add(blockInfo.getId());
		}
	}

	public final ElementInformation getBlockInfo() {
		return blockInfo;
	}

	public final short getId() {
		return blockInfo.getId();
	}

	public abstract void initialize();
}
