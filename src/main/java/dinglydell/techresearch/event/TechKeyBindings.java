package dinglydell.techresearch.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dinglydell.techresearch.gui.GuiResearch;

public class TechKeyBindings {
	public static KeyBinding openTable;

	public static void RegisterKeyBindings() {
		openTable = new KeyBinding("key.researchTable.desc", Keyboard.KEY_R,
				"key.techresearch.category");

		ClientRegistry.registerKeyBinding(openTable);

		FMLCommonHandler.instance().bus().register(new TechKeyBindings());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onEvent(KeyInputEvent event) {
		// DEBUG
		System.out.println("Key Input Event");

		// check each enumerated key binding type for pressed and take
		// appropriate action
		if (openTable.isPressed()) {
			// DEBUG
			System.out
					.println("Key binding = " + openTable.getKeyDescription());

			// do stuff for this key binding here
			// remember you may need to send packet to server
			if (Minecraft.getMinecraft().currentScreen instanceof GuiResearch) {
				Minecraft.getMinecraft().thePlayer.closeScreen();
			} else {
				GuiResearch.openGui();
			}

		}

	}
}
