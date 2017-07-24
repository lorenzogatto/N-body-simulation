package pap.exam.nbody.actors;

import akka.actor.ActorSystem;
import akka.actor.Props;


/*
 * Soluzione con attori.
 * Ogni attore può ricevere un insieme predeterminato di messaggi definito dalle classi
 * MessageFor[Tipo attore]
 *
 */
public class Main {

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("nbody");
		system.actorOf(Props.create(ControllerActor.class));
	}

}
