package dinglydell.techresearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import cpw.mods.fml.common.registry.GameData;

public class PlayerTechDataExtendedProps implements IExtendedEntityProperties {

	public static final String TECHDATA = "techData";
	public static final String BIOLOGY = "biology";
	public static final String CHEMISTRY = "chemistry";
	public static final String PHYSICS = "physics";
	public static final String TECH_NODES = "techNodes";
	public static final String EXPERIMENTS = "experiments";
	public static final String EXPERIMENT = "experiment";
	public static final String QUANTITY = "quantity";

	protected final EntityPlayer player;

	protected double biology = 0;
	protected double chemistry = 0;
	protected double physics = 0;
	private Map<TechNode, NodeProgress> nodes = new HashMap<TechNode, NodeProgress>();
	private Map<Experiment, Integer> experiments = new HashMap<Experiment, Integer>();

	public PlayerTechDataExtendedProps(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		NBTTagCompound techData = new NBTTagCompound();
		nbt.setTag(TECHDATA, techData);
		techData.setDouble(BIOLOGY, biology);
		techData.setDouble(CHEMISTRY, chemistry);
		techData.setDouble(PHYSICS, physics);
		NBTTagList techNodes = new NBTTagList();
		techData.setTag(TECH_NODES, techNodes);
		for (Entry<TechNode, NodeProgress> np : getNodes().entrySet()) {
			techNodes.appendTag(np.getValue().getTag());
		}
		NBTTagList exps = new NBTTagList();
		techData.setTag(EXPERIMENTS, techNodes);
		for (Entry<Experiment, Integer> exp : experiments.entrySet()) {
			NBTTagCompound expTag = new NBTTagCompound();
			expTag.setInteger(EXPERIMENT, exp.getKey().ordinal());
			expTag.setInteger(QUANTITY, exp.getValue());
			techNodes.appendTag(expTag);
		}
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		NBTTagCompound techData = nbt.getCompoundTag(TECHDATA);
		biology = techData.getDouble(BIOLOGY);
		chemistry = techData.getDouble(CHEMISTRY);
		physics = techData.getDouble(PHYSICS);
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
			experiments.put(Experiment.values()[expTag.getInteger(EXPERIMENT)],
					expTag.getInteger(QUANTITY));
		}
	}

	@Override
	public void init(Entity ent, World world) {

	}

	public static final PlayerTechDataExtendedProps get(EntityPlayer player) {
		return (PlayerTechDataExtendedProps) player
				.getExtendedProperties(TECHDATA);
	}

	public Map<TechNode, NodeProgress> getNodes() {
		return nodes;
	}

	public void addProgress(TechNode tn, double spendValue) {
		spendValue /= 50;
		if (nodes.containsKey(tn)) {
			nodes.get(tn).progress += spendValue;
		} else {
			nodes.put(tn, new NodeProgress(tn, spendValue));
		}
		player.addChatMessage(new ChatComponentText("Points spent towards "
				+ tn.id + " (" + (100 * nodes.get(tn).progress) + "%)"));
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
		}
		sendPacket();
	}

	public boolean hasCompleted(TechNode tn) {
		return nodes.containsKey(tn) && nodes.get(tn).isComplete();
	}

	public void reset() {
		nodes.clear();
		experiments.clear();
		biology = 0;
		chemistry = 0;
		physics = 0;
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

	public void addPhysics(Experiment exp, double multiplier) {
		double amt = multiplier;
		if (experiments.containsKey(exp)) {
			int q = experiments.get(exp);
			amt /= q;
			amt = roundToTenth(amt);
			experiments.put(exp, q + 1);
			if (amt == 0 && roundToTenth(multiplier / (q - 1)) > 0) {
				player.addChatMessage(new ChatComponentText(
						"You can no longer gain physics knowledge by experimenting with "
								+ exp.name + " here."));
			}
		} else {
			experiments.put(exp, 1);
		}
		if (amt > 0) {
			physics += amt;
			player.addChatMessage(new ChatComponentText(
					"Your experiments with " + exp.name + " have earned you "
							+ amt + " physics research."));
			sendPacket();
		}
	}

	public void addPhysics(Experiment exp) {
		addPhysics(exp, exp.initialValue);
	}

	public Map<Experiment, Integer> getExperiments() {
		return experiments;
	}

	public void setExperiments(Map<Experiment, Integer> experiments) {
		this.experiments = experiments;

	}
}
