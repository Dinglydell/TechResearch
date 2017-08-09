package dinglydell.techresearch.techtree;

import java.util.HashMap;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TechNodeType {
	public static final HashMap<String, TechNodeType> types = new HashMap<String, TechNodeType>();
	static {
		RegisterType(new TechNodeType("theory", 0x7c00b9,
				EnumChatFormatting.DARK_PURPLE));
		RegisterType(new TechNodeType("application", 0x51d40f,
				EnumChatFormatting.GREEN));
	}

	public static void RegisterType(TechNodeType type) {
		types.put(type.getKey(), type);
	}

	private String key;
	private int colour;
	private EnumChatFormatting chatColour;

	public TechNodeType(String key, int colour, EnumChatFormatting chatColour) {
		this.key = key;
		this.colour = colour;
		this.chatColour = chatColour;

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

	public String getDescription() {
		// TODO: change this, it's a bit of a hack
		if (key == "theory") {
			return StatCollector
					.translateToLocal("gui.techresearch.techtype.theory.desc");
		}
		return null;
	}

	public EnumChatFormatting getChatColour() {
		return chatColour;
	}
}