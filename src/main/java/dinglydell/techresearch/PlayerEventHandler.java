package dinglydell.techresearch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerEventHandler {

	@SubscribeEvent
	public void playerClone(PlayerEvent.Clone clone) {
		PlayerTechDataExtendedProps ptdepNew = PlayerTechDataExtendedProps
				.get(clone.entityPlayer);
		PlayerTechDataExtendedProps ptdepOld = PlayerTechDataExtendedProps
				.get(clone.original);
		ptdepNew.biology = ptdepOld.biology;
		ptdepNew.chemistry = ptdepOld.chemistry;
		ptdepNew.physics = ptdepOld.physics;
	}

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {

		if (event.entity instanceof EntityPlayer) {

			event.entity
					.registerExtendedProperties(
							PlayerTechDataExtendedProps.TECHDATA,
							new PlayerTechDataExtendedProps(
									(EntityPlayer) event.entity));
		}
	}

}
