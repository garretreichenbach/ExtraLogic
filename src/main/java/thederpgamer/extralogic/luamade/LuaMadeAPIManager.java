package thederpgamer.extralogic.luamade;

import api.mod.StarLoader;
import luamade.lua.element.block.Block;
import luamade.luawrap.LuaMadeUserdata;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import thederpgamer.extralogic.element.ElementManager;

public class LuaMadeAPIManager {

	public static boolean initialize() {
		if(StarLoader.getModFromName("LuaMade") != null) {
			LuaMadeUserdata.graftMethod(Block.class, "isHoloProjector", new LuaFunction() {
				@Override
				public LuaValue call(LuaValue arg) {
					Block block = (Block) arg.checkuserdata(Block.class);
					return LuaBoolean.valueOf(block.getId() == ElementManager.getBlock("Holo Projector").getId());
				}
			});
			return true;
		} else return false;
	}
}
