package dinglydell.techresearch;

import java.util.Map;
import java.util.Map.Entry;

import dinglydell.techresearch.researchtype.ResearchType;

import net.minecraft.util.StatCollector;

public class TechNode {

	public final String id;

	public final String[] unlocks;
	public final String[] subTypeUnlocks;

	public final Map<ResearchType, Double> costs;

	public final String[] requiresAll;
	public final String[] requiresAny;

	/**
	 * Requires you to have research points of these types
	 * */
	public final String[] requiresPoints;

	public final String displayName;

	public final String description;

	public TechNodeType type;

	public TechNode(String id, String type, String[] unlocks,
			String[] subTypeUnlocks, Map<ResearchType, Double> costs,
			String[] requiresAll, String[] requiresAny,
			String[] requiresPoints, String displayName, String description) {
		this.id = id;
		this.unlocks = unlocks;
		this.subTypeUnlocks = subTypeUnlocks;
		this.costs = costs;
		this.requiresAll = requiresAll;
		this.requiresAny = requiresAny;
		this.requiresPoints = requiresPoints;
		this.displayName = displayName;
		this.description = description;
		this.type = TechNodeType.types.get(type);
	}

	public TechNode(String id, String type, String[] unlocks,
			String[] subTypeUnlocks, Map<ResearchType, Double> costs,
			String[] requiresAll, String[] requiresAny,
			String[] requiresPoints, String displayName) {
		this(id, type, unlocks, subTypeUnlocks, costs, requiresAll,
				requiresAny, requiresPoints, displayName, "tech.techresearch."
						+ id + ".desc");
	}

	public TechNode(String id, String type, String[] unlocks,
			String[] subTypeUnlocks, Map<ResearchType, Double> costs,
			String[] requiresAll, String[] requiresAny, String[] requiresPoints) {
		this(id, type, unlocks, subTypeUnlocks, costs, requiresAll,
				requiresAny, requiresPoints, "tech.techresearch." + id);
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

	public String getDescription() {
		return StatCollector.translateToLocal(displayName + ".desc");
	}

}
