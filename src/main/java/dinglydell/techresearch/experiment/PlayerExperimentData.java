package dinglydell.techresearch.experiment;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;

/** Data about experiments specific to a player */
public class PlayerExperimentData {
	/** Number of times the experiment has been carried out */
	public int uses;
	/** time the experiment was last carried out in minecraft time */
	public long lastUsed;

	public PlayerExperimentData(NBTTagCompound data) {
		uses = data.getInteger("uses");
		lastUsed = data.getLong("lastUsed");

	}

	public PlayerExperimentData() {
		uses = 0;
		lastUsed = 0;
	}

	public NBTTagCompound getNBTData() {
		NBTTagCompound data = new NBTTagCompound();

		data.setInteger("uses", uses);
		data.setLong("lastUsed", lastUsed);
		return data;
	}

	public PlayerExperimentData useExperiment() {
		uses++;

		lastUsed = Minecraft.getMinecraft().theWorld.getWorldTime();
		return this;
	}
}
