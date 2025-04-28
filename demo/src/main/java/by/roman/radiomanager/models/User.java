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
    @Column(unique = true)
    private String login;
    private String password;
    private String station;
    private int inSystem;

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
}
