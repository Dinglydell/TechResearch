package dinglydell.techresearch.recipe;

import java.lang.reflect.Field;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.ReflectionHelper;
import dinglydell.techresearch.NodeProgress;
import dinglydell.techresearch.PlayerTechDataExtendedProps;
import dinglydell.techresearch.TechNode;

public class RecipeResearchable implements IRecipe {
	private static final Field eventHandlerField = ReflectionHelper.findField(
			InventoryCrafting.class, "eventHandler");
	private static final Field containerPlayerPlayerField = ReflectionHelper
			.findField(ContainerPlayer.class, "thePlayer");
	private static final Field slotCraftingPlayerField = ReflectionHelper
			.findField(SlotCrafting.class, "thePlayer");

	protected IRecipe recipe;
	protected TechNode tech;

	protected EntityPlayer findPlayer(InventoryCrafting inv) {
		try {
			Container container = (Container) eventHandlerField.get(inv);
			if (container instanceof ContainerPlayer) {
				return (EntityPlayer) containerPlayerPlayerField.get(container);
			} else if (container instanceof ContainerWorkbench) {
				return (EntityPlayer) slotCraftingPlayerField.get(container
						.getSlot(0));
			} else {
				// don't know the player
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected boolean hasTech(InventoryCrafting craft) {
		EntityPlayer player = findPlayer(craft);
		PlayerTechDataExtendedProps ptdep = PlayerTechDataExtendedProps
				.get(player);
		if (!ptdep.getNodes().containsKey(tech)) {
			return false;
		}
		NodeProgress np = ptdep.getNodes().get(tech);
		return np.isComplete();
	}

	@Override
	public boolean matches(InventoryCrafting craft, World world) {
		return hasTech(craft) && recipe.matches(craft, world);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craft) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRecipeSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getRecipeOutput() {
		// TODO Auto-generated method stub
		return null;
	}

}
