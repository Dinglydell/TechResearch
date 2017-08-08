package dinglydell.techresearch.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import scala.util.Random;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.experiment.Experiment;

public class PlayerEventHandler {

	@SubscribeEvent
	public void playerClone(PlayerEvent.Clone clone) {
		PlayerTechDataExtendedProps ptdepNew = PlayerTechDataExtendedProps
				.get(clone.entityPlayer);
		PlayerTechDataExtendedProps ptdepOld = PlayerTechDataExtendedProps
				.get(clone.original);
		ptdepNew.researchPoints = ptdepOld.researchPoints;
		// ptdepNew.biology = ptdepOld.biology;
		// ptdepNew.engineering = ptdepOld.engineering;
		// ptdepNew.physics = ptdepOld.physics;
		ptdepNew.setNodes(ptdepOld.getNodes());
		ptdepNew.setExperiments(ptdepOld.getExperiments());
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {

		if (event.entity instanceof EntityPlayer) {

			event.entity
					.registerExtendedProperties(PlayerTechDataExtendedProps.TECHDATA,
							new PlayerTechDataExtendedProps(
									(EntityPlayer) event.entity));
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayerMP && !event.world.isRemote) {
			PlayerTechDataExtendedProps ptdep = PlayerTechDataExtendedProps
					.get((EntityPlayer) event.entity);
			ptdep.sendPacket();
		}
	}

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
					.addResearchPoints(Experiment.anvil);
		}
	}

}
