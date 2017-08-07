package dinglydell.techresearch.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dinglydell.techresearch.PlayerTechDataExtendedProps;

public class PacketTechResearch implements IMessage {

	NBTTagCompound data;

	public PacketTechResearch() {
	}

	public PacketTechResearch(PlayerTechDataExtendedProps ptdep) {
		data = new NBTTagCompound();
		ptdep.saveNBTData(data);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, data);
	}
}
