package com.ermnvldmr.w.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserCredentialsEnter {
    @NotBlank(message = "Login cannot not be blank")
    @Size(min = 2, max = 24, message = "Login must be of size between 2 and 24 characters")
    @Pattern(regexp = "[a-zA-Z]{2,24}", message = "Login must only contain latin letters")
    private String login;

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 8, max = 60, message = "Password must be of size between 8 and 60 characters")
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
