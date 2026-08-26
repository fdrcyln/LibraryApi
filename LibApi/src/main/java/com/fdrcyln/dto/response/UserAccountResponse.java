package com.fdrcyln.dto.response;

import com.fdrcyln.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountResponse {

    private Long id;
    private String email;
    private Role role;
    private Boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
