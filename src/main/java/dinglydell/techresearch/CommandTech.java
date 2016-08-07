package dinglydell.techresearch;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
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
					sender.addChatMessage(new ChatComponentText("Biology: "
							+ ptdep.biology));
					sender.addChatMessage(new ChatComponentText("Chemistry: "
							+ ptdep.chemistry));
					sender.addChatMessage(new ChatComponentText("Physics: "
							+ ptdep.physics));
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
							ptdep.biology += Double.parseDouble(args[2]);
							break;
						case "chemistry":
							ptdep.chemistry += Double.parseDouble(args[2]);
							break;
						case "physics":
							ptdep.physics += Double.parseDouble(args[2]);
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
					if (args.length < 3
							|| !(args[1].equals("biology")
									|| args[1].equals("chemistry") || args[1]
										.equals("physics"))) {
						sender.addChatMessage(new ChatComponentText(
								getCommandUsage(sender)));
						return;
					}

					try {
						// I don't know how I feel about this
						Field type = TechNode.class.getDeclaredField(args[1]);

						Field playerType = PlayerTechDataExtendedProps.class
								.getDeclaredField(args[1]);
						double playerValue = playerType.getDouble(ptdep);
						double spendValue = Double.parseDouble(args[2]);
						if (playerValue < spendValue) {
							sender.addChatMessage(new ChatComponentText(
									"You don't have enough " + args[1]
											+ " points."));
							return;
						}
						List<TechNode> nodes = new ArrayList<TechNode>(
								TechTree.nodes.values());
						nodes.removeIf(new Predicate<TechNode>() {
							@Override
							public boolean test(TechNode tn) {
								if (ptdep.hasCompleted(tn)) {
									return true;
								}
								for (String tid : tn.requiresAll) {
									if (!ptdep.hasCompleted(tid)) {
										return true;
									}
								}
								for (String tid : tn.requiresAny) {
									if (ptdep.hasCompleted(tid)) {
										return false;
									}
								}
								return false;
							}
						});
						float totalWeight = 0;
						for (TechNode tn : nodes) {
							totalWeight += type.getFloat(tn);
							// switch (args[1]) {
							// case "biology":
							// totalWeight += tn.biology;
							// break;
							// case "chemistry":
							// totalWeight += tn.chemistry;
							// break;
							// case "physics":
							// totalWeight += tn.physics;
							// break;
							// }
						}

						if (totalWeight > 0) {
							double rnd = Math.random() * totalWeight;

							int i;
							for (i = 0; rnd >= 0; i++) {
								TechNode tn = nodes.get(i);
								rnd -= type.getFloat(tn);

							}
							if (rnd == 0) {
								i = 1;
							}
							TechNode tn = nodes.get(i - 1);

							ptdep.addProgress(tn, spendValue);
							playerType.setDouble(ptdep, playerValue
									- spendValue);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}

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
			list.add("chemistry");
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
