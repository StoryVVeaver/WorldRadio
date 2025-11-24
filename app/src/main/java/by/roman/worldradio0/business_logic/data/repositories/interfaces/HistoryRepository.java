package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.HistoryDTO;
import by.roman.worldradio0.business_logic.data.models.History;

public interface HistoryRepository {
    void addToHistory(History history);
    void removeFromHistory(History history);
    void removeFromHistoryById(int userId);
    List<History> getHistoryList(int page, int page_size);
    History getLastHistory();
}
