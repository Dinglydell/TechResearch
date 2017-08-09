package dinglydell.techresearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dinglydell.techresearch.researchtype.ResearchType;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class CommandTech implements ICommand {
	public static final List<String> aliases = new ArrayList<String>();

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "tech";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "/tech <add|reset|spend|view> [type] [quantity]";
	}

	@Override
	public List getCommandAliases() {
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		World world = sender.getEntityWorld();
		EntityPlayer player = (EntityPlayer) sender;
		if (!world.isRemote) {
			if (args.length == 0) {
				sender.addChatMessage(new ChatComponentText(
						getCommandUsage(sender)));
				return;
			}
			final PlayerTechDataExtendedProps ptdep = PlayerTechDataExtendedProps
					.get(player);
			switch (args[0]) {
				case "reset":
					ptdep.reset();

					sender.addChatMessage(new ChatComponentText(
							"Your tech data has been reset."));
					break;
				case "view":
					for (ResearchType rt : ResearchType.getTypes().values()) {
						if (rt.isBaseDiscoveredType(ptdep)) {
							String displayName = StatCollector
									.translateToLocal("research.techresearch."
											+ rt.name);
							if (rt.isOtherType(ptdep)) {
								if (ptdep.getDisplayResearchPoints(rt.name) == 0) {
									continue;
								}

								displayName = StatCollector
										.translateToLocal("research.techresearch."
												+ rt.name + ".other");
							}
							sender.addChatMessage(new ChatComponentText(
									displayName
											+ ": "
											+ ptdep.getDisplayResearchPoints(rt.name)));
						}
					}
					// sender.addChatMessage(new ChatComponentText("Biology: "
					// + ptdep.getResearchPoints("biology")));
					// sender.addChatMessage(new
					// ChatComponentText("Engineering: "
					// + ptdep.getResearchPoints("engineering")));
					// sender.addChatMessage(new ChatComponentText("Physics: "
					// + ptdep.getResearchPoints("physics")));
					break;

				case "add":
					if (args.length < 3) {
						sender.addChatMessage(new ChatComponentText(
								getCommandUsage(sender)));
						return;
					}
					boolean success = true;
					switch (args[1]) {
						case "biology":
							ptdep.forceAddResearchPoints("biology",
									Double.parseDouble(args[2]));
							break;
						case "engineering":
							ptdep.forceAddResearchPoints("engineering",
									Double.parseDouble(args[2]));
							break;
						case "physics":
							ptdep.forceAddResearchPoints("phyisics",
									Double.parseDouble(args[2]));
							break;
						default:
							success = false;
							sender.addChatMessage(new ChatComponentText(
									getCommandUsage(sender)));
							break;
					}
					if (success) {
						sender.addChatMessage(new ChatComponentText("Added "
								+ args[2] + " " + args[1] + " points."));
					}
					break;

				case "spend":
					if (args.length < 4
							|| !ResearchType.getTypes().containsKey(args[1])) {
						sender.addChatMessage(new ChatComponentText(
								getCommandUsage(sender)));
						return;
					}

					double spendValue = Double.parseDouble(args[2]);

					if (ptdep
							.spendResearchPoints(ResearchType.getType(args[1]),
									spendValue,
									false) < spendValue) {
						sender.addChatMessage(new ChatComponentText(
								"You don't have enough " + args[1] + " points."));
						return;
					}
					TechNode tn = ptdep.getAvailableNode(args[3]);

					if (tn == null) {
						sender.addChatMessage(new ChatComponentText(
								"Not an available tech!"));
						return;
					}

					spendValue = ptdep.addProgress(tn,
							ResearchType.getType(args[1]),
							spendValue);
					ptdep.spendResearchPoints(ResearchType.getType(args[1]),
							spendValue,
							true);

					break;
				case "options":
					Collection<TechNode> nodes = ptdep.getAvailableNodes();
					sender.addChatMessage(new ChatComponentText(
							"Available nodes:"));
					for (TechNode node : nodes) {
						if (ptdep.hasProgress(node)) {
							sender.addChatMessage(new ChatComponentText("- ["
									+ node.type.getKey()
									+ "] "
									+ node.id
									+ " ("
									+ node.costsAsString(ptdep
											.getProgress(node)) + ")"));
						} else {
							sender.addChatMessage(new ChatComponentText("- ["
									+ node.type.getKey() + "] " + node.id
									+ " (" + node.costsAsString() + ")"));
						}
					}
					break;
				case "regenerate":
					ptdep.regenerateTechChoices();
					break;

				default:
					sender.addChatMessage(new ChatComponentText(
							"Unknown argument " + args[0]));
					sender.addChatMessage(new ChatComponentText(
							getCommandUsage(sender)));
			}
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {

		if (args.length == 1) {
			List<String> list = new ArrayList<String>();
			list.add("add");
			list.add("reset");
			list.add("spend");
			list.add("view");

			return list;
		} else if (args.length == 2) {
			List<String> list = new ArrayList<String>();
			list.add("biology");
			list.add("engineering");
			list.add("physics");
			return list;
		}

		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}

}
