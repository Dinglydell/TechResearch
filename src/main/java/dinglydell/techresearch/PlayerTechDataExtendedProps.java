package dinglydell.techresearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import scala.actors.threadpool.Arrays;
import cpw.mods.fml.common.registry.GameData;

public class PlayerTechDataExtendedProps implements IExtendedEntityProperties {

	public static final String TECHDATA = "techData";
	public static final String BIOLOGY = "biology";
	public static final String ENGINEERING = "engineering";
	public static final String PHYSICS = "physics";
	public static final String RESEARCH_POINTS = "researchPoints";
	public static final String RESEARCH_NAME = "name";
	public static final String TECH_NODES = "techNodes";
	public static final String EXPERIMENTS = "experiments";
	public static final String EXPERIMENT = "experiment";
	public static final String QUANTITY = "quantity";

	protected final EntityPlayer player;

	protected Map<ResearchType, Double> researchPoints = new HashMap<ResearchType, Double>();
	// protected double biology = 0;
	// protected double engineering = 0;
	// protected double physics = 0;
	private Map<TechNode, NodeProgress> nodes = new HashMap<TechNode, NodeProgress>();
	private Map<String, TechNode> availableNodes = new HashMap<String, TechNode>();
	private Map<Experiment, Integer> experiments = new HashMap<Experiment, Integer>();

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
		for (Entry<Experiment, Integer> exp : experiments.entrySet()) {
			NBTTagCompound expTag = new NBTTagCompound();
			expTag.setString(EXPERIMENT, exp.getKey().name);
			expTag.setInteger(QUANTITY, exp.getValue());
			exps.appendTag(expTag);
		}
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
			researchPoints.put(ResearchType.getType(rTag
					.getString(RESEARCH_NAME)), rTag.getDouble(QUANTITY));
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
			experiments.put(Experiment.experiments.get(expTag
					.getString(EXPERIMENT)), expTag.getInteger(QUANTITY));
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
		List<TechNode> nodes = new ArrayList<TechNode>(TechTree.nodes.values());
		nodes.removeIf(new Predicate<TechNode>() {
			@Override
			public boolean test(TechNode tn) {
				if (hasCompleted(tn)) {
					return true;
				}
				for (ResearchType rt : tn.costs.keySet()) {
					if (!hasDiscovered(rt)) {
						return true;
					}
				}
				for (String tid : tn.requiresAll) {
					if (!hasCompleted(tid)) {
						return true;
					}
				}
				for (String tid : tn.requiresAny) {
					if (hasCompleted(tid)) {
						return false;
					}
				}
				return false;
			}
		});
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

			for (int i = 0; i < 3; i++) {
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
		if (rnd == 0) {
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

	public double addProgress(TechNode tn, ResearchType type, double spendValue) {
		// spendValue /= 50;

		if (nodes.containsKey(tn)) {
			if (nodes.get(tn).progressLeft(type) <= 0) {
				player.addChatMessage(new ChatComponentText(
						"Already invested maximum amount of " + type.name
								+ " into " + tn.id + "."));
			}

			nodes.get(tn).progress.put(type, nodes.get(tn).getProgress(type)
					+ spendValue);
		} else {
			nodes.put(tn, new NodeProgress(tn, type, spendValue));
		}
		if (nodes.get(tn).progressLeft(type) < 0) {
			spendValue += nodes.get(tn).progressLeft(type);
			nodes.get(tn).progress
					.put(type, nodes.get(tn).node.costs.get(type));
		}
		player.addChatMessage(new ChatComponentText(spendValue
				+ " points spent towards " + tn.id + " ("
				+ (100 * nodes.get(tn).getTotalProgress()) + "%)"));
		if (nodes.get(tn).isComplete()) {
			player.addChatMessage(new ChatComponentText("You have completed "
					+ tn.id));

			for (String item : tn.unlocks) {
				Item it = GameData.getItemRegistry().getObject(item);
				player.addChatMessage(new ChatComponentText(
						"You can now create "
								+ StatCollector.translateToLocal(it
										.getUnlocalizedName())));

			}
			for (String subType : tn.subTypeUnlocks) {
				player.addChatMessage(new ChatComponentText(
						"You now know that some "
								+ ResearchType.getType(subType).getParentType().name
								+ " can be specialised as " + subType));
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

	private double addResearch(ResearchType researchType,
			Experiment exp,
			double amount) {
		double amt = amount;
		if (experiments.containsKey(exp)) {
			int q = experiments.get(exp);
			amt /= q;
			amt = roundToTenth(amt);
			experiments.put(exp, q + 1);
			if (amt == 0 && roundToTenth(amount / (q - 1)) > 0) {
				player.addChatMessage(new ChatComponentText(
						"You can no longer gain " + researchType.name
								+ " knowledge by experimenting with "
								+ exp.name + " here."));
			}
		} else {
			experiments.put(exp, 1);
		}
		if (amt == 0) {
			return 0;
		}
		ResearchType discoveredType = researchType;
		while (!hasDiscovered(discoveredType)) {
			discoveredType = discoveredType.getParentType();
		}
		player.addChatMessage(new ChatComponentText("Your experiments with "
				+ exp.name + " have earned you " + amt + " "
				+ discoveredType.name
				+ (discoveredType.name.equals("research") ? "" : " research.")));
		return amt;

	}

	public Map<Experiment, Integer> getExperiments() {
		return experiments;
	}

	public void setExperiments(Map<Experiment, Integer> experiments) {
		this.experiments = experiments;

	}

	public void addResearchPoints(Experiment exp, double multiplier) {
		for (Entry<ResearchType, Double> value : exp.initialValues.entrySet()) {
			researchPoints.put(value.getKey(),
					getResearchPoints(value.getKey())
							+ addResearch(value.getKey(), exp, value.getValue()
									* multiplier));
		}

		sendPacket();
	}

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
				List<String> unlocks = Arrays
						.asList(node.getKey().subTypeUnlocks);
				if (unlocks.contains(researchType.name)) {
					return true;
				}
			}
		}
		return false;
	}

	public double spendResearchPoints(ResearchType type,
			double amount,
			boolean doSpend) {
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
		double spendAmt = amount / children.size();
		for (ResearchType child : children) {

			double amt = spendResearchPoints(child, spendAmt, false);
			if (amt < spendAmt) {
				List<ResearchType> newChildren = new ArrayList<ResearchType>(
						children);
				newChildren.remove(child);
				spendChildren(newChildren, spendAmt);
			} else {
				spendResearchPoints(child, spendAmt, true);
			}
		}

	}

	public TechNode getAvailableNode(String node) {

		return availableNodes.getOrDefault(node, null);
	}

	public Collection<TechNode> getAvailableNodes() {
		if (availableNodes.size() < 3) {
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
