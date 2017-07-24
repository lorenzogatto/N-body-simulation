package pap.exam.rxJavaEx;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class TempStream {
	/**
	 * Creates an observable from a sensor
	 * @param sensor
	 * @param interval
	 * @return
	 */
	public static Observable<Double> getObservable(TempSensor sensor, int interval) {
		return Observable.interval(interval, TimeUnit.MILLISECONDS)
				.map((v)->sensor.getCurrentValue())
				.share();
	}
}
