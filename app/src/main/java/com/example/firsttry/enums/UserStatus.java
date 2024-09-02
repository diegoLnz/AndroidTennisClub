package com.example.firsttry.enums;

public enum UserStatus
{
    ACTIVE("Attivo"),
    SUSPENDED("Sospeso"),
    BANDITED("Bandito");

    private final String description;

    UserStatus(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
}
