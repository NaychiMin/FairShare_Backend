package com.example.fairsharebackend.validation;

import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegisterRequestDto> {

    @Override
    public boolean isValid(UserRegisterRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getPassword2() == null) return false;
        return dto.getPassword().equals(dto.getPassword2());
    }
}