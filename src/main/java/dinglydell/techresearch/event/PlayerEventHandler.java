package dinglydell.techresearch.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dinglydell.techresearch.PlayerTechDataExtendedProps;

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

}
