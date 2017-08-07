package dinglydell.techresearch;

import java.util.HashMap;

public class TechNodeType {
	public static final HashMap<String, TechNodeType> types = new HashMap<String, TechNodeType>();
	static {
		RegisterType(new TechNodeType("theory"));
		RegisterType(new TechNodeType("application"));
	}

	public static void RegisterType(TechNodeType type) {
		types.put(type.getKey(), type);
	}

	private String key;

	public TechNodeType(String key) {
		this.key = key;

	}

	public String getKey() {
		return key;
	}
}