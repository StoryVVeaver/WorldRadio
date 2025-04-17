package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import by.roman.worldradio0.business_logic.data.database.UserDao;
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
