package thederpgamer.extralogic.manager;

import api.utils.textures.StarLoaderTexture;
import org.schema.schine.graphicsengine.core.ResourceException;
import org.schema.schine.graphicsengine.forms.Mesh;
import org.schema.schine.graphicsengine.forms.Sprite;
import org.schema.schine.resource.ResourceLoader;
import thederpgamer.extralogic.ExtraLogic;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.HashMap;

/**
 * Manager class for handling resource loading.
 *
 * @author TheDerpGamer
 */
public class ResourceManager {
	private static final String[] textureNames = {"wireless-link-module"};
	private static final String[] iconNames = {"wireless-link-module-icon"};
	private static final String[] spriteNames = {};
	private static final String[] modelNames = {};
	private static final HashMap<String, StarLoaderTexture> textureMap = new HashMap<>();
	private static final HashMap<String, StarLoaderTexture> iconMap = new HashMap<>();
	private static final HashMap<String, Sprite> spriteMap = new HashMap<>();
	private static final HashMap<String, Mesh> meshMap = new HashMap<>();

	public static void loadResources(final ExtraLogic instance, final ResourceLoader loader) {
		StarLoaderTexture.runOnGraphicsThread(new Runnable() {
			@Override
			public void run() {
				//Load Textures
				for(String textureName : textureNames) {
					try {
						textureMap.put(textureName, StarLoaderTexture.newBlockTexture(ImageIO.read(ExtraLogic.getInstance().getJarResource("textures/" + textureName + ".png")), ImageIO.read(ExtraLogic.getInstance().getJarResource("textures/" + textureName + "-normal.png"))));
					} catch(Exception exception) {
						instance.logException("Failed to load texture \"" + textureName + "\"", exception);
					}
				}

				//Load Icons
				for(String iconName : iconNames) {
					try {
						iconMap.put(iconName, StarLoaderTexture.newIconTexture(ImageIO.read(ExtraLogic.getInstance().getJarResource("icons/" + iconName + ".png"))));
					} catch(Exception exception) {
						instance.logException("Failed to load icon \"" + iconName + "\"", exception);
					}
				}

				//Load Sprites
				for(String spriteName : spriteNames) {
					try {
						Sprite sprite = StarLoaderTexture.newSprite(ImageIO.read(ExtraLogic.getInstance().getJarResource("sprites/" + spriteName + ".png")), instance, spriteName);
						sprite.setPositionCenter(false);
						sprite.setName(spriteName);
						spriteMap.put(spriteName, sprite);
					} catch(Exception exception) {
						instance.logException("Failed to load sprite \"" + spriteName + "\"", exception);
					}
				}
				//Load models
				for(String modelName : modelNames) {
					try {
						loader.getMeshLoader().loadModMesh(instance, modelName, ExtraLogic.getInstance().getJarResource("models/" + modelName + ".zip"), null);
						Mesh mesh = loader.getMeshLoader().getModMesh(ExtraLogic.getInstance(), modelName);
						mesh.setFirstDraw(true);
						meshMap.put(modelName, mesh);
					} catch(ResourceException | IOException exception) {
						instance.logException("Failed to load model \"" + modelName + "\"", exception);
					}
				}
			}
		});
	}

	public static StarLoaderTexture getTexture(String name) {
		return textureMap.get(name);
	}

	public static StarLoaderTexture getIcon(String name) {
		return iconMap.get(name);
	}

	public static Sprite getSprite(String name) {
		return spriteMap.get(name);
	}

	public static Mesh getMesh(String name) {
		if(meshMap.containsKey(name)) return (Mesh) meshMap.get(name).getChilds().get(0);
		else return null;
	}
}
