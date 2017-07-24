package pap.exam.rxJavaEx;

import rx.Observable;
import rx.Subscription;

public class ExamRxJava {

	private static int maxVariation = 50;
	private static MonitorWindow view = new MonitorWindow();
	private static Observable<Double> minimumAverage;
	private static Observable<Double> maximumAverage;
	private static Observable<Double> latestAverage;
	private static Observable<Double> tempStream1NoOutliers, tempStream2NoOutliers, tempStream3NoOutliers; // streams
																											// without
																											// outliers
	private static Subscription sub1, sub2, sub3; // subscriptions to print to
													// standard output
	private static Timer timer;

	public synchronized static void main(String[] args) {
		createObservableVariables();
	}

	private static void createObservableVariables() {
		// make sensors observable
		TempSensor sensor1 = new TempSensor(0, 100, 0.1);
		TempSensor sensor2 = new TempSensor(0, 100, 0.1);
		TempSensor sensor3 = new TempSensor(0, 100, 0.1);
		Observable<Double> tempStream1, tempStream2, tempStream3;
		tempStream1 = TempStream.getObservable(sensor1, 1000);
		tempStream2 = TempStream.getObservable(sensor2, 1000);
		tempStream3 = TempStream.getObservable(sensor3, 1000);
		// tempStream1.subscribe(v->System.out.println("Sensor 1 " + v));
		tempStream1NoOutliers = tempStream1.scan((a, b) -> {
			if (a == null || Math.abs(a - b) < maxVariation)
				return b;
			else
				return null;
		}).filter(a -> a != null);
		tempStream2NoOutliers = tempStream2.scan((a, b) -> {
			if (a == null || Math.abs(a - b) < maxVariation)
				return b;
			else
				return null;
		}).filter(a -> a != null);
		tempStream3NoOutliers = tempStream3.scan((a, b) -> {
			if (a == null || Math.abs(a - b) < maxVariation)
				return b;
			else
				return null;
		}).filter(a -> a != null);
		latestAverage = Observable.combineLatest(tempStream1NoOutliers, tempStream2NoOutliers, tempStream3NoOutliers,
				(a, b, c) -> (a + b + c) / 3);
		minimumAverage = latestAverage.scan((a, b) -> Math.min(a, b));
		maximumAverage = latestAverage.scan((a, b) -> Math.max(a, b));
		timer = new Timer(view, latestAverage);
	}

	public synchronized static void startListening() {
		System.out.println("Connecting");
		sub1 = tempStream1NoOutliers.subscribe(v -> {
			synchronized (System.out) {
				System.out.println("Cleaned sensor 1 " + v);
			}
		});
		sub2 = tempStream2NoOutliers.subscribe(v -> {
			synchronized (System.out) {
				System.out.println("Cleaned sensor 2 " + v);
			}
		});
		sub3 = tempStream3NoOutliers.subscribe(v -> {
			synchronized (System.out) {
				System.out.println("Cleaned sensor 3 " + v);
			}
		});
		view.observeAverage(latestAverage);
		view.observeMinimum(minimumAverage);
		view.observeMaximum(maximumAverage);
		timer.startListening();
	}

	public synchronized static void stopListening() {
		if (sub1 != null) {
			sub1.unsubscribe();
			sub2.unsubscribe();
			sub3.unsubscribe();
		}
		view.stopOservingAverage();
		view.stopOservingMaximum();
		view.stopOservingMinimum();
		timer.stopListening();
	}
}
