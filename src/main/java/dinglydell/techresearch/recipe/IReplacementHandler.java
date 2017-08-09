package dinglydell.techresearch.recipe;

import java.util.Collection;

import dinglydell.techresearch.techtree.TechNode;

public interface IReplacementHandler {
	public void replace(Collection<TechNode> nodes);
}
