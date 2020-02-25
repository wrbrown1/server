package server;

public class User {
    private String userName, password, privilege;
    private int ID;
    
    public User (String userName, String password, String privilege, int ID){
        this.userName = userName;
        this.password = password;
        this.privilege = privilege;
        this.ID = ID;
    }
}
