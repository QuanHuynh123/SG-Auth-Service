package com.example.Makeup.enums;

public enum RoleType {
    USER("USER"),
    ADMIN("ADMIN"),
    STAFF_MAKEUP("STAFF_MAKEUP"),
    STAFF_NAIL("STAFF_NAIL");

    private final String value;

    RoleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
