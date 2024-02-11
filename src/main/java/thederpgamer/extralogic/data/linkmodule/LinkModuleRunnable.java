package thederpgamer.extralogic.data.linkmodule;

import org.schema.game.common.controller.SendableSegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.element.ElementCollection;
import thederpgamer.extralogic.systems.logic.WirelessLinkModule;

import java.util.HashMap;
import java.util.Map;

/**
 * [Description]
 *
 * @author Garret Reichenbach
 */
public class LinkModuleRunnable implements Runnable {

	private final SegmentPiece activator;
	private final WirelessLinkModule.WirelessLinkModuleData activatorData;
	private final HashMap<WirelessLinkModule.WirelessLinkModuleData, SegmentPiece> toActivate = new HashMap<>();

	public LinkModuleRunnable(WirelessLinkModule.WirelessLinkModuleData activatorData, SegmentPiece activator, HashMap<WirelessLinkModule.WirelessLinkModuleData, SegmentPiece> toActivate) {
		this.activatorData = activatorData;
		this.activator = activator;
		this.toActivate.putAll(toActivate);
	}

	@Override
	public void run() {
		for(Map.Entry<WirelessLinkModule.WirelessLinkModuleData, SegmentPiece> data : toActivate.entrySet()) {
			if(data.getKey() != activatorData) {
				SegmentPiece segmentPiece = data.getValue();
				if(segmentPiece.isActive() != activator.isActive()) {
					long activationEnc = ElementCollection.getEncodeActivation(segmentPiece, true, activator.isActive(), false);
					((SendableSegmentController) segmentPiece.getSegmentController()).getBlockActivationBuffer().enqueue(activationEnc);
				}
			}
		}
	}
}
