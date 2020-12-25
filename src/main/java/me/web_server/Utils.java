package me.web_server;

public final class Utils {
    private Utils() {
        super();
	}

	public interface IfNotNullCallable<T, U> {
		T call(U object);
	}

	public interface IfNullCallable<T> {
		T call();
	}

	public interface IfNotNullThrowingCallable<T, U, E extends Throwable> {
		T call(U object) throws E;
	}

	public interface IfNullThrowingCallable<T, E extends Throwable> {
		T call() throws E;
	}

	public interface IfNotNullIfCallable<T> {
		boolean call(T object);
	}

	public interface IfNullIfCallable {
		boolean call();
	}

	public interface IfNotNullIfThrowingCallable<T, E extends Throwable> {
		boolean call(T object) throws E;
	}

	public interface IfNullIfThrowingCallable<E extends Throwable> {
		boolean call() throws E;
	}

	public static <T> T safeCast(Class<T> typeClass, Object object) {
		if (nonNullParameters(typeClass, object)) {
			return typeClass.isInstance(object) ? typeClass.cast(object) : null;
		} else {
			return null;
		}
	}

	public static boolean nonNullParameters(Object... objects) {
		if (objects == null) {
			return false;
		}
		
		for (Object object : objects) {
			if (object == null) {
				return false;
			}
		}

		return true;
	}

	public static <T, U> T ifNotNullThen(
		U object,
		IfNotNullCallable<T, U> callable
	) {
		if (object == null) {
			return null;
		}
		
		return callable.call(object);
	}

	public static <T, U, E extends Throwable> T ifNotNullThenThrowing(
		U object,
		IfNotNullThrowingCallable<T, U, E> callable
	) throws E {
		if (object == null) {
			return null;
		}
		
		return callable.call(object);
	}

	public static <T, U> T ifNotNullThenElse(
		U object,
		IfNotNullCallable<T, U> notNullcallable,
		IfNullCallable<T> nullCallable
	) {
		return (object == null ? nullCallable.call() : notNullcallable.call(object));
	}

	public static <T, U, E extends Throwable> T ifNotNullThenElseThrowing(
		U object,
		IfNotNullThrowingCallable<T, U, E> notNullCallable,
		IfNullThrowingCallable<T, E> nullCallable
	) throws E {
		return (object == null ? nullCallable.call() : notNullCallable.call(object));
	}

	public static <T> boolean ifNotNullThenIf(
		T object,
		IfNotNullIfCallable<T> callable
	) {
		if (object == null) {
			return false;
		}

		return callable.call(object);
	}
	public static <T, E extends Throwable> boolean ifNotNullThenIfThrowing(
		T object,
		IfNotNullIfThrowingCallable<T, E> callable
	) throws E {
		if (object == null) {
			return false;
		}

		return callable.call(object);
	}

	public static <T> boolean ifNotNullThenIfElse(
		T object,
		IfNotNullIfCallable<T> notNullCallable,
		IfNullIfCallable nullCallable
	) {
		return (object == null ? nullCallable.call() : notNullCallable.call(object));
	}

	public static <T, E extends Throwable> boolean ifNotNullThenIfElseThrowing(
		T object,
		IfNotNullIfThrowingCallable<T, E> notNullCallable,
		IfNullIfThrowingCallable<E> nullCallable
	) throws E {
		return (object == null ? nullCallable.call() : notNullCallable.call(object));
	}
}