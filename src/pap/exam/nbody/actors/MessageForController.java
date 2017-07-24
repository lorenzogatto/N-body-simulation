package pap.exam.nbody.actors;

public final class MessageForController {
	
	public enum Type {
		/**
		 * Start button pressed
		 */
		START,
		/**
		 * Pause button pressed
		 */
		PAUSE,
		/**
		 * Galaxy state has changed. Take list of planets as extra
		 */
		GALAXY_STATE_UPDATED;
	}
	private final Type type;
	private final Object extra;
	
	public MessageForController(Type type, Object extra) {
		this.type = type;
		this.extra = extra;
	}

	public Type getType() {
		return type;
	}

	public Object getExtra() {
		return extra;
	}	
}
