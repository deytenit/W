package com.ermnvldmr.w.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserCredentialsEditForm {
    @NotBlank(message = "Name cannot not be blank")
    @Size(min = 1, max = 64, message = "Name must be of size between 1 and 64 characters")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
