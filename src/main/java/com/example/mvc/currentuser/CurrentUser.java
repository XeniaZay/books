package com.example.mvc.currentuser;

public record CurrentUser(String id, String role) {
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}


