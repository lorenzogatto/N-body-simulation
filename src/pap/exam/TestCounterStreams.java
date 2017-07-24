package pap.exam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by gatto on 4/26/2017.
 */
public class TestCounterStreams {

	public static void main(String args[]) {
		List<Counter> counters = new ArrayList<>();
		counters.add(new Counter(10));
		counters.add(new Counter(20));
		counters.add(new Counter(0));
		counters.add(new Counter(-20));
		counters.add(new Counter(1));
		counters.add(new Counter(7));
		counters.add(new Counter(0));
		counters.add(new Counter(50));
		counters.add(new Counter(40));
		counters.add(new Counter(80));

		// test incrementByOneIfZero
		CounterStreams.incrementByOneIfZero(counters).forEach(c -> System.out.println(c.getValue()));
		testGetMax(counters, 10, 49);
		testGetMax(counters, 100, 200);
		// testare funzione che conta i contatori con valore sopra la media
		System.out.println("Counters average is: " + counters.stream().mapToInt(c -> c.getValue()).average().orElse(0));
		System.out.println("Counters above average: " + CounterStreams.aboveAverage(counters));

		System.out.println(CounterStreams.mapByValue(counters).toString());
	}

	private static void testGetMax(List<Counter> counters, int minRange, int maxRange) {
		Optional<Counter> maximum = CounterStreams.getMax(counters, minRange, maxRange);
		if (maximum.isPresent()) {
			System.out.println(
					"Maximum value in range [" + minRange + "," + maxRange + "] is: " + maximum.get().getValue());
		} else {
			System.out.println("No value found in range [" + minRange + "," + maxRange + "]");
		}
	}
}
