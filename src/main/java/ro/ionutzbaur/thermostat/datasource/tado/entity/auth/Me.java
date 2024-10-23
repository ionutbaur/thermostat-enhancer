package ro.ionutzbaur.thermostat.datasource.tado.entity.auth;

import java.util.List;

public class Me {

    private String name;
    private String email;
    private String username;
    private List<Home> homes;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public List<Home> getHomes() {
        return homes;
    }
}
