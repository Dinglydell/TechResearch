package dinglydell.techresearch.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for map to make creating them easier. With this a map can be
 * created in one line, eg:
 * 
 * new MapBuilder<ResearchType,Double>().put(ResearchType.science,
 * 20.0).put(ResearchType.engineering, 20.0).getMap()
 */
public class MapBuilder<TKey, TValue> {
	Map<TKey, TValue> map;

	public MapBuilder() {
		map = new HashMap<TKey, TValue>();
	}

	public MapBuilder<TKey, TValue> put(TKey key, TValue value) {
		map.put(key, value);
		return this;
	}

	public Map<TKey, TValue> getMap() {
		return map;
	}

}
