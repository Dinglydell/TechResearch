package dinglydell.techresearch;

import net.minecraft.nbt.NBTTagCompound;

public class NodeProgress {

	public TechNode node;
	public double progress;

	public NodeProgress(TechNode node, double progress) {
		this.node = node;
		this.progress = progress;

	}

	public NodeProgress(NBTTagCompound nbt) {
		node = TechTree.nodes.get(nbt.getString("id"));
		progress = nbt.getDouble("progress");
	}

	public NBTTagCompound getTag() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("id", node.id);
		nbt.setDouble("progress", progress);
		return nbt;
	}

	public boolean isComplete() {
		return progress >= 1;
	}
}
