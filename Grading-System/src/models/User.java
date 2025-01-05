package models;

public class User {

    private String firstName;
    private String lastName;
    private String id;
    private String username;
    private String password;
    private Role role;

    public User(String firstName, String lastName, String id, String username, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getId() {
        return id;
    }

    public String getUsername(){
        return "@" + username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getRoleId() {
        return role.getId();
    }
    
    public void setFirstName(String newName) {
        this.firstName = newName;
    }

    public void setLastName(String newName) {
        this.lastName = newName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(String newId) {
        this.id = newId;
    }
}
