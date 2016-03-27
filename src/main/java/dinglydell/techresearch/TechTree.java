package dinglydell.techresearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dinglydell.techresearch.recipe.IReplacementHandler;

public class TechTree {
	static Map<String, TechNode> nodes = new HashMap<String, TechNode>();

	static List<IReplacementHandler> handlers = new ArrayList<IReplacementHandler>();

	public static void AddTechNode(TechNode node) {
		nodes.put(node.id, node);
	}

	public static void addHandler(IReplacementHandler handler) {

	}

	public static void replace() {
		for (IReplacementHandler handler : handlers) {
			handler.replace(nodes.values());
		}
	}
}
