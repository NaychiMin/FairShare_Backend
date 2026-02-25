package com.example.fairsharebackend.entity.dto.request;

import com.example.fairsharebackend.validation.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@PasswordMatches
public class UserUpdatePasswordRequestDto implements UserPasswordConfirmable {
    @NotBlank(message = "Old password is required")
    String oldPassword;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
    String password;

    @NotBlank(message = "Password confirmation is required")
    String password2;

    public UserUpdatePasswordRequestDto() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}
