package com.fdrcyln.mapper;

import com.fdrcyln.dto.request.RegisterRequest;
import com.fdrcyln.dto.response.UserAccountResponse;
import com.fdrcyln.entities.UserAccount;
import com.fdrcyln.enums.Role;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserAccountMapper {

    public UserAccount toEntity(RegisterRequest request) {
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail(request.getEmail());
        userAccount.setRole(Role.USER);
        userAccount.setActive(true);
        userAccount.setCreatedDate(LocalDateTime.now());
        return userAccount;
    }

    public UserAccountResponse toResponse(UserAccount userAccount) {
        UserAccountResponse response = new UserAccountResponse();
        BeanUtils.copyProperties(userAccount, response);
        return response;
    }
}
