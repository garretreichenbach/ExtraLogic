package thederpgamer.extralogic.element.blocks;

import org.schema.game.common.data.element.ElementCategory;

/**
 * <Description>
 *
 * @author TheDerpGamer
 * @since 09/01/2021
 */
public interface BlockGroup {
	Block[] getBlocks();

	ElementCategory getCategory();
}
