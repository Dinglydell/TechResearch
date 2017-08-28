package dinglydell.techresearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import dinglydell.techresearch.experiment.Experiment;
import dinglydell.techresearch.experiment.PlayerExperimentData;
import dinglydell.techresearch.network.PacketTechResearch;
import dinglydell.techresearch.researchtype.ResearchType;
import dinglydell.techresearch.techtree.NodeProgress;
import dinglydell.techresearch.techtree.TechNode;
import dinglydell.techresearch.techtree.TechTree;
import dinglydell.techresearch.util.MapUtils;

public class PlayerTechDataExtendedProps implements IExtendedEntityProperties {

	public static final int NUM_AVAILABLE_TECHS = 4;
	public static final String TECHDATA = "techData";
	// public static final String BIOLOGY = "biology";
	// public static final String ENGINEERING = "engineering";
	// public static final String PHYSICS = "physics";
	public static final String RESEARCH_POINTS = "researchPoints";
	public static final String RESEARCH_NAME = "name";
	public static final String TECH_NODES = "techNodes";
	public static final String EXPERIMENTS = "experiments";
	public static final String EXPERIMENT = "experiment";
	public static final String QUANTITY = "quantity";
	public static final String AVAILABLE_NODES = "available";
	public static final String AVAILABLE_NODE = "node";

	public final EntityPlayer player;

	public Map<ResearchType, Double> researchPoints = new HashMap<ResearchType, Double>();
	// protected double biology = 0;
	// protected double engineering = 0;
	// protected double physics = 0;
	private Map<TechNode, NodeProgress> nodes = new HashMap<TechNode, NodeProgress>();
	private Map<String, TechNode> availableNodes = new HashMap<String, TechNode>();
	private Map<Experiment, PlayerExperimentData> experiments = new HashMap<Experiment, PlayerExperimentData>();

	public PlayerTechDataExtendedProps(EntityPlayer player) {
		this.player = player;

	}

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		NBTTagCompound techData = new NBTTagCompound();
		nbt.setTag(TECHDATA, techData);
		NBTTagList rsrchPts = new NBTTagList();
		techData.setTag(RESEARCH_POINTS, rsrchPts);
		for (Entry<ResearchType, Double> rsrch : researchPoints.entrySet()) {
			NBTTagCompound rTag = new NBTTagCompound();
			rTag.setString(RESEARCH_NAME, rsrch.getKey().name);
			rTag.setDouble(QUANTITY, rsrch.getValue());
			rsrchPts.appendTag(rTag);
		}
		// techData.s(BIOLOGY, biology);
		// techData.setDouble(ENGINEERING, engineering);
		// techData.setDouble(PHYSICS, physics);

		NBTTagList techNodes = new NBTTagList();
		techData.setTag(TECH_NODES, techNodes);
		for (Entry<TechNode, NodeProgress> np : getNodes().entrySet()) {
			techNodes.appendTag(np.getValue().getTag());
		}
		NBTTagList exps = new NBTTagList();
		techData.setTag(EXPERIMENTS, exps);
		for (Entry<Experiment, PlayerExperimentData> exp : experiments
				.entrySet()) {
			NBTTagCompound expTag = exp.getValue().getNBTData();
			expTag.setString(EXPERIMENT, exp.getKey().name);
			// expTag.setInteger(QUANTITY, exp.getValue());
			exps.appendTag(expTag);
		}

		// available nodes
		NBTTagList avNodes = new NBTTagList();
		for (TechNode node : getAvailableNodes(false)) {
			NBTTagCompound nodeTag = new NBTTagCompound();

			// nodeTag.setString(AVAILABLE_NODE, node.getValue().id);
			avNodes.appendTag(new NBTTagString(node.id));
		}
		techData.setTag(AVAILABLE_NODES, avNodes);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		NBTTagCompound techData = nbt.getCompoundTag(TECHDATA);
		// biology = techData.getDouble(BIOLOGY);
		// engineering = techData.getDouble(ENGINEERING);
		// physics = techData.getDouble(PHYSICS);

