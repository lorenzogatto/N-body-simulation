package pap.exam.nbody;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class NBodyExecutors implements Observer {

	private Galaxy galaxy = null;
	private int NPlanets = 2000;
	private ViewPlanets view = new ViewPlanets();
	private LinkedBlockingQueue<Events> events = new LinkedBlockingQueue<>();
	private State state = State.WAITING_USER;
	private Future<?> galaxyStateUpdate = null;
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	private int nIterations = 0;
	private final int SECONDS_ADVANCE_PER_FRAME = 100000000;

	private enum State {
		WAITING_USER, RUNNING, PAUSED
	}

	public enum Events {
		START_BUTTON, PAUSE_BUTTON, FINISHED_FRAME_RENDERING;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		new NBodyExecutors();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		events.add((Events) arg1);
	}

	private NBodyExecutors() throws InterruptedException, ExecutionException {
		view.addObserver(this);
		mainCycle();
	}

	/**
	 * Read the event queue iterativelly
	 * @throws InterruptedException
	 */
	private void mainCycle() throws InterruptedException {
		while (true) {
			Events event = events.take();
			if (event.equals(Events.START_BUTTON)) {
				if (state.equals(State.RUNNING)) {
					galaxyStateUpdate.cancel(true);
				}
				state = State.RUNNING;
				galaxy = new Galaxy(NPlanets);
				view.setPlanets(galaxy.getGalaxyState());
				view.repaint();
				updateGalaxy();
			} else if (event.equals(Events.PAUSE_BUTTON)) {
				if (state.equals(State.RUNNING)) {
					state = State.PAUSED;
					synchronized(System.out) {
						System.out.println("Cancelling galaxy update!");
					}
					galaxyStateUpdate.cancel(true);
				} else if (state.equals(State.PAUSED)) {
					state = State.RUNNING;
					updateGalaxy();
				}
			} else if (event.equals(Events.FINISHED_FRAME_RENDERING)) {
				if (state.equals(State.RUNNING)) {
					updateGalaxy();
				}
			}
			System.out.println("Event: " + event);
		}
	}

	private void updateGalaxy() {
		galaxyStateUpdate = executor.submit(() -> {
			try {
				galaxy.advanceTime(SECONDS_ADVANCE_PER_FRAME);
				view.repaint();
			} catch (ExecutionException | InterruptedException e) {
				throw new RuntimeException(e);
			}
			nIterations++;
			synchronized(System.out) {
				System.out.println("Iteration #:" + nIterations);
			}
			update(null, Events.FINISHED_FRAME_RENDERING);
		});
	}
}
