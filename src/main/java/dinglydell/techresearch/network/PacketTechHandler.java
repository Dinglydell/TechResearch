package dinglydell.techresearch.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.gui.GuiResearch;

public class PacketTechHandler implements
		IMessageHandler<PacketTechResearch, IMessage> {

	public PacketTechHandler() {

	}

	@Override
	public IMessage onMessage(PacketTechResearch message, MessageContext ctx) {
		EntityPlayer player = (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer
				: ctx.getServerHandler().playerEntity);
		PlayerTechDataExtendedProps ptdep = PlayerTechDataExtendedProps
				.get(player);
		ptdep.loadNBTData(message.data);
		if (ctx.side.isClient()
				&& Minecraft.getMinecraft().currentScreen instanceof GuiResearch) {
			Minecraft.getMinecraft().thePlayer.closeScreen();
			GuiResearch.openGui();
		}
		return null;
	}

}