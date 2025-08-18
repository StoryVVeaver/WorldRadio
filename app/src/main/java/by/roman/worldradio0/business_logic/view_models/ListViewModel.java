package by.roman.worldradio0.business_logic.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListViewModel extends ViewModel {
    private final MutableLiveData<List<String>> items = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private int currentPage = 0;
    private final int pageSize = 20;
    private boolean allLoaded = false;

    public LiveData<List<String>> getItems() {
        return items;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadNextPage() {
        if (Boolean.TRUE.equals(isLoading.getValue()) || allLoaded) return;

        isLoading.setValue(true);

        new Thread(() -> {
            List<String> newData = loadDataPage(currentPage, pageSize);

            List<String> currentList = new ArrayList<>(Objects.requireNonNull(items.getValue()));
            currentList.addAll(newData);

            if (newData.size() < pageSize) allLoaded = true;

            currentPage++;

            new Handler(Looper.getMainLooper()).post(() -> {
                items.setValue(currentList);
                isLoading.setValue(false);
            });
        }).start();
    }

    private List<String> loadDataPage(int page, int size) {
        List<String> result = new ArrayList<>();
        int totalItems = 370; // всего элементов
        int start = page * size;
        for (int i = 0; i < size; i++) {
            int index = start + i;
            if (index >= totalItems) break;
            result.add("Item " + (index + 1));
        }
        return result;
    }
}
