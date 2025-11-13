package by.roman.worldradio0.business_logic;

public class UiState<T> {


    public enum Status {
        SUCCESS, ERROR, LOADING
    }

    public final Status status;
    public final T data;
    public final String message;

    private UiState(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> UiState<T> success(T data) {
        return new UiState<>(Status.SUCCESS, data, null);
    }

    public static <T> UiState<T> error(String msg) {
        return new UiState<>(Status.ERROR, null, msg);
    }

    public static <T> UiState<T> loading() {
        return new UiState<>(Status.LOADING, null, null);
    }
    public static <T> UiState<T> loading(T data) {
        return new UiState<>(Status.LOADING, data, null);
    }
}
