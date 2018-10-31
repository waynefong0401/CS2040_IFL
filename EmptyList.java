package cs2030.mystream;

class EmptyList<T> extends IFL<T> {
	EmptyList() {
	}

	@Override
	boolean isEmpty() {
		return true;
	}
}
