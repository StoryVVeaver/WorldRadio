package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import by.roman.worldradio0.business_logic.data.database.FilterDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FilterRepository;

public class FilterRepositoryImpl implements FilterRepository {
    private final FilterDao filterDao;
    private final UserDao userDao;
    public FilterRepositoryImpl(FilterDao filterDao, UserDao userDao){
        this.filterDao = filterDao;
        this.userDao = userDao;
    }
    @Override
    public Filter getFilters(){
        try {
            return filterDao.getFilters(userDao.getIdUserInSystem());
        } catch (Exception e) {
            Log.e("FilterRepositoryImp","Failed get filters");
            return null;
        }
    }
    @Override
    public void setFilters(FilterDTO filterDTO){
        try {
            filterDao.setFilters(filterDTO);
        } catch (Exception e) {
            Log.e("FilterRepositoryImp","Failed set filters");
        }
    }
    @Override
    public void addFilters(FilterDTO filterDTO){
        try {
            filterDao.addFilter(filterDTO);
        } catch (Exception e) {
            Log.e("FilterRepositoryImp","Failed add filters");
        }
    }
    @Override
    public void removeFilters(){
        try {
            filterDao.removeFilters(userDao.getIdUserInSystem());
        } catch (Exception e) {
            Log.e("FilterRepositoryImp","Failed remove filters");
        }
    }
}
