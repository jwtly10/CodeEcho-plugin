package jwtly10.codeecho.callback;

/**
 * This interface is used to provide a callback mechanism for asynchronous operations.
 * It handles potential outcomes of the operation, such as success, failure, or completion,
 * allowing for graceful handling of the operation's result or exception
 */
public interface AsyncCallback<T> {

    /**
     * Called when the operation is successful, and there is some result to be returned
     */
    default void onResult(T result) {
    }

    /**
     * Called when the operation fails with an exception
     */
    default void onError(Exception e) {
    }

    /**
     * Called when the operation is completed, regardless of its outcome
     */
    default void onComplete() {
    }
}
