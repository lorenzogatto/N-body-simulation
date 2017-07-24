package pap.exam.nbody.actors;

public final class MessageForUI {

	public enum Type {
		/**
		 * Need to render new content. Need list of planets as extra
		 */
		RENDER;
	}

	private final Type type;
	private final Object extra;

	public MessageForUI(Type type, Object extra) {
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
