package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.User;

public class UserRepositoryImpl implements UserRepository {
    private final UserDao userDao;
    public UserRepositoryImpl(UserDao userDao){
        this.userDao = userDao;
    }
    @Override
    public User getUserData(){
        try {
            return userDao.getUserData(userDao.getIdUserInSystem());
        } catch (Exception e) {
            Log.e("UserRepositoryImp","Failed get user data");
            return null;
        }
    }
    @Override
    public boolean entrance(User user){
        return false; //TODO
    }
    @Override
    public int getUserInSystem(){
        try {
            return userDao.getIdUserInSystem();
        } catch (Exception e) {
            Log.e("UserRepositoryImp","Failed get user id in system");
            return -1;
        }
    }
    @Override
    public String getPlayingUUID(){
        try {
            return userDao.getColumnPlayingUUID(userDao.getIdUserInSystem());
        } catch (Exception e) {
            Log.e("UserRepositoryImp","Failed get playing UUID");
            return null;
        }
    }
    @Override
    public void setPlayingUUID(String UUID){
        try {
            userDao.setColumnPlayingUUID(userDao.getIdUserInSystem(),UUID);
        } catch (Exception e){
            Log.e("UserRepositoryImp","Failed set playing UUID");
        }
    }
    @Override
    public void useradd(UserDTO dto){
        try {
            userDao.addUser(dto);
        } catch (Exception e){
            Log.e("UserRepositoryImp","Failed add user");
        }
    }
    @Override
    public  void removeUser(){
        try {
            userDao.removeUser(userDao.getIdUserInSystem());
        } catch (Exception e) {
            Log.e("UserRepositoryImp","Failed remove user");
        }
    }
    @Override
    public void exit(){
        try {
            userDao.exit();
        } catch (Exception e) {
            Log.e("UserRepositoryImp","Failed exit");
        }
    }
}