		researchPoints.clear();
		NBTTagList rsrchPts = techData.getTagList(RESEARCH_POINTS, 10);
		for (int i = 0; i < rsrchPts.tagCount(); i++) {
			NBTTagCompound rTag = rsrchPts.getCompoundTagAt(i);
			ResearchType rt = ResearchType.getType(rTag
					.getString(RESEARCH_NAME));
			if (rt != null) {
				researchPoints.put(rt, rTag.getDouble(QUANTITY));
			}
		}
		getNodes().clear();
		NBTTagList techNodes = techData.getTagList(TECH_NODES, 10);
		for (int i = 0; i < techNodes.tagCount(); i++) {
			NodeProgress np = new NodeProgress(techNodes.getCompoundTagAt(i));
			getNodes().put(np.node, np);
		}
		experiments.clear();
		NBTTagList exps = techData.getTagList(EXPERIMENTS, 10);
		for (int i = 0; i < exps.tagCount(); i++) {
			NBTTagCompound expTag = exps.getCompoundTagAt(i);
			Experiment exp = Experiment.experiments.get(expTag
					.getString(EXPERIMENT));
			experiments.put(exp, exp.getData(expTag));
			// experiments.put(Experiment.experiments.get(expTag
			// .getString(EXPERIMENT)), expTag.getInteger(QUANTITY));
		}

