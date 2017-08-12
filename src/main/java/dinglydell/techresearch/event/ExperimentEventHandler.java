package dinglydell.techresearch.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import scala.util.Random;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.experiment.Experiment;

public class ExperimentEventHandler {

	@SubscribeEvent
	public void onEntityFall(LivingFallEvent event) {
		if (event.entity instanceof EntityPlayer
				&& !event.entity.worldObj.isRemote) {
			EntityPlayer player = (EntityPlayer) event.entity;
			if (event.distance >= 4) {
				System.out.println("player fell " + event.distance + " blocks");
				Random rnd = new Random();
				if (rnd.nextDouble() < event.distance / 10) {
					PlayerTechDataExtendedProps.get(player)
							.addResearchPoints(Experiment.fall,
									Math.min(Math.floor(event.distance), 20));
				}

			}
		}
	}

	@SubscribeEvent
	public void onPlayerAnvil(AnvilRepairEvent event) {
		if (!event.entity.worldObj.isRemote) {
			EntityPlayer player = (EntityPlayer) event.entity;
			PlayerTechDataExtendedProps.get(player)
					.addResearchPoints(Experiment.anvil, 1, event.output);
		}
	}

	@SubscribeEvent
	public void onPlayerCraft(ItemCraftedEvent event) {
		if (!event.player.worldObj.isRemote) {
			PlayerTechDataExtendedProps.get(event.player)
					.addResearchPoints(Experiment.crafting, 1, event.crafting);
		}
	}

}
