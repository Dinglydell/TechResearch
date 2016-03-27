package dinglydell.techresearch;

import java.io.File;
import java.util.Set;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = TechResearch.MODID, version = TechResearch.VERSION)
public class TechResearch {
	public static final String MODID = "techresearch";
	public static final String VERSION = "0.1";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());

		Configuration techtree = new Configuration(new File(
				event.getModConfigurationDirectory(), MODID + "/TechTree.cfg"));
		boolean defaults = techtree
				.getBoolean(
						"defaultTech",
						"",
						true,
						"Determines whether the mod should use the default tech. If set to true, the mod will auto-(re)generate any missing configuration for the default techtree");
		if (defaults) {
			setDefaults(techtree);
		}
		Set<String> categs = techtree.getCategoryNames();
		for (String c : categs) {
			String[] unlocks = techtree.getStringList("unlocks", c,
					new String[] {}, "Item IDs unlocked by this node");
			float biology = techtree
					.getFloat(
							"biology",
							c,
							0,
							0,
							Float.MAX_VALUE,
							"The amount biology research is likely to contribute towards discovering this node");
			float chemistry = techtree
					.getFloat(
							"chemistry",
							c,
							0,
							0,
							Float.MAX_VALUE,
							"The amount chemistry research is likely to contribute towards discovering this node");
			float physics = techtree
					.getFloat(
							"physics",
							c,
							0,
							0,
							Float.MAX_VALUE,
							"The amount physics research is likely to contribute towards discovering this node");
			String[] requiresAll = techtree
					.getStringList("requiresAll", c, new String[] {},
							"The node requires all of these nodes in order to be unlocked");
			String[] requiresAny = techtree.getStringList("requiresAny", c,
					new String[] {},
					"The node requires any of these nodes to be unlocked");

		}
	}

	public void postInit(FMLPostInitializationEvent event) {
		TechTree.replace();
	}

	private void setDefaults(Configuration techtree) {

	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {

		event.registerServerCommand(new CommandTech());
	}
}
