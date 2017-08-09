package dinglydell.techresearch.researchtype;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemMaterial {
	static Map<Item, ItemMaterial> materialDatabase = new HashMap<Item, ItemMaterial>();

	public static ItemMaterial metal = new ItemMaterial("metal");

	public static ItemMaterial stone = new ItemMaterial("stone");

	static {
		materialDatabase.put(GameRegistry.findItem("minecraft", "iron_ingot"),
				ItemMaterial.metal);
		addTools("minecraft", "iron", ItemMaterial.metal);
		addTools("minecraft", "stone", ItemMaterial.stone);
		addTools("minecraft", "gold", ItemMaterial.metal);
		addTools("minecraft", "diamond", ItemMaterial.stone);
	}

	public static void addTools(String modId, String type, ItemMaterial material) {
		String[] tools = new String[] { "_sword", "_shovel", "_pickaxe", "_axe" };
		for (String tool : tools) {
			registerItem(GameRegistry.findItem(modId, type + tool), material);
		}

	}

	public static void registerItem(Item item, ItemMaterial material) {
		materialDatabase.put(item, material);
	}

	private String name;

	public ItemMaterial(String name) {
		this.name = name;
	}

	public static boolean hasEntry(Item item) {

		return materialDatabase.containsKey(item);
	}

	public static ItemMaterial get(Item item) {
		return materialDatabase.get(item);
	}
}
