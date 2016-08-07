package dinglydell.techresearch;

import java.io.File;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import dinglydell.techresearch.block.BlockPendulum;
import dinglydell.techresearch.block.TRBlocks;
import dinglydell.techresearch.recipe.CraftingReplacementHandler;

@Mod(modid = TechResearch.MODID, version = TechResearch.VERSION)
public class TechResearch {
	public static final String MODID = "techresearch";
	public static final String VERSION = "0.1";

	public static SimpleNetworkWrapper snw;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		registerEventHandlers();

		registerPacketHandlers();

		readConfig(event);

		registerBlocks();
	}

	private void registerBlocks() {
		TRBlocks.pendulum = new BlockPendulum();
		GameRegistry.registerBlock(TRBlocks.pendulum, "pendulum");

	}

	private void readConfig(FMLPreInitializationEvent event) {
		Configuration techtree = new Configuration(new File(
				event.getModConfigurationDirectory(), MODID + "/TechTree.cfg"));
		techtree.load();
		boolean defaults = techtree
				.getBoolean("defaultTech",
						"General",
						true,
						"Determines whether the mod should use the default tech. If set to true, the mod will auto-(re)generate any missing configuration for the default techtree");
		if (defaults) {
			setDefaults(techtree);
		}
		Set<String> categs = techtree.getCategoryNames();
		for (String c : categs) {
			if (c.equals("general")) {
				continue;
			}
			String[] unlocks = techtree.getStringList("unlocks",
					c,
					new String[] {},
					"Item IDs unlocked by this node");
			float biology = techtree
					.getFloat("biology",
							c,
							0,
							0,
							Float.MAX_VALUE,
							"The amount biology research is likely to contribute towards discovering this node");
			float chemistry = techtree
					.getFloat("chemistry",
							c,
							0,
							0,
							Float.MAX_VALUE,
							"The amount chemistry research is likely to contribute towards discovering this node");
			float physics = techtree
					.getFloat("physics",
							c,
							0,
							0,
							Float.MAX_VALUE,
							"The amount physics research is likely to contribute towards discovering this node");
			String[] requiresAll = techtree
					.getStringList("requiresAll",
							c,
							new String[] {},
							"The node requires all of these nodes in order to be unlocked");
			String[] requiresAny = techtree.getStringList("requiresAny",
					c,
					new String[] {},
					"The node requires any of these nodes to be unlocked");

			TechTree.AddTechNode(new TechNode(c, unlocks, biology, chemistry,
					physics, requiresAll, requiresAny));

		}
		techtree.save();
	}

	private void registerPacketHandlers() {
		snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		snw.registerMessage(PacketTechHandler.class,
				PacketTechResearch.class,
				0,
				Side.CLIENT);
	}

	private void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		TechTree.addHandler(new CraftingReplacementHandler());

		registerRecipes();
	}

	private void registerRecipes() {
		GameRegistry.addRecipe(new ShapedOreRecipe(TRBlocks.pendulum,
				new Object[] { "ppp",
						"psp",
						"pbp",
						Character.valueOf('p'),
						"plankWood",
						Character.valueOf('s'),
						"stickWood",
						Character.valueOf('b'),
						Blocks.wooden_button }));

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		TechTree.replace();
	}

	private void setDefaults(Configuration techtree) {
		addDefaultNode(techtree,
				"stonetools",
				new String[] { "minecraft:stone_pickaxe",
						"minecraft:stone_axe",
						"minecraft:stone_sword",
						"minecraft:stone_shovel",
						"minecraft:stone_hoe" },
				0f,
				2f,
				3f);

		addDefaultNode(techtree,
				"irontools",
				new String[] { "minecraft:iron_pickaxe",
						"minecraft:iron_axe",
						"minecraft:iron_sword",
						"minecraft:iron_shovel",
						"minecraft:iron_hoe" },
				0f,
				1.5f,
				2.5f,
				new String[] { "stonetools", "furnace" });

		addDefaultNode(techtree,
				"furnace",
				new String[] { "minecraft:furnace" },
				0f,
				1f,
				0.8f);

	}

	private void addDefaultNode(Configuration techtree,
			String id,
			String[] unlocks,
			float biology,
			float chemistry,
			float physics) {
		addDefaultNode(techtree,
				id,
				unlocks,
				biology,
				chemistry,
				physics,
				new String[] {});
	}

	private void addDefaultNode(Configuration techtree,
			String id,
			String[] unlocks,
			float biology,
			float chemistry,
			float physics,
			String[] requiresAll) {
		addDefaultNode(techtree,
				id,
				unlocks,
				biology,
				chemistry,
				physics,
				requiresAll,
				new String[] {});

	}

	private void addDefaultNode(Configuration techtree,
			String id,
			String[] unlocks,
			float biology,
			float chemistry,
			float physics,
			String[] requiresAll,
			String[] requiresAny) {
		techtree.getStringList("unlocks",
				id,
				unlocks,
				"List of items this node unlocks");
		getPositiveFloat(techtree, "biology", id, biology);
		getPositiveFloat(techtree, "chemistry", id, chemistry);
		getPositiveFloat(techtree, "physics", id, physics);
		techtree.getStringList("requiresAll", id, requiresAll, "");
		techtree.getStringList("requiresAny", id, requiresAny, "");
	}

	private float getPositiveFloat(Configuration cfg,
			String name,
			String category,
			float defaultValue,
			String comment) {
		return cfg.getFloat(name,
				category,
				defaultValue,
				0,
				Float.MAX_VALUE,
				comment);
	}

	private float getPositiveFloat(Configuration cfg,
			String name,
			String category,
			float defaultValue) {
		return getPositiveFloat(cfg, name, category, defaultValue, "");
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {

		event.registerServerCommand(new CommandTech());
	}
}
