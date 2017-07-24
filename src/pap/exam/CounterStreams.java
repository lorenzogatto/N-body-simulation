package pap.exam;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Created by gatto on 4/26/2017.
 */
public class CounterStreams {

	/**funzione che data una lista di contatori, incrementi tutti quelli con valore pari a zero
	 * 
	 * @param counters
	 * @return
	 */
	public static List<Counter> incrementByOneIfZero(List<Counter> counters) {
		return counters.stream().map(c -> {
			if (c.getValue() == 0)
				c.inc();
			return c;
		}).collect(toList());
	}

	/**
	 * funzione che data una lista di contatori e due valori interi min e max, con min < max,
	 * determini il contatore con il valore massimo fra quelli con valore compreso in [min,max] 
	 * @param counters
	 * @param min
	 * @param max
	 * @return
	 */
	public static Optional<Counter> getMax(List<Counter> counters, int min, int max) {
		return counters.stream()
				.filter(c -> c.getValue() >= min && c.getValue() <= max)
				.max((c1, c2) -> c1.getValue() - c2.getValue());
	}

	/**
	 * funzione che data una lista di contatori  conti il numero di quelli che hanno valore maggiore della media dei contatori
	 * @param counters
	 * @return
	 */
	public static long aboveAverage(List<Counter> counters) {
		OptionalDouble mean = counters.stream().mapToInt(c -> c.getValue()).average();
		if (!mean.isPresent()) {
			return 0;
		}
		return counters.stream().filter(c -> c.getValue() > mean.getAsDouble()).count();
	}

	/**
	 * funzione che data una lista di contatori costruisca una mappa in cui si raggruppino 
	 * tutti i contatori che hanno il medesimo valore, utilizzato come chiave
	 * @param counters
	 * @return
	 */
	public static Map<Integer, List<Counter>> mapByValue(List<Counter> counters) {
		return counters.stream().collect(groupingBy(e -> e.getValue()));
	}
}
