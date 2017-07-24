package pap.exam.nbody.actors;

public final class MessageForGalaxy {
	
	public enum Type {
		/**
		 * Expects an extra of type Float
		 */
		ADVANCE_TIME,
		/**
		 * Stop advancing time
		 */
		STOP_COMPUTATION,
		/**
		 * New galaxy state (self-sent message)
		 */
		NEW_POSITIONS_COMPUTED;
	}
	private final Type type;
	private final Object extra;
	
	public MessageForGalaxy(Type type, Object extra) {
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
