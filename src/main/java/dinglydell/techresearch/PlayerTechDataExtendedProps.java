package dinglydell.techresearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerTechDataExtendedProps implements IExtendedEntityProperties {

	public static final String TECHDATA = "techData";
	public static final String BIOLOGY = "biology";
	public static final String CHEMISTRY = "chemistry";
	public static final String PHYSICS = "physics";
	public static final String TECH_NODES = "techNodes";

	protected final EntityPlayer player;

	protected double biology = 0;
	protected double chemistry = 0;
	protected double physics = 0;
	private Map<TechNode, NodeProgress> nodes = new HashMap<TechNode, NodeProgress>();

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
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		NBTTagCompound techData = nbt.getCompoundTag(TECHDATA);
		biology = techData.getDouble(BIOLOGY);
		chemistry = techData.getDouble(CHEMISTRY);
		physics = techData.getDouble(PHYSICS);
		NBTTagList techNodes = techData.getTagList(TECH_NODES, 10);
		for (int i = 0; i < techNodes.tagCount(); i++) {
			NodeProgress np = new NodeProgress(techNodes.getCompoundTagAt(i));
			getNodes().put(np.node, np);
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

}
