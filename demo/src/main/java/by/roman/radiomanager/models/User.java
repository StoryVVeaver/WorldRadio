package by.roman.radiomanager.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, columnDefinition = "TEXT")
    private String login;
    @Column(columnDefinition = "TEXT")
    private String password;
    @Column(columnDefinition = "TEXT")
    private String station;
    @Column(columnDefinition = "TEXT")
    private String avatar;
    private int inSystem = 0;

    public User() {}

    public User(String login, String password, String station, int inSystem) {
        this.login = login;
        this.password = password;
        this.station = station;
        this.inSystem = inSystem;

    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getStation() {
        return station;
    }

    public int getInSystem() {
        return inSystem;
    }

    public String getAvatar(){
        return avatar;
    }
    public void setAvatar(String avatar){
        this.avatar = avatar;
    }
}
