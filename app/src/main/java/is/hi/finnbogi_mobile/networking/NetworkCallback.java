package is.hi.finnbogi_mobile.networking;

public interface NetworkCallback<T> {
    void onSuccess(T result);

    void onFailure(String errorString);
}
