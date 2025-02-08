package com.example.projectcampusride.controllers;

import com.example.projectcampusride.models.UserRole;
import com.example.projectcampusride.repositories.UserRepository;

public class RoleSelectionController {
    private final UserRepository userRepository;

    public RoleSelectionController() {
        this.userRepository = UserRepository.getInstance();
    }

    public void setUserRole(String userId, UserRole role, RoleSelectionCallback callback) {
        userRepository.updateUserRole(userId,role).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onRoleSet(true);
            } else {
                callback.onRoleSet(false);
            }
        });
    }

    public interface RoleSelectionCallback {
        void onRoleSet(boolean success);
    }
}
