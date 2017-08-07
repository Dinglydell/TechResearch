package dinglydell.techresearch;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.util.StatCollector;

public class TechNode {

	public final String id;

	public final String[] unlocks;
	public final String[] subTypeUnlocks;

	public final Map<ResearchType, Double> costs;

	public final String[] requiresAll;
	public final String[] requiresAny;

	public final String displayName;

	public TechNodeType type;

	public TechNode(String id, String type, String[] unlocks,
			String[] subTypeUnlocks, Map<ResearchType, Double> costs,
			String[] requiresAll, String[] requiresAny, String displayName) {
		this.id = id;
		this.unlocks = unlocks;
		this.subTypeUnlocks = subTypeUnlocks;
		this.costs = costs;
		this.requiresAll = requiresAll;
		this.requiresAny = requiresAny;
		this.displayName = displayName;
		this.type = TechNodeType.types.get(type);
	}

	public TechNode(String id, String type, String[] unlocks,
			String[] subTypeUnlocks, Map<ResearchType, Double> costs,
			String[] requiresAll, String[] requiresAny) {
		this(id, type, unlocks, subTypeUnlocks, costs, requiresAll,
				requiresAny, "tech.techresearch." + id);
	}

	public String costsAsString() {
		StringBuilder cSb = new StringBuilder();
		for (Entry<ResearchType, Double> c : costs.entrySet()) {
			cSb.append(c.getKey().getDisplayName()).append(": ")
					.append(c.getValue()).append(", ");
		}
		cSb.deleteCharAt(cSb.length() - 1);
		cSb.deleteCharAt(cSb.length() - 1);
		return cSb.toString();
	}

	public String costsAsString(NodeProgress np) {
		StringBuilder cSb = new StringBuilder();
		for (Entry<ResearchType, Double> c : costs.entrySet()) {
			cSb.append(c.getKey().name).append(": ")
					.append(np.getProgress(c.getKey())).append("/")
					.append(c.getValue()).append(", ");
		}
		cSb.deleteCharAt(cSb.length() - 1);
		cSb.deleteCharAt(cSb.length() - 1);
		return cSb.toString();
	}

	public String getDisplayName() {

		return StatCollector.translateToLocal(displayName);
	}

}
