package dinglydell.techresearch.techtree;

import java.util.ArrayList;
import java.util.Collection;
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

	private final List<TechNode> requiresAll;
	private final List<TechNode> requiresAny;

	/**
	 * Requires you to have research points of these types
	 * */
	private final List<ResearchType> requiresPoints;

	public String unlocalisedName;

	public String description;

	public TechNodeType type;

	public TechNode(String id, TechNodeType type,
			Map<ResearchType, Double> costs) {
		// , String[] unlocks,
		// String[] subTypeUnlocks,,
		// String[] requiresAll, String[] requiresAny,
		// String[] requiresPoints, ) {

		this.id = id;

		this.unlocks = new ArrayList<Item>();

		this.subTypeUnlocks = new ArrayList<ResearchType>();

		this.costs = costs;
		this.requiresAll = new ArrayList<TechNode>();
		this.requiresAny = new ArrayList<TechNode>();
		this.requiresPoints = new ArrayList<ResearchType>();
		// for (String rt : requiresPoints) {
		// this.requiresPoints.add(ResearchType.getType(rt));
		// }
		this.unlocalisedName = "tech.techresearch." + id;
		this.description = "tech.techresearch." + id + ".desc";
		this.type = type;
	}

	/**
	 * Sets the localisation string for the display name. Default is
	 * tech.techresearch.NODEID
	 * */
	public TechNode setUnlocalisedName(String name) {
		this.unlocalisedName = name;
		return this;
	}

	/**
	 * Sets the localisation string for the description. Default is
	 * tech.techresearch.NODEID.desc
	 * 
	 * If it can't translate it won't display a description (TODO: This part is
	 * only partially implemented)
	 * */
	public TechNode setDecsription(String desc) {
		this.description = desc;
		return this;
	}

	/**
	 * Adds a tech requirement for this tech node.
	 * 
	 * The node requires every requirementAll to be valid.
	 * */
	public TechNode addRequirementAll(String nodeID) {
		return addRequirementAll(nodeID);

	}

	/**
	 * Adds a tech requirement for this tech node.
	 * 
	 * The node requires every requirementAll to be valid.
	 * */
	public TechNode addRequirementAll(TechNode required) {
		requiresAll.add(required);
		return this;

	}

	/**
	 * Adds many requirements at once for this tech node.
	 * 
	 * The node requires every requirementAll to be valid.
	 * */
	public TechNode addRequirementsAll(Collection<String> reqAll) {
		for (String id : reqAll) {
			addRequirementAll(id);
		}
		return this;

	}

	/**
	 * Adds a tech requirement for this tech node.
	 * 
	 * The node requires ANY requirementAny to be valid.
	 * */
	public TechNode addRequirementAny(String nodeID) {
		return addRequirementAny(TechTree.nodes.get(nodeID));
	}

	/**
	 * Adds a tech requirement for this tech node.
	 * 
	 * The node requires ANY requirementAny to be valid.
	 * */
	public TechNode addRequirementAny(TechNode required) {
		requiresAny.add(required);
		return this;

	}

	/**
	 * Adds many requirements at once for this tech node.
	 * 
	 * The node requires ANY requirementAny to be valid.
	 * */
	public TechNode addRequirementsAny(Collection<String> reqAny) {
		for (String id : reqAny) {
			addRequirementAny(id);
		}
		return this;
	}

	/**
	 * Adds many research points requirements to this tech node at once.
	 * 
	 * @param reqPts
	 *            The node will require the player to have at least some
	 *            research points of each of these type to be valid
	 * */
	public TechNode addRequirementsPoints(Collection<String> reqPts) {
		for (String id : reqPts) {
			addRequirementPoints(id);
		}
		return this;
	}

	/**
	 * Adds a research points requirement to this tech node.
	 * 
	 * @param typeID
	 *            The node will require the player to have at least some
	 *            research points of this type to be valid
	 * */
	public TechNode addRequirementPoints(String typeID) {
		return addRequirementPoints(ResearchType.getType(typeID));
	}

	/**
	 * Adds a research points requirement to this tech node.
	 * 
	 * @param type
	 *            The node will require the player to have at least some
	 *            research points of this type to be valid
	 * */
	public TechNode addRequirementPoints(ResearchType type) {
		requiresPoints.add(type);
		return this;
	}

	/**
	 * Adds multiple items that will have its recipe unlocked by this tech node.
	 * 
	 * Once a tech node is set to unlock an item, its recipe will automatically
	 * be unavailable in all methods defined by registered IReplacementHandlers
	 * until the node is unlocked.
	 * */
	public TechNode addItemsUnlocked(Iterable<String> items) {
		for (String id : items) {
			addItemUnlocked(id);
		}
		return this;
	}

	/**
	 * Adds an item that will have its recipe unlocked by this tech node.
	 * 
	 * Once a tech node is set to unlock an item, its recipe will automatically
	 * be unavailable in all methods defined by registered IReplacementHandlers
	 * until the node is unlocked.
	 * */
	public TechNode addItemUnlocked(String itemID) {

		return addItemUnlocked(GameData.getItemRegistry().getObject(itemID));
	}

	/**
	 * Adds an item that will have its recipe unlocked by this tech node.
	 * 
	 * Once a tech node is set to unlock an item, its recipe will automatically
	 * be unavailable in all methods defined by registered IReplacementHandlers
	 * until the node is unlocked.
	 * */
	public TechNode addItemUnlocked(Item item) {

		this.unlocks.add(item);
		return this;
	}

	/**
	 * Adds many research types that this tech node will unlock.
	 * 
	 * If the player has any tech that unlocks this type then it will be
	 * available
	 * */
	public TechNode addSubtypesUnlocked(Iterable<String> rts) {
		for (String id : rts) {
			addSubtypeUnlocked(id);
		}
		return this;
	}

	/**
	 * Adds a research type that this tech node will unlock.
	 * 
	 * If the player has any tech that unlocks this type then it will be
	 * available
	 * */
	public TechNode addSubtypeUnlocked(String typeID) {
		return addSubtypeUnlocked(ResearchType.getType(typeID));
	}

	/**
	 * Adds a research type that this tech node will unlock.
	 * 
	 * If the player has any tech that unlocks this type then it will be
	 * available
	 * */
	public TechNode addSubtypeUnlocked(ResearchType type) {
		this.requiresPoints.add(type);
		return this;
	}

	// public TechNode(String id, String type,
	// // String[] subTypeUnlocks,
	// Map<ResearchType, Double> costs,
	// // String[] requiresAll, String[] requiresAny,
	// // String[] requiresPoints,
	// String displayName) {
	// this(id, type, costs, displayName, "tech.techresearch." + id + ".desc");
	// }
	//
	// public TechNode(String id, String type, Map<ResearchType, Double> costs)
	// {
	// this(id, type, costs, "tech.techresearch." + id);
	// }

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
			return false;
		}
		for (ResearchType rt : this.costs.keySet()) {
			if (!ptdep.hasDiscovered(rt)) {
				return false;
			}
		}
		for (ResearchType type : this.requiresPoints) {
			if (type.getValue(ptdep) == 0) {
				return false;
			}
		}
		for (TechNode tn : this.requiresAll) {
			if (!ptdep.hasCompleted(tn)) {
				return false;
			}
		}
		for (TechNode tn : this.requiresAny) {
			if (ptdep.hasCompleted(tn)) {
				return true;
			}
		}

		return this.requiresAny.size() == 0;
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

		return StatCollector.translateToLocal(unlocalisedName);
	}

	public String getDescription() {
		return StatCollector.translateToLocal(unlocalisedName + ".desc");
	}

}
