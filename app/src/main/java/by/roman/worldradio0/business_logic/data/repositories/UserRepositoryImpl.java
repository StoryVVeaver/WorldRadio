package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.User;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;

public class UserRepositoryImpl implements UserRepository {
    private final UserDao userDao;
    public UserRepositoryImpl(UserDao userDao){
        this.userDao = userDao;
    }
    @Override
    public User getUserData(){
        try {
            return userDao.getUserData(getUserInSystem());
        } catch (Exception e) {
            Log.e("UserRepositoryImp","Failed get user data");
            return null;
        }
    }
    @Override
    public boolean isTableEmpty(){
        try {
            return userDao.isTableEmpty();
        } catch (Exception e) {
            Log.e("UserRepositoryImpl","Error scanning table: " + e.getMessage());
            return true;
        }
    }

    @Override
    public boolean setUserAvatar(String avatar) {
        try {
            return userDao.setUserAvatar(getUserInSystem(), avatar);
        } catch (Exception e) {
            Log.e("UserRepositoryImpl", " " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean entrance(User user){
        return false; //TODO
    }
    @Override
    public void setUserInSystem(int id){
        try {
            userDao.setUserInSystem(id);
        } catch (Exception e) {
            Log.e("UserRepositoryImp","Failed set user id in system");
        }
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
            userDao.removeUser(getUserInSystem());
        } catch (Exception e) {
            Log.e("UserRepositoryImp","Failed remove user");
        }
    }
    @Override
    public boolean exit(){
        try {
            return userDao.exit();
        } catch (Exception e) {
            Log.e("UserRepositoryImp","Failed exit");
            return false;
        }
    }
}
