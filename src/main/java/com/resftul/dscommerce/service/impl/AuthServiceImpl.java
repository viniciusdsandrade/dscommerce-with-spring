package com.resftul.dscommerce.service.impl;

import com.resftul.dscommerce.entity.User;
import com.resftul.dscommerce.exception.ForbiddenException;
import com.resftul.dscommerce.service.AuthService;
import org.springframework.stereotype.Service;

@Service("authService")
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void validateSelfOrAdmin(long userId) {
        User me = userService.authenticated();
        if (!me.hasRole("ROLE_ADMIN") && !me.getId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
