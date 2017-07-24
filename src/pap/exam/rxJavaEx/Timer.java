package pap.exam.rxJavaEx;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

public class Timer {
	public static final double DEFAULT_THREASHOLD = 50;
	public static final long DEFAULT_TIMEOUT_MS = 3000;
	private double threashold;
	private long timeout;
	private MonitorWindow view;
	private Observable<Double> sensor;
	private Subscription sensorSub;
	private Observable<?> timer;
	private Subscription timerSub;

	public Timer(MonitorWindow view, Observable<Double> sensor) {
		this.view = view;
		this.sensor = sensor;
		this.threashold = DEFAULT_THREASHOLD;
		this.timeout = DEFAULT_TIMEOUT_MS;
		view.setTimer(this);
	}

	public void changedThreashold(double t) {
		System.out.println("new threashold");
		if (t > threashold && timerSub != null)
			resetTimer();
		this.threashold = t;
	}

	public void changedTimeout(long t) {
		System.out.println("new timeout");
		this.timeout = t;
		if (timerSub != null) {
			resetTimer();
		}
	}

	public void stopListening() {
		if (sensorSub != null)
			sensorSub.unsubscribe();
		if (timerSub != null)
			timerSub.unsubscribe();
	}

	public void startListening() {
		System.out.println("Start listening");
		sensor.subscribe(v -> {
			if (v > threashold) {
				if (timer == null) {
					resetTimer();
				}
			} else {
				if (timer != null) {
					timer = null;
					timerSub.unsubscribe();
					view.displayWarning(false);
				}
			}
		});
	}

	private void resetTimer() {
		if (timerSub != null)
			timerSub.unsubscribe();
		System.out.println("Start timer, timeout in (ms) " + timeout);
		timer = Observable.timer(timeout, TimeUnit.MILLISECONDS);
		timerSub = timer.subscribe(e -> view.displayWarning(true));
	}
}
