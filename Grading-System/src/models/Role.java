package models;

public enum Role {
    STUDENT(1, "Student"),
    TEACHER(2, "Teacher"),
    ADMIN(3, "Admin");

    private final int id;
    private final String name;

    Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Role getById(int id) {
        for (Role role : values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
