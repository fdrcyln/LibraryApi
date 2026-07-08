package com.fdrcyln.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMemberRequest {

    @NotBlank(message = "Adı boş olamaz")
    private String firstName;

    @NotBlank(message = "Soyadı boş olamaz")
    private String lastName;

    @NotBlank(message = "E-posta boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi giriniz")
    private String email;

    private String phone;

    private String address;

    private Boolean active;
}