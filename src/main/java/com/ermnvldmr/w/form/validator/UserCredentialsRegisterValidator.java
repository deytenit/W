package com.ermnvldmr.w.form.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.ermnvldmr.w.form.UserCredentialsRegister;
import com.ermnvldmr.w.service.UserService;

@Component
public class UserCredentialsRegisterValidator implements Validator {
    private final UserService userService;

    public UserCredentialsRegisterValidator(UserService userService) {
        this.userService = userService;
    }

    public boolean supports(Class<?> clazz) {
        return UserCredentialsRegister.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            UserCredentialsRegister registerForm = (UserCredentialsRegister) target;
            if (userService.findByLogin(registerForm.getLogin()) != null) {
                errors.reject("user-exists", "User with such login already exists");
            }
        }
    }
}
