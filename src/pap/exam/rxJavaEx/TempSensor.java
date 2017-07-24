package pap.exam.rxJavaEx;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Class implementing a simulated temperature sensor
 * (Assignment #08)
 * 
 * @author aricci
 *
 */
public class TempSensor {	
	private volatile double currentValue;
	private double min, max, spikeFreq;
	private Random gen;
	private BaseTimeValue time;
	private double zero;
	private double range;
	private double spikeVar;
	private UpdateTask updateTask;
	private ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
	
	/**
	 * Create a sensor producing values in a (min,max) range, with possible spikes
	 * 
	 * @param min range min
	 * @param max range max
	 * @param spikeFreq - probability to read a spike (0 = no spikes, 1 = always spikes)
	 */
	public TempSensor(double min, double max, double spikeFreq){
		gen = new Random(System.nanoTime());
		time = BaseTimeValue.getInstance();
		zero = (max + min)*0.5;
		range = (max - min)*0.5;
		this.spikeFreq = spikeFreq; 
		spikeVar = range*10;
		updateTask = new UpdateTask();
		exec.scheduleAtFixedRate(updateTask, 0, 100, java.util.concurrent.TimeUnit.MILLISECONDS);

		/* initialize currentValue */
		updateTask.run();
	}
	
	/**
	 * Reading the current sensor value 
	 * 
	 * @return sensor value
	 */
	public double getCurrentValue(){
		synchronized (updateTask){
			return currentValue;
		}
	}

	public void shutdown(){
		exec.shutdownNow();
	}
	
	class UpdateTask implements Runnable {
		public void run(){
			double newValue;

			double delta = (-0.5 + gen.nextDouble())*range*0.2;
			newValue = zero + Math.sin(time.getCurrentValue())*range*0.8 + delta;

			boolean newSpike = gen.nextDouble() <= spikeFreq;
			if (newSpike){
				newValue = currentValue + spikeVar;
			}

			synchronized (this){
				currentValue = newValue;
			}
		}
	}
}

class BaseTimeValue {
	static BaseTimeValue instance;
	static BaseTimeValue getInstance() {
		synchronized (BaseTimeValue.class){
			if (instance == null){
				instance = new BaseTimeValue();
			}
			return instance;
		}
	}
	
	private double time;
	private ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

	private BaseTimeValue(){
		time = 0;
		exec.scheduleAtFixedRate(() -> {
			synchronized (exec){
				time += 0.01;
			}
		}, 0, 100, java.util.concurrent.TimeUnit.MILLISECONDS);
	}
	
	public double getCurrentValue(){
		synchronized (exec){
			return time;
		}
	}
}

