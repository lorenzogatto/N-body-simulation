package pap.exam.nbody;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.util.Pair;

/**
 * A set of planets who can be moved forward in time with the advanceTime method
 * 
 * @author gatto
 *
 */
public class Galaxy {

	private List<Planet> planets = new ArrayList<>();
	private Random randomGenerator = new Random();
	private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);
	private double G = 6.673e-11;
    
	public Galaxy(int nPlanets) {
		for (int i = 0; i < nPlanets; i++) {
			planets.add(randomPlanet());
		}
	}

	public void advanceTime(float seconds) throws ExecutionException, InterruptedException {
		List<Pair<Float, Float>> accelerations = calculateAccelerations();
		// sposta corpi
		for (int i = 0; i < planets.size(); i++) {
			float deltaT = seconds;
			Pair<Float, Float> acceleration = null;
			while (acceleration == null) {
				acceleration = accelerations.get(i);
			}
			float ax = acceleration.getKey();
			float ay = acceleration.getValue();
			float initialXVelocity = planets.get(i).getSpeed().getKey();
			float initialYVelocity = planets.get(i).getSpeed().getValue();
			float finalX = planets.get(i).getPosition().getKey() + initialXVelocity * deltaT + ax * deltaT * deltaT / 2;
			float finalY = planets.get(i).getPosition().getValue() + initialYVelocity * deltaT
					+ ay * deltaT * deltaT / 2;
			float finalXVelocity = initialXVelocity + ax * deltaT;
			float finalYVelocity = initialYVelocity + ay * deltaT;
			planets.get(i).setPosition(new Pair<>(finalX, finalY));
			planets.get(i).setSpeed(new Pair<>(finalXVelocity, finalYVelocity));
		}
	}
	/**
	 * Calculate planets accelerations using executors
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private List<Pair<Float, Float>> calculateAccelerations() throws ExecutionException, InterruptedException {
		Future<Pair<Float, Float>> accelerations[] = (Future<Pair<Float, Float>>[]) new Future[planets.size()];
		for (int i = 0; i < planets.size(); i++) {
			final int index = i;
			accelerations[i] = executor.submit(() -> {
				Pair<Float, Float> myPosition = planets.get(index).getPosition();
				float Ax = 0, Ay = 0;
				for (int p = 0; p < planets.size(); p++) {
					if (p == index)
						continue;
					float otherMass = planets.get(p).getMass();
					Pair<Float, Float> otherPosition = planets.get(p).getPosition();
					double distanceSquared = Math.pow(myPosition.getKey() - otherPosition.getKey(), 2)
							+ Math.pow(myPosition.getValue() - otherPosition.getValue(), 2);
					distanceSquared = Math.max(distanceSquared, Math.pow(1e10, 2));
					double acceleration = otherMass * G / distanceSquared;
					double angle = Math.atan2(otherPosition.getValue() - myPosition.getValue(),
							otherPosition.getKey() - myPosition.getKey());
					Ax += acceleration * Math.cos(angle);
					Ay += acceleration * Math.sin(angle);
				}
				return new Pair<>(Ax, Ay);
			});
		}
		ArrayList<Pair<Float, Float>> ret = new ArrayList<>();
		for (int i = 0; i < planets.size(); i++)
			try {
				ret.add(accelerations[i].get());
			} catch (InterruptedException e) {
				// System.out.println("Interrupted");
				for (Future<Pair<Float, Float>> f : accelerations)
					f.cancel(true);
				throw e;
			}
		return ret;
	}

	public synchronized List<Planet> getGalaxyState() {
		return planets;
	}

	/**
	 * Generate a random planets
	 * @return
	 */
	private Planet randomPlanet() {
		Planet planet = new Planet();
		float mass = (float) (randomGenerator.nextFloat() * 1e21 + 1e15);
		float x = (float) (randomGenerator.nextGaussian() * 1e10 + 0.35 * 1e12);
		float y = (float) (randomGenerator.nextGaussian() * 1e10 + 0.35 * 1e12);
		float vx = (float) (randomGenerator.nextGaussian() * 1e1);
		float vy = (float) (randomGenerator.nextGaussian() * 1e1);
		planet.setMass(mass);
		planet.setPosition(new Pair<>(x, y));
		planet.setSpeed(new Pair<>(vx, vy));
		return planet;
	}
}
