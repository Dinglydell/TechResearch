package dinglydell.techresearch;

import java.util.HashMap;

import net.minecraft.util.StatCollector;

public class TechNodeType {
	public static final HashMap<String, TechNodeType> types = new HashMap<String, TechNodeType>();
	static {
		RegisterType(new TechNodeType("theory", 0x7c00b9));
		RegisterType(new TechNodeType("application", 0x51d40f));
	}

	public static void RegisterType(TechNodeType type) {
		types.put(type.getKey(), type);
	}

	private String key;
	private int colour;

	public TechNodeType(String key, int colour) {
		this.key = key;
		this.colour = colour;

	}

	public String getKey() {
		return key;
	}

	public int getColour() {
		return colour;
	}

	public String getDisplayName() {
		return StatCollector.translateToLocal("gui.techresearch.techtype."
				+ key);
	}
}