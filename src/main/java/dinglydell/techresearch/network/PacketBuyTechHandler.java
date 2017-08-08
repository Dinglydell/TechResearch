package dinglydell.techresearch.network;

import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dinglydell.techresearch.NodeProgress;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.ResearchType;

public class PacketBuyTechHandler implements
		IMessageHandler<PacketBuyTech, IMessage> {

	@Override
	public IMessage onMessage(PacketBuyTech message, MessageContext ctx) {
		EntityPlayer player = (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer
				: ctx.getServerHandler().playerEntity);
		PlayerTechDataExtendedProps ptdep = PlayerTechDataExtendedProps
				.get(player);
		if (message.node == null) {
			player.addChatMessage(new ChatComponentText("Not a node!"));
			return null;
		}
		if (ptdep.hasCompleted(message.node)) {
			player.addChatMessage(new ChatComponentText(
					"Node already complete!"));
			return null;
		}
		NodeProgress progress = ptdep.getProgress(message.node);
		for (Entry<ResearchType, Double> cost : message.node.costs.entrySet()) {

			double spend = cost.getValue();
			if (progress != null) {
				spend -= progress.getProgress(cost.getKey());
			}
			// ensure they don't spend more than they have
			spend = ptdep.spendResearchPoints(cost.getKey(), spend, false);
			ptdep.spendResearchPoints(cost.getKey(), spend, true);
			ptdep.addProgress(message.node, cost.getKey(), spend);

		}
		return null;
	}

}