		// available nodes
		NBTTagList avNodes = techData.getTagList(AVAILABLE_NODES, 8);
		availableNodes.clear();
		for (int i = 0; i < avNodes.tagCount(); i++) {
			String id = avNodes.getStringTagAt(i);
			availableNodes.put(id, TechTree.nodes.get(id));
		}
	}

	@Override
	public void init(Entity ent, World world) {
		if (availableNodes.isEmpty()) {
			regenerateTechChoices();

		}
	}

	public void regenerateTechChoices() {
		availableNodes.clear();
		// List<TechNode> allNodes = new
		// ArrayList<TechNode>(TechTree.nodes.values());
		// final PlayerTechDataExtendedProps ptdep = this;
		List<TechNode> nodes = new ArrayList<TechNode>();
		for (TechNode tn : TechTree.nodes.values()) {
			if (tn.isValid(this)) {
				nodes.add(tn);
			}
		}
		// nodes.removeIf(new Predicate<TechNode>() {
		// @Override
		// public boolean test(TechNode tn) {
		// return tn.isValid(ptdep);
		// }
		// });
		float totalWeight = 0;
		// TODO: better varying tech weight
		Map<TechNode, Double> weights = new HashMap<TechNode, Double>();
		for (TechNode tn : nodes) {
			int numCosts = tn.costs.size();
			double weight = 0;
			for (Entry<ResearchType, Double> cost : tn.costs.entrySet()) {

				weight += getResearchPoints(cost.getKey())
						/ (cost.getValue() * tn.costs.size());
			}
			totalWeight += weight;
			weights.put(tn, weight);
		}
		if (totalWeight > 0) {

			for (int i = 0; i < NUM_AVAILABLE_TECHS; i++) {
				if (nodes.size() > 0) {
					TechNode node = generateNode(totalWeight, nodes, weights);
					totalWeight -= weights.get(node);
					nodes.remove(node);
					availableNodes.put(node.id, node);
				}
			}

		}

	}

	private TechNode generateNode(float totalWeight,
			List<TechNode> nodes,
			Map<TechNode, Double> weights) {
		double rnd = Math.random() * totalWeight;
		int i;
		for (i = 0; rnd >= 0 && i < nodes.size(); i++) {
			TechNode tn = nodes.get(i);
			rnd -= weights.get(tn);

		}
		if (i == 0) {
			i = 1;
		}
		TechNode tn = nodes.get(i - 1);
		return tn;
	}

	public static final PlayerTechDataExtendedProps get(EntityPlayer player) {
		return (PlayerTechDataExtendedProps) player
				.getExtendedProperties(TECHDATA);
	}

	public Map<TechNode, NodeProgress> getNodes() {
		return nodes;
	}

	public double addProgress(TechNode tn,
			ResearchType spendType,
			double spendValue) {
		// spendValue /= 50;
		ResearchType type = spendType;
		while (!tn.costs.containsKey(type) && type != null) {
			type = type.getParentType();
		}
		if (type == null) {
			player.addChatMessage(new ChatComponentText(tn.id
					+ " does not require any " + spendType.name + "."));
		}
		if (!nodes.containsKey(tn)) {
			nodes.put(tn, new NodeProgress(tn, type, 0));
		}
		if (nodes.get(tn).progressLeft(type) <= 0) {
			player.addChatMessage(new ChatComponentText(
					"Already invested maximum amount of " + type.name
							+ " into " + tn.id + "."));
		}

		nodes.get(tn).progress.put(type, nodes.get(tn).getProgress(type)
				+ spendValue);
		if (nodes.get(tn).progressLeft(type) < 0) {
			spendValue += nodes.get(tn).progressLeft(type);
			nodes.get(tn).progress
					.put(type, nodes.get(tn).node.costs.get(type));
		}
		player.addChatMessage(new ChatComponentText(spendValue + " "
				+ type.name + " points spent towards " + tn.getDisplayName()
				+ " (" + (100 * nodes.get(tn).getTotalProgress()) + "%)"));
		if (nodes.get(tn).isComplete()) {
			player.addChatMessage(new ChatComponentText("You have completed "
					+ tn.getDisplayName()));

			for (String str : tn.getUnlockedDisplay()) {
				player.addChatMessage(new ChatComponentText(
						"You have unlocked " + str));

			}
			for (ResearchType subType : tn.getSubtypesUnlocked()) {
				player.addChatMessage(new ChatComponentText(
						"You now know that some "
								+ subType.getParentType()
										.getDiscoveredType(this)
										.getDisplayName()
								+ " can be specialised as "
								+ subType.getDisplayName()));
			}

			regenerateTechChoices();

		}
		sendPacket();
		return spendValue;
	}

	public boolean hasCompleted(TechNode tn) {
		return nodes.containsKey(tn) && nodes.get(tn).isComplete();
	}

	public void reset() {
		nodes.clear();
		experiments.clear();
		researchPoints.clear();
		regenerateTechChoices();
		sendPacket();

	}

	public void sendPacket() {
		if (player instanceof EntityPlayerMP) {
			EntityPlayerMP emp = ((EntityPlayerMP) player);
			TechResearch.snw.sendTo(new PacketTechResearch(this), emp);
		}
	}

	public void setNodes(Map<TechNode, NodeProgress> nodes) {
		this.nodes = nodes;

	}

	private double roundToTenth(double n) {
		return Math.round(n * 10) / 10.0;
	}

	public boolean hasCompleted(String tid) {
		return hasCompleted(TechTree.nodes.get(tid));
	}

	// private double addResearch(ResearchType researchType,
	// Experiment exp,
	// double amount) {
	// double amt = amount;
	// ResearchType discoveredType = researchType;
	// while (!hasDiscovered(discoveredType)) {
	// discoveredType = discoveredType.getParentType();
	// }
	// double threshhold = 1.0;
	// if (experiments.containsKey(exp)) {
	// int q = experiments.get(exp);
	// amt /= q;
	// amt = roundToTenth(amt);
	// experiments.put(exp, q + 1);
	//
	// if (amt < threshhold
	// && roundToTenth(amount / (q - 1)) >= threshhold) {
	// player.addChatMessage(new ChatComponentText(
	// "You can no longer gain " + discoveredType.name
	// + " knowledge by observing " + exp.name
	// + " here."));
	// }
	// } else {
	// experiments.put(exp, 1);
	// }
	// if (amt < threshhold) {
	// return 0;
	// }
	//
	// player.addChatMessage(new ChatComponentText("Your observations of "
	// + exp.name + " have earned you " + amt + " "
	// + discoveredType.name
	// + (discoveredType.name.equals("research") ? "" : " research.")));
	// return amt;
	//
	// }

	public Map<Experiment, PlayerExperimentData> getExperiments() {
		return experiments;
	}

	public void setExperiments(Map<Experiment, PlayerExperimentData> experiments) {
		this.experiments = experiments;

	}

	/**
	 * Add research points with a context
	 * */
	public <T> void addResearchPoints(Experiment<T> exp,
			double multiplier,
			T context) {

		incrementExperiment(exp, context);
		Map<ResearchType, Double> discoveredPointsSet = new HashMap<ResearchType, Double>();
		Map<ResearchType, Double> values = exp.getValues(this,
				multiplier,
				context);
		for (Entry<ResearchType, Double> value : values.entrySet()) {
			ResearchType discovered = value.getKey().getDiscoveredType(this);
			// if undiscovered, or -1, set to 0
			if (!discoveredPointsSet.containsKey(discovered)
					|| (discoveredPointsSet.get(discovered) == -1 && value
							.getValue() > 0)) {
				discoveredPointsSet.put(discovered, 0.0);
			}

			addPoints(exp, value.getKey(), value.getValue());

			discoveredPointsSet.put(discovered,
					discoveredPointsSet.get(discovered) + value.getValue());

		}
		for (Entry<ResearchType, Double> entry : discoveredPointsSet.entrySet()) {
			if (entry.getValue() == -1) {
				player.addChatMessage(new ChatComponentText(
						"You can no longer gain "
								+ entry.getKey().getDisplayName()
								+ " knowledge by observing "
								+ exp.getDisplayName() + " here."));
				continue;
			}
			player.addChatMessage(new ChatComponentText("Your observations of "
					+ exp.getDisplayName()
					+ " have earned you "
					+ entry.getValue()
					+ " "
					+ entry.getKey().getDisplayName()
					+ (entry.getKey().name.equals("research") ? ""
							: " research.")));
		}

		sendPacket();
	}

	private void addPoints(Experiment exp, ResearchType type, Double value) {
		double gain = value;
		if (gain > 0) {
			researchPoints.put(type, getResearchPoints(type) + gain);
		}

		if (gain == 0) {
			return;
		}

		ResearchType discoveredType = type.getDiscoveredType(this);
		while (!hasDiscovered(discoveredType)) {
			discoveredType = discoveredType.getParentType();
		}

	}

	private <TContext> void incrementExperiment(Experiment<TContext> exp,
			TContext context) {
		if (!experiments.containsKey(exp)) {
			experiments.put(exp, exp.getBlankData());
		}
		experiments.get(exp).useExperiment(context);

	}

	private void incrementExperiment(Experiment exp) {
		incrementExperiment(exp, null);
	}

	/** Adds research points for this experiment with a multiplier */
	public void addResearchPoints(Experiment exp, double multiplier) {
		addResearchPoints(exp, multiplier, null);

	}

	/** Adds research points for this experiment */
	public void addResearchPoints(Experiment exp) {
		addResearchPoints(exp, 1.0);
	}

	/** This is cheating. */
	public void forceAddResearchPoints(String type, double amount) {
		researchPoints.put(ResearchType.getType(type), getResearchPoints(type)
				+ amount);
	}

	public double getResearchPoints(ResearchType type) {
		return type.getValue(this);
	}

	public double getDisplayResearchPoints(String name) {
		return Math
				.round(ResearchType.getType(name).getDisplayValue(this) * 100) / 100.0;
	}

	public double getResearchPoints(String type) {
		return getResearchPoints(ResearchType.getType(type));
	}

	public boolean hasDiscovered(ResearchType researchType) {
		if (researchType.isStartType()) {
			return true;
		}
		for (Entry<TechNode, NodeProgress> node : this.nodes.entrySet()) {
			if (this.hasCompleted(node.getKey())) {
				List<ResearchType> unlocks = node.getKey()
						.getSubtypesUnlocked();
				if (unlocks.contains(researchType)) {
					return true;
				}
			}
		}
		return false;
	}

	public double spendResearchPoints(ResearchType type,
			double amount,
			boolean doSpend) {
		if (amount == 0) {
			return 0;
		}
		if (type.isBaseType()) {
			double value = Math.min(amount, getResearchPoints(type));
			if (doSpend) {
				researchPoints.put(type, getResearchPoints(type) - value);
			}
			return value;

		}

		if (getResearchPoints(type) < amount) {
			return 0;
		}
		if (doSpend) {
			spendChildren(type.getChildren(), amount);
		}
		return amount;

	}

	private void spendChildren(List<ResearchType> children, double amount) {
		// double spendAmt = amount / children.size();
		double total = 0;
		for (ResearchType child : children) {
			total += child.getValue(this);
		}
		for (ResearchType child : children) {
			double amt = amount * child.getValue(this) / total;
			spendResearchPoints(child, amt, true);
		}
		// for (ResearchType child : children) {
		//
		// double amt = spendResearchPoints(child, spendAmt, false);
		// if (amt < spendAmt) {
		// List<ResearchType> newChildren = new ArrayList<ResearchType>(
		// children);
		// newChildren.remove(child);
		// spendChildren(newChildren, spendAmt);
		// } else {
		// spendResearchPoints(child, spendAmt, true);
		// }
		// }

	}

	public TechNode getAvailableNode(String node) {

		return MapUtils.getOrDefault(availableNodes, node, null);
	}

	public Collection<TechNode> getAvailableNodes() {
		return getAvailableNodes(true);
	}

	public Collection<TechNode> getAvailableNodes(boolean regen) {
		if (regen && availableNodes.size() < NUM_AVAILABLE_TECHS) {
			regenerateTechChoices();
		}
		return availableNodes.values();
	}

	public boolean hasProgress(TechNode node) {
		return nodes.containsKey(node);
	}

	public NodeProgress getProgress(TechNode node) {
		return nodes.get(node);
	}

}
