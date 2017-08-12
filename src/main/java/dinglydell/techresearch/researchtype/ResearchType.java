package dinglydell.techresearch.researchtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.TechResearch;
import dinglydell.techresearch.techtree.TechNode;
import dinglydell.techresearch.techtree.TechNodeType;
import dinglydell.techresearch.util.MapUtils;

public class ResearchType {
	private static ResearchType topType = null;
	private static Map<String, ResearchType> researchTypes = new HashMap<String, ResearchType>();
	/**
	 * Splits into Science & Engineering. NOTE: This should be the only type at
	 * the top of the tree (with no parent)
	 */
	public static ResearchType research = new ResearchType("research")
			.setAsStartingType().setAsTopType();
	/** Splits into biology & physics */
	public static ResearchType science = new ResearchType("science",
			ResearchType.research);
	/** "Splits" into processing */
	public static ResearchType engineering = new ResearchType("engineering",
			ResearchType.research);

	// science
	/** Splits into motion, electrics */
	public static ResearchType physics = new ResearchType("physics",
			ResearchType.science);
	/** Splits into botany, zoology */
	public static ResearchType biology = new ResearchType("biology",
			ResearchType.science);
	public static ResearchType materials = new ResearchType("materials",
			ResearchType.science);
	public static ResearchType geology = new ResearchType("geology",
			ResearchType.science);

	// engineering
	public static ResearchType processing = new ResearchType("processing",
			ResearchType.engineering);

	// physics
	public static ResearchType motion = new ResearchType("motion",
			ResearchType.physics);
	public static ResearchType electrics = new ResearchType("electrics",
			ResearchType.physics);

	// biology
	public static ResearchType botany = new ResearchType("botany",
			ResearchType.biology);
	public static ResearchType zoology = new ResearchType("zoology",
			ResearchType.biology);

	// materials
	public static ResearchType metallurgy = new ResearchTypeMaterial(
			"metallurgy", ResearchType.materials, ItemMaterial.metal);

	public static ResearchType petrology = new ResearchTypeMaterial(
			"petrology", ResearchType.geology, ItemMaterial.stone);

	// processing
	public static ResearchType crafting = new ResearchType("crafting",
			ResearchType.processing);
	public static ResearchType smithing = new ResearchType("smithing",
			ResearchType.processing);

	public final String name;
	public final ResourceLocation icon;
	private ResearchType parentType = null;
	private boolean isStartType = false;
	private List<ResearchType> childTypes = new ArrayList<ResearchType>();

	public ResearchType(String name) {
		this.name = name;
		if (researchTypes.containsKey(this.name)) {
			throw new IllegalArgumentException(this.name + " already exists.");
		}
		icon = new ResourceLocation(TechResearch.MODID, "textures/icons/"
				+ name + ".png");

		researchTypes.put(this.name, this);
	}

	public ResearchType(String name, ResearchType parentType) {
		this(name);
		this.parentType = parentType;
		parentType.addChild(this);
	}

	protected void addChild(ResearchType child) {
		childTypes.add(child);

	}

	public String getDisplayName() {
		return StatCollector.translateToLocal(getLocalisationString());

	}

	public String getLocalisationString() {
		return "research.techresearch." + name;
	}

	public double getValue(PlayerTechDataExtendedProps ptDep) {
		if (isBaseType()) {
			return MapUtils.getOrDefault(ptDep.researchPoints, this, 0.0);
		}
		double value = 0.0;
		for (ResearchType child : childTypes) {

			value += child.getValue(ptDep);

		}
		return value;
		// return getValue(ptDep.researchPoints);
	}

	/** Display value will not add child values if child type is discovered */
	public double getDisplayValue(PlayerTechDataExtendedProps ptDep) {
		if (isBaseType()) {
			return MapUtils.getOrDefault(ptDep.researchPoints, this, 0.0);
		}
		double value = 0.0;
		for (ResearchType child : childTypes) {
			if (!ptDep.hasDiscovered(child)) {
				value += child.getValue(ptDep);
			}
		}
		return value;
	}

