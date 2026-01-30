package by.roman.worldradio0.business_logic.network.radio.callbacks;

public interface RadioCallback<T> {
    void onSuccess(T t);
    void onFailure(Throwable t);
    void onLoading();
}
