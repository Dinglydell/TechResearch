package dinglydell.techresearch.util;

import java.util.Map;

public abstract class MapUtils {

	public static <TKey, TVal> TVal getOrDefault(Map<TKey, TVal> map,
			TKey key,
			TVal dflt) {
		return map.containsKey(key) ? map.get(key) : dflt;
	}
}