	public double getValue(Map<ResearchType, Double> map) {
		return 0.0;
	}

	/**
	 * The player will know about this type from the start of the game.
	 * 
	 * @return
	 */
	public ResearchType setAsStartingType() {
		this.isStartType = true;
		return this;
	}

	/**
	 * The type that all other types are children of. There can only be one of
	 * these.
	 * */
	public ResearchType setAsTopType() {
		if (getTopType() != null) {
			throw new IllegalArgumentException(
					"Someone tried to set "
							+ this.name
							+ " as the top level type when the top level type already exists!");
		}
		topType = this;
		return this;
	}

	public boolean isBaseType() {
		return childTypes.size() == 0;
	}

	public ResearchType(String name, String parentType) {
		this(name, researchTypes.get(parentType));
	}

	public ResearchType getParentType() {
		return parentType;
	}

	public static ResearchType getType(String name) {
		return researchTypes.get(name);
	}

	public static Map<String, ResearchType> getTypes() {
		return researchTypes;

	}

	public boolean isStartType() {

		return this.isStartType;
	}

	/** Returns true when discovered unless ALL child types have been discovered */
	public boolean isBaseDiscoveredType(PlayerTechDataExtendedProps ptDep) {
		if (!ptDep.hasDiscovered(this)) {
			return false;
		}
		if (isBaseType()) {
			return true;
		}
		for (ResearchType child : childTypes) {
			if (!ptDep.hasDiscovered(child)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if ANY children have been discovered - this is when it
	 * should be displayed as "other *name*"
	 */
	public boolean isOtherType(PlayerTechDataExtendedProps ptDep) {
		if (isBaseType()) {
			return false;
		}
		for (ResearchType child : childTypes) {
			if (ptDep.hasDiscovered(child)) {
				return true;
			}
		}
		return false;
	}

	public List<ResearchType> getChildren() {
		return childTypes;

	}

	public boolean hasParentType() {
		return this.parentType != null;
	}

	public static ResearchType getTopType() {

		return topType;

	}

	/**
	 * Returns the closest parent that has been discovered (or this if this has
	 * been discovered)
	 * */
	public ResearchType getDiscoveredType(PlayerTechDataExtendedProps ptdep) {
		ResearchType discoveredType = this;
		while (!ptdep.hasDiscovered(discoveredType)) {
			discoveredType = discoveredType.getParentType();
		}
		return discoveredType;
	}

	/**
	 * Creates a node to unlock this type.
	 * 
	 * The node will be a "theory", will require the player to have points of
	 * the type and will be set to unlock this type. The node will cost 20
	 * research of the parent type
	 * */
	public TechNode generateTechNode() {
		Map<ResearchType, Double> costs = new HashMap<ResearchType, Double>();
		costs.put(this.parentType, 20.0);
		return generateTechNode(costs);

	}

	/**
	 * Creates a node to unlock this type with a specified cost.
	 * 
	 * The node will be a "theory", will require the player to have points of
	 * the type and will be set to unlock this type
	 * */
	public TechNode generateTechNode(Map<ResearchType, Double> costs) {
		return new TechNode(this.name, TechNodeType.types.get("theory"), costs)
				.addRequirementPoints(this).addSubtypeUnlocked(this)
				.setUnlocalisedName(this.getLocalisationString());
	}

	/**
	 * Creates a node to unlock this type with a specified cost and type.
	 * 
	 * The node will require the player to have points of the type and will be
	 * set to unlock this type
	 * */
	public TechNode generateTechNode(Map<ResearchType, Double> costs,
			TechNodeType type) {
		return new TechNode(this.name, type, costs).addRequirementPoints(this)
				.addSubtypeUnlocked(this)
				.setUnlocalisedName(this.getLocalisationString());
	}
}
