package dinglydell.techresearch;

import java.util.ArrayList;
import java.util.List;

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
		return "tech <view|add> [type] [quantity]";
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
			PlayerTechDataExtendedProps ptdep = PlayerTechDataExtendedProps
					.get(player);
			switch (args[0]) {
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

		if (args.length == 0) {
			List<String> list = new ArrayList<String>();
			list.add("view");
			list.add("add");
			return list;
		} else if (args.length == 1) {
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
