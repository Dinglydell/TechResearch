package dinglydell.techresearch;

public class TechResearchSettings {

	static boolean defaultTree = true;
	public static boolean defaultExps = true;
	public static boolean defaultNotebookRecipe = true;

	public static void disableDefaultTree() {
		defaultTree = false;
	}

	public static void disableDefaultExperiments() {
		defaultExps = false;
	}

	public static void disableDefaultNotebookRecipe() {
		defaultNotebookRecipe = false;
	}

}
