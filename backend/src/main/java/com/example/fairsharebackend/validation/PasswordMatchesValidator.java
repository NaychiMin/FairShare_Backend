package com.example.fairsharebackend.validation;

import com.example.fairsharebackend.entity.dto.request.UserPasswordConfirmable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserPasswordConfirmable> {

    @Override
    public boolean isValid(UserPasswordConfirmable dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getPassword2() == null) return false;
        return dto.getPassword().equals(dto.getPassword2());
    }
}