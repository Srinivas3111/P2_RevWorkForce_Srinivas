package com.rev.app.service;

import com.rev.app.dto.EmployeeDTO;

public interface AuthService
{
    EmployeeDTO authenticate(String identifier, String password);

    boolean isInactiveAccount(String identifier, String password);
}
