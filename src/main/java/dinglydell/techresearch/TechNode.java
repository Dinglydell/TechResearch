package dinglydell.techresearch;

import java.util.Map;
import java.util.Map.Entry;

public class TechNode {

	public final String id;

	public final String[] unlocks;
	public final String[] subTypeUnlocks;

	public final Map<ResearchType, Double> costs;

	public final String[] requiresAll;
	public final String[] requiresAny;

	public TechNode(String id, String[] unlocks, String[] subTypeUnlocks,
			Map<ResearchType, Double> costs, String[] requiresAll,
			String[] requiresAny) {
		this.id = id;
		this.unlocks = unlocks;
		this.subTypeUnlocks = subTypeUnlocks;
		this.costs = costs;
		this.requiresAll = requiresAll;
		this.requiresAny = requiresAny;
	}

	public String costsAsString() {
		StringBuilder cSb = new StringBuilder();
		for (Entry<ResearchType, Double> c : costs.entrySet()) {
			cSb.append(c.getKey().name).append(": ").append(c.getValue())
					.append(", ");
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

}
