package kasirin.data.model;

/// User roles that match the database enum values.
/// <p>Contains 3 roles: ADMIN, STAFF, OWNER</p>
public enum Role {
    ADMIN("admin"),
    STAFF("staff"),
    OWNER("owner");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role fromString(String value) {
        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
