package dinglydell.techresearch.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import dinglydell.techresearch.TechNode;
import dinglydell.techresearch.TechTree;

// todo: superclass for this
public class PacketBuyTech implements IMessage {
	TechNode node;

	public PacketBuyTech() {
	}

	public PacketBuyTech(TechNode node) {
		this.node = node;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		String id = ByteBufUtils.readUTF8String(buf);
		if (TechTree.nodes.containsKey(id)) {
			node = TechTree.nodes.get(id);
		} else {
			node = null;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, node.id);
	}
}
