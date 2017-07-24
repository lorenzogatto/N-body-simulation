package pap.exam.nbody;

import javafx.util.Pair;
/**
 * Thread-safe implementation of a planet entity
 */
public class Planet {
	private volatile Pair<Float, Float> position;
	private volatile Pair<Float, Float> speed;
	private volatile float mass;
	
	public Pair<Float, Float> getPosition() {
		return position;
	}
	public void setPosition(Pair<Float, Float> position) {
		this.position = position;
	}
	public Pair<Float, Float> getSpeed() {
		return speed;
	}
	public void setSpeed(Pair<Float, Float> speed) {
		this.speed = speed;
	}
	public float getMass() {
		return mass;
	}
	public void setMass(float mass) {
		this.mass = mass;
	}
	
}
