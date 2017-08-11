package dinglydell.techresearch.techtree;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import dinglydell.techresearch.researchtype.ResearchType;
import dinglydell.techresearch.util.MapUtils;

public class NodeProgress {

	public TechNode node;
	public Map<ResearchType, Double> progress;

	public NodeProgress(TechNode node, Map<ResearchType, Double> progress) {
		this.node = node;
		this.progress = progress;

	}

	public NodeProgress(NBTTagCompound nbt) {
		node = TechTree.nodes.get(nbt.getString("id"));
		progress = new HashMap<ResearchType, Double>();
		NBTTagList progressTag = nbt.getTagList("progress", 10);
		for (int i = 0; i < progressTag.tagCount(); i++) {
			NBTTagCompound rTag = progressTag.getCompoundTagAt(i);
			progress.put(ResearchType.getType(rTag.getString("name")),
					rTag.getDouble("progress"));
		}
	}

	public NodeProgress(TechNode tn, ResearchType type, double progress) {
		this.node = tn;
		this.progress = new HashMap<ResearchType, Double>();
		this.progress.put(type, progress);
	}

	public NBTTagCompound getTag() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("id", node.id);
		NBTTagList progressTag = new NBTTagList();
		nbt.setTag("progress", progressTag);
		for (Entry<ResearchType, Double> rsrch : progress.entrySet()) {
			NBTTagCompound rTag = new NBTTagCompound();
			rTag.setString("name", rsrch.getKey().name);
			rTag.setDouble("progress", rsrch.getValue());
			progressTag.appendTag(rTag);
		}
		return nbt;
	}

	public boolean isComplete() {
		for (Entry<ResearchType, Double> cost : node.costs.entrySet()) {
			if (MapUtils.getOrDefault(progress, cost.getKey(), 0.0) < cost
					.getValue()) {
				return false;
			}
		}
		return true;
	}

	public double getProgress(ResearchType type) {

		return MapUtils.getOrDefault(progress, type, 0.0);
	}

	public double getTotalProgress() {
		double totalProgress = 0;
		double totalCost = 0;
		for (Entry<ResearchType, Double> cost : node.costs.entrySet()) {
			totalProgress += getProgress(cost.getKey());
			totalCost += cost.getValue();

		}
		return totalProgress / totalCost;
	}

	public double progressLeft(ResearchType type) {

		return node.costs.get(type) - getProgress(type);
	}
}
