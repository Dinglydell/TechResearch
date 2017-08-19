package dinglydell.techresearch.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import dinglydell.techresearch.techtree.TechNode;

public class CraftingReplacementHandler implements IReplacementHandler {

	@Override
	public void replace(Collection<TechNode> nodes) {

		for (TechNode node : nodes) {
			List<IRecipe> additions = new ArrayList<IRecipe>();
			for (ItemStack it : node.getItemsUnlocked()) {

				Iterator<IRecipe> iterator = CraftingManager.getInstance()
						.getRecipeList().iterator();

				while (iterator.hasNext()) {
					IRecipe recipe = iterator.next();
					if (recipe == null)
						continue;
					ItemStack output = recipe.getRecipeOutput();

					if (output != null && output.getItem() == it.getItem()
							&& output.getItemDamage() == it.getItemDamage()) {
						iterator.remove();
						additions.add(recipe);
					}
				}
			}

			for (IRecipe r : additions) {
				GameRegistry.addRecipe(new RecipeResearchable(r, node));
			}
		}

	}
}
