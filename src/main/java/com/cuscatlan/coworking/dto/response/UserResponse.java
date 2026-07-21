package com.cuscatlan.coworking.dto.response;

import com.cuscatlan.coworking.domain.Role;
import com.cuscatlan.coworking.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role
) {
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}