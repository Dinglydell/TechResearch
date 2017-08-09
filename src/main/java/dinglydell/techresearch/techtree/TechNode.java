package dinglydell.techresearch.techtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.registry.GameData;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.researchtype.ResearchType;

public class TechNode {

	public final String id;

	private final List<Item> unlocks;
	private final List<ResearchType> subTypeUnlocks;

	public final Map<ResearchType, Double> costs;

	private final String[] requiresAll;
	private final String[] requiresAny;

	/**
	 * Requires you to have research points of these types
	 * */
	private final String[] requiresPoints;

	public final String displayName;

	public final String description;

	public TechNodeType type;

	public TechNode(String id, String type, String[] unlocks,
			String[] subTypeUnlocks, Map<ResearchType, Double> costs,
			String[] requiresAll, String[] requiresAny,
			String[] requiresPoints, String displayName, String description) {
		this.id = id;

		this.unlocks = new ArrayList<Item>();
		for (String it : unlocks) {
			this.unlocks.add(GameData.getItemRegistry().getObject(it));
		}
		this.subTypeUnlocks = new ArrayList<ResearchType>();
		for (String rt : subTypeUnlocks) {
			this.subTypeUnlocks.add(ResearchType.getType(rt));
		}

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

	public List<Item> getItemsUnlocked() {

		return unlocks;
	}

	public List<ResearchType> getSubtypesUnlocked() {

		return subTypeUnlocks;
	}

	/**
	 * Whether this tech is valid to be available to the player
	 * */
	public boolean isValid(PlayerTechDataExtendedProps ptdep) {
		if (ptdep.hasCompleted(this)) {
			return true;
		}
		for (ResearchType rt : this.costs.keySet()) {
			if (!ptdep.hasDiscovered(rt)) {
				return true;
			}
		}
		for (String type : this.requiresPoints) {
			if (ResearchType.getType(type).getValue(ptdep) == 0) {
				return true;
			}
		}
		for (String tid : this.requiresAll) {
			if (!ptdep.hasCompleted(tid)) {
				return true;
			}
		}
		for (String tid : this.requiresAny) {
			if (ptdep.hasCompleted(tid)) {
				return false;
			}
		}

		return this.requiresAny.length != 0;
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
