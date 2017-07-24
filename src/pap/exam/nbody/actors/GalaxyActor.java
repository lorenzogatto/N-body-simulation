package pap.exam.nbody.actors;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.rits.cloning.Cloner; //https://github.com/kostaskougios/cloning/
import akka.actor.UntypedActor;
import pap.exam.nbody.Galaxy;
import pap.exam.nbody.Planet;
import pap.exam.nbody.actors.MessageForGalaxy.Type;

public class GalaxyActor extends UntypedActor {

	private Galaxy galaxy;
	private float step;
	private Future<?> galaxyStateUpdate = null;
	private ExecutorService executor = Executors.newFixedThreadPool(1);
	private Boolean inMovement = false;
	private final int N_PLANETS = 200;
    private int iteration = 0;
    
	public void preStart() {
		galaxy = new Galaxy(N_PLANETS);
	}

	@Override
	public void onReceive(Object arg0) throws Exception {
		MessageForGalaxy message = (MessageForGalaxy) arg0;
		Type type = message.getType();
		if (type.equals(Type.ADVANCE_TIME)) {
			step = (Float) message.getExtra();
			inMovement = true;
			updateParentAboutPositions();
			updateGalaxy();
		} else if (type.equals(Type.STOP_COMPUTATION)) {
			galaxyStateUpdate.cancel(true);
			inMovement = false;
		} else if(type.equals(Type.NEW_POSITIONS_COMPUTED)) {
			updateParentAboutPositions();
			if(inMovement) {
				updateGalaxy();
			}
		}
	}

	private void updateGalaxy() {
		//System.out.println("Update!");
		galaxyStateUpdate = executor.submit(() -> {
			try {
				galaxy.advanceTime(step);
				MessageForGalaxy message = new MessageForGalaxy(Type.NEW_POSITIONS_COMPUTED, null);
				self().tell(message, self());
				iteration++;
				synchronized(System.out) {
					System.out.println("Iteration #:" + iteration);
				}
			} catch (ExecutionException | InterruptedException e) {
				//System.out.println(e);
				throw new RuntimeException(e);
			}
		});
	}
	
	private void updateParentAboutPositions() {
		List<Planet> planets = galaxy.getGalaxyState();
		Cloner cloner=new Cloner();
		List<Planet> clonedPlanetsList=cloner.deepClone(planets);
		MessageForController message = new MessageForController(MessageForController.Type.GALAXY_STATE_UPDATED,
				clonedPlanetsList);
		getContext().parent().tell(message, self());
	}

}
