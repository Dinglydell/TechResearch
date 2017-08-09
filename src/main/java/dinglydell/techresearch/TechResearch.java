package dinglydell.techresearch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import scala.actors.threadpool.Arrays;
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
import dinglydell.techresearch.event.PlayerEventHandler;
import dinglydell.techresearch.event.TechKeyBindings;
import dinglydell.techresearch.network.PacketBuyTech;
import dinglydell.techresearch.network.PacketBuyTechHandler;
import dinglydell.techresearch.network.PacketTechHandler;
import dinglydell.techresearch.network.PacketTechResearch;
import dinglydell.techresearch.recipe.CraftingReplacementHandler;
import dinglydell.techresearch.researchtype.ResearchType;
import dinglydell.techresearch.techtree.TechNode;
import dinglydell.techresearch.techtree.TechTree;

@Mod(modid = TechResearch.MODID, version = TechResearch.VERSION)
public class TechResearch {
	public static final String MODID = "techresearch";
	public static final String VERSION = "0.1";

	public static SimpleNetworkWrapper snw;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		registerEventHandlers();

		registerPacketHandlers();
		TechKeyBindings.RegisterKeyBindings();
		readConfig(event);

		registerBlocks();
	}

	private void registerBlocks() {
		TRBlocks.pendulum = (BlockPendulum) new BlockPendulum()
				.setBlockName("pendulum");
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
			String[] subTypeUnlocks = techtree.getStringList("subTypeUnlocks",
					c,
					new String[] {},
					"Types of research points discovered by this node");
			String type = techtree
					.getString("type",
							c,
							"application",
							"theory or application. Generally theories are prerequisites to application techs which provide something useful.");
			Map<ResearchType, Double> costs = new HashMap<ResearchType, Double>();
			for (ResearchType rt : ResearchType.getTypes().values()) {
				if (techtree.hasKey(c, rt.name)) {
					double value = (double) techtree
							.getFloat(rt.name,
									c,
									0,
									0,
									Float.MAX_VALUE,
									"The amount "
											+ rt.name
											+ " research is likely to contribute towards discovering this node");
					if (value > 0) {
						costs.put(rt, value);
					}
				}
			}
			// float biology = techtree
			// .getFloat("biology",
			// c,
			// 0,
			// 0,
			// Float.MAX_VALUE,
			// "The amount biology research is likely to contribute towards discovering this node");
			// float engineering = techtree
			// .getFloat("engineering",
			// c,
			// 0,
			// 0,
			// Float.MAX_VALUE,
			// "The amount engineering research is likely to contribute towards discovering this node");
			// float physics = techtree
			// .getFloat("physics",
			// c,
			// 0,
			// 0,
			// Float.MAX_VALUE,
			// "The amount physics research is likely to contribute towards discovering this node");
			String[] requiresAll = techtree
					.getStringList("requiresAll",
							c,
							new String[] {},
							"The node requires all of these nodes in order to be unlocked");
			String[] requiresAny = techtree.getStringList("requiresAny",
					c,
					new String[] {},
					"The node requires any of these nodes to be unlocked");
			String[] requiresPoints = techtree
					.getStringList("requiresPoints",
							c,
							new String[] {},
							"The node requires the player to have research points for all of these types. This is usually used for unlocking new research point types (so you can only get the tech to unlock physics if you unknowingly have physics points)");
			String displayName = techtree.getString("displayString",
					c,
					"tech.techresearch." + c,
					"Localisation string or display string of the tech");

			TechTree.AddTechNode(new TechNode(c, type, costs)
					.setDisplayName(displayName)
					.addItemsUnlocked(Arrays.asList(unlocks))
					.addSubtypesUnlocked(Arrays.asList(subTypeUnlocks)));

		}
		techtree.save();
	}

	private void registerPacketHandlers() {
		snw = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		snw.registerMessage(PacketTechHandler.class,
				PacketTechResearch.class,
				0,
				Side.CLIENT);

		snw.registerMessage(PacketBuyTechHandler.class,
				PacketBuyTech.class,
				1,
				Side.SERVER);
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
				addResearch(createTechMap(), 10));

		addDefaultNode(techtree,
				"irontools",
				"application",
				new String[] { "minecraft:iron_pickaxe",
						"minecraft:iron_axe",
						"minecraft:iron_sword",
						"minecraft:iron_shovel",
						"minecraft:iron_hoe" },
				addSmithing(addMetallurgy(createTechMap(), 10), 15),
				new String[] { "stonetools", "furnace" });

		addDefaultNode(techtree,
				"furnace",
				new String[] { "minecraft:furnace" },
				addEngineering(addScience(createTechMap(), 10), 10));

		addDefaultNode(techtree,
				"diamondtools",
				"application",
				new String[] { "minecraft:diamond_pickaxe",
						"minecraft:diamond_axe",
						"minecraft:diamond_sword",
						"minecraft:diamond_shovel",
						"minecraft:diamond_hoe" },
				addEngineering(addPhysics(createTechMap(), 10), 50),
				new String[] { "irontools" });

		for (Entry<String, ResearchType> type : ResearchType.getTypes()
				.entrySet()) {
			if (!type.getValue().hasParentType()) {
				continue;
			}
			Map<ResearchType, Double> map = createTechMap();
			map.put(type.getValue().getParentType(), 20.0);
			addDefaultNode(techtree,
					type.getValue().name,
					new String[] {},
					new String[] { type.getValue().name },
					map);

		}

		// addDefaultNode(techtree,
		// "engineering",
		// new String[] {},
		// new String[] { ResearchType.engineering.name },
		// addResearch(createTechMap(), 20));
		// // science
		// addDefaultNode(techtree,
		// "physics",
		// new String[] {},
		// new String[] { ResearchType.physics.name },
		// addScience(createTechMap(), 20));
		// addDefaultNode(techtree,
		// "biology",
		// new String[] {},
		// new String[] { ResearchType.biology.name },
		// addScience(createTechMap(), 20));
		// addDefaultNode(techtree,
		// "materials",
		// new String[] {},
		// new String[] { ResearchType.materials.name },
		// addScience(createTechMap(), 20));
		// // engineering
		// addDefaultNode(techtree,
		// "smithing",
		// new String[] {},
		// new String[] { ResearchType.smithing.name },
		// addEngineering(createTechMap(), 20));
		//
		// //physics
		// addDefaultNode(techtree,
		// "motion",
		// new String[] {},
		// new String[] { ResearchType.motion.name },
		// addPhysics(createTechMap(), 20));
		// addDefaultNode(techtree,
		// "electrics",
		// new String[] {},
		// new String[] { ResearchType.electrics.name },
		// addPhysics(createTechMap(), 20));
		//
		// //biology
		// addDefaultNode(techtree,
		// "botany",
		// new String[] {},
		// new String[] { ResearchType.botany.name },
		// addPhysics(createTechMap(), 20));

	}

	private Map<ResearchType, Double> addSmithing(Map<ResearchType, Double> map,
			double amt) {
		map.put(ResearchType.smithing, amt);
		return map;
	}

	private Map<ResearchType, Double> addMetallurgy(Map<ResearchType, Double> map,
			double amt) {
		map.put(ResearchType.metallurgy, amt);
		return map;
	}

	private Map<ResearchType, Double> createTechMap() {
		return new HashMap<ResearchType, Double>();
	}

	/** Generates a tech map from some basic types of tech */
	private Map<ResearchType, Double> addResearch(Map<ResearchType, Double> techMap,
			double research) {
		techMap.put(ResearchType.research, research);
		return techMap;
	}

	private Map<ResearchType, Double> addEngineering(Map<ResearchType, Double> techMap,
			double engineering) {
		techMap.put(ResearchType.engineering, engineering);
		return techMap;
	}

	private Map<ResearchType, Double> addScience(Map<ResearchType, Double> techMap,
			double points) {
		techMap.put(ResearchType.science, points);
		return techMap;
	}

	private Map<ResearchType, Double> addPhysics(Map<ResearchType, Double> techMap,
			double points) {
		techMap.put(ResearchType.physics, points);
		return techMap;
	}

	private void addDefaultNode(Configuration techtree,
			String id,
			String type,
			String[] unlocks,
			Map<ResearchType, Double> costs) {
		addDefaultNode(techtree, id, type, unlocks, costs, new String[] {});
	}

	private void addDefaultNode(Configuration techtree,
			String id,
			String[] unlocks,
			Map<ResearchType, Double> costs) {
		addDefaultNode(techtree,
				id,
				"application",
				unlocks,
				costs,
				new String[] {});
	}

	private void addDefaultNode(Configuration techtree,
			String id,
			String[] unlocks,
			String[] subTypesUnlocks,
			Map<ResearchType, Double> costs) {
		addDefaultNode(techtree,
				id,
				"theory",
				unlocks,
				subTypesUnlocks,
				costs,
				new String[] {},
				new String[] {},
				"research.techresearch." + id);

	}

	private void addDefaultNode(Configuration techtree,
			String id,
			String type,
			String[] unlocks,
			Map<ResearchType, Double> costs,
			String[] requiresAll) {
		addDefaultNode(techtree,
				type,
				id,
				unlocks,
				costs,
				requiresAll,
				new String[] {});

	}

	private void addDefaultNode(Configuration techtree,
			String id,
			String type,
			String[] unlocks,
			Map<ResearchType, Double> costs,
			String[] requiresAll,
			String[] requiresAny) {
		addDefaultNode(techtree,
				type,
				id,
				unlocks,
				new String[] {},
				costs,
				requiresAll,
				requiresAny);
	}

	private void addDefaultNode(Configuration techtree,
			String id,
			String type,
			String[] unlocks,
			String[] subTypeUnlocks,
			Map<ResearchType, Double> costs,
			String[] requiresAll,
			String[] requiresAny) {
		addDefaultNode(techtree,
				id,
				type,
				unlocks,
				subTypeUnlocks,
				costs,
				requiresAll,
				requiresAny,
				"tech.techresearch." + id);
	}

	private void addDefaultNode(Configuration techtree,
			String id,
			String type,
			String[] unlocks,
			String[] subTypeUnlocks,
			Map<ResearchType, Double> costs,
			String[] requiresAll,
			String[] requiresAny,
			String displayName) {
		techtree.getStringList("unlocks",
				id,
				unlocks,
				"List of items this node unlocks");
		techtree.getStringList("subTypeUnlocks",
				id,
				subTypeUnlocks,
				"List of research types this node unlocks");
		for (Entry<ResearchType, Double> cost : costs.entrySet()) {
			getPositiveFloat(techtree, cost.getKey().name, id, cost.getValue()
					.floatValue());
		}
		// getPositiveFloat(techtree, "engineering", id, engineering);
		// getPositiveFloat(techtree, "physics", id, physics);
		techtree.getString("type", id, type, "");
		techtree.getStringList("requiresAll", id, requiresAll, "");
		techtree.getStringList("requiresAny", id, requiresAny, "");
		techtree.getStringList("requiresPoints", id, subTypeUnlocks, "");
		techtree.getString("displayString", id, displayName, "");
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
