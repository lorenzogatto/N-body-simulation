package pap.exam.nbody.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import pap.exam.nbody.actors.MessageForController.Type;

/**
 * Controller of the application
 * 
 * @author gatto
 *
 */
public class ControllerActor extends UntypedActor {

	private ActorRef galaxyRef;
	private ActorRef uiRef;
	private State state;
	private Float timePerFrame = (float) 100000000;

	private enum State {
		WAITING_USER, RUNNING, PAUSED
	}

	public void preStart() {
		uiRef = getContext().actorOf(Props.create(UIActor.class));
		state = State.WAITING_USER;
	}

	@Override
	public void onReceive(Object arg0) throws Exception {
		MessageForController message = (MessageForController) arg0;
		Type type = message.getType();
		//System.out.println(type);
		if (type.equals(Type.START)) {
			if (state.equals(State.RUNNING)) {
				galaxyRef.tell(new MessageForGalaxy(MessageForGalaxy.Type.STOP_COMPUTATION, null), self());
			}
			galaxyRef = getContext().actorOf(Props.create(GalaxyActor.class));
			state = State.RUNNING;
			galaxyRef.tell(new MessageForGalaxy(MessageForGalaxy.Type.ADVANCE_TIME, timePerFrame), self());
		} else if (type.equals(Type.PAUSE)) {
			if (state.equals(State.RUNNING)) {
				state = State.PAUSED;
				System.out.println("Cancelling galaxy update!");
				galaxyRef.tell(new MessageForGalaxy(MessageForGalaxy.Type.STOP_COMPUTATION, null), self());
			} else if (state.equals(State.PAUSED)) {
				state = State.RUNNING;
				galaxyRef.tell(new MessageForGalaxy(MessageForGalaxy.Type.ADVANCE_TIME, timePerFrame), self());
			}
		} else if (type.equals(Type.GALAXY_STATE_UPDATED)) {
			Object extra = message.getExtra();
			uiRef.tell(new MessageForUI(MessageForUI.Type.RENDER, extra), self());
		}
	}
}
