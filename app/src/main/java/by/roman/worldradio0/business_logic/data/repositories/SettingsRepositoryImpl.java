package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import by.roman.worldradio0.business_logic.data.database.SettingsDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;

public class SettingsRepositoryImpl implements SettingsRepository {
   private final SettingsDao settingsDao;
   private final UserDao userDao;
   public SettingsRepositoryImpl(SettingsDao settingsDao, UserDao userDao){
      this.settingsDao = settingsDao;
      this.userDao = userDao;
   }
   @Override
   public Settings getSettings(){
      try {
         return settingsDao.getSetting(userDao.getIdUserInSystem());
      } catch (Exception e) {
         Log.e("FavoriteRepositoryImp","Failed get settings");
         return null;
      }
   }
   @Override
   public void setSettings(SettingsDTO settingsDTO){
      try {
         settingsDao.setSettings(settingsDTO);
      } catch (Exception e) {
         Log.e("FavoriteRepositoryImp","Failed set settings");
      }
   }
   @Override
   public void removeSettings(){
      try {
         settingsDao.removeSettings(userDao.getIdUserInSystem());
      } catch (Exception e) {
         Log.e("FavoriteRepositoryImp","Failed remove settings");
      }
   }
   @Override
   public void addSettings(SettingsDTO dto){
      try {
         settingsDao.addSettings(dto);
      } catch (Exception e) {
         Log.e("FavoriteRepositoryImp","Failed add settings");
      }
   }
}
