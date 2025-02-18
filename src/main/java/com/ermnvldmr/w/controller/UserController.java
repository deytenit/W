package com.ermnvldmr.w.controller;

import com.ermnvldmr.w.domain.User;
import com.ermnvldmr.w.form.UserCredentialsEditForm;
import com.ermnvldmr.w.form.UserCredentialsEnter;
import com.ermnvldmr.w.form.UserCredentialsRegister;
import com.ermnvldmr.w.service.UserService;
import com.ermnvldmr.w.form.validator.UserCredentialsEnterValidator;
import com.ermnvldmr.w.form.validator.UserCredentialsRegisterValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ermnvldmr.w.utils.Utils.getValidationErrors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class UserController {
    private final UserService userService;
    private final UserCredentialsEnterValidator userCredentialsEnterValidator;
    private final UserCredentialsRegisterValidator userCredentialsRegisterValidator;

    public UserController(UserService userService,
                          UserCredentialsEnterValidator userCredentialsEnterValidator,
                          UserCredentialsRegisterValidator userCredentialsRegisterValidator) {
        this.userService = userService;
        this.userCredentialsEnterValidator = userCredentialsEnterValidator;
        this.userCredentialsRegisterValidator = userCredentialsRegisterValidator;
    }

    @InitBinder("userCredentialsEnter")
    public void initEnterBinder(WebDataBinder binder) {
        binder.addValidators(userCredentialsEnterValidator);
    }

    @InitBinder("userCredentialsRegister")
    public void initRegisterBinder(WebDataBinder binder) {
        binder.addValidators(userCredentialsRegisterValidator);
    }

    @Operation(summary = "Authenticate user and retrieve JWT",
            description = "Validates user credentials and returns a JWT if successful.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "JWT retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation errors"),
            @ApiResponse(responseCode = "401", description = "Wrong login or password")
    })
    @PostMapping("users/enter")
    public ResponseEntity<Object> findUserJwt(
            @Parameter(description = "User credentials for login", required = true)
            @RequestBody @Valid UserCredentialsEnter userCredentials,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(bindingResult));  // 400 Bad Request
        }

        User user = userService.findByLoginAndPassword(userCredentials.getLogin(), userCredentials.getPassword());
        if (user == null) {
            return ResponseEntity.status(401).body("Wrong login or password.");  // 401 Unauthorized
        }

        String jwt = userService.createUserJwt(user);
        return ResponseEntity.ok(jwt);  // 200 OK
    }

    @Operation(summary = "Retrieve user by JWT",
            description = "Finds a user based on the provided JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("users/jwt")
    public ResponseEntity<User> findUserByJwt(
            @Parameter(description = "JWT for the user", required = true)
            @RequestParam String jwt) {
        User user = userService.findByJwt(jwt);
        if (user == null) {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
        return ResponseEntity.ok(user);  // 200 OK
    }

    @Operation(summary = "Retrieve all users",
            description = "Fetches a list of all registered users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved users")
    })
    @GetMapping("users")
    public ResponseEntity<List<User>> findUsers() {
        List<User> users = this.userService.findAll();
        return ResponseEntity.ok(users);  // 200 OK
    }

    @Operation(summary = "Register a new user",
            description = "Creates a new user account using the provided credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation errors")
    })
    @PostMapping("users/register")
    public ResponseEntity<Object> addUser(
            @Parameter(description = "User credentials for registration", required = true)
            @RequestBody @Valid UserCredentialsRegister userCredentials,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(bindingResult));  // 400 Bad Request
        }

        User newUser = userService.addUserByCredentials(userCredentials);
        return ResponseEntity.status(201).body(newUser);  // 201 Created
    }

    @Operation(summary = "Delete a user",
            description = "Deletes the specified user. Only the user themselves or an admin can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden action"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("users/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable long id,
            @Parameter(description = "JWT token for authentication", required = true)
            @RequestParam String jwt) {
        User authenticatedUser = userService.findByJwt(jwt);
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).build(); // 41 Unauthorized
        }

        User userToDelete = userService.findById(id);
        if (userToDelete == null) {
            return ResponseEntity.status(404).build(); // 404 Not found
        }

        if (authenticatedUser.getId() != userToDelete.getId() && !authenticatedUser.isAdmin()) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        userService.deleteUserById(id);
        return ResponseEntity.ok().build(); // 200 Ok
    }

    @Operation(summary = "Edit user profile",
            description = "Allows a user to edit their profile or an admin to edit any user's profile.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation errors"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Forbidden action"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("users/{id}")
    public ResponseEntity<Object> editUserProfile(
            @Parameter(description = "ID of the user to edit", required = true) @PathVariable long id,
            @Parameter(description = "Updated user profile", required = true) @RequestBody @Valid UserCredentialsEditForm userProfileEdit,
            BindingResult bindingResult,
            @Parameter(description = "JWT token for authentication", required = true) @RequestParam String jwt) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getValidationErrors(bindingResult)); // 400 Bad Request
        }

        User authenticatedUser = userService.findByJwt(jwt);
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        User userToEdit = userService.findById(id);
        if (userToEdit == null) {
            return ResponseEntity.status(404).build(); // 404 Not Found
        }

        if (authenticatedUser.getId() != userToEdit.getId() && !authenticatedUser.isAdmin()) {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        userToEdit.setName(userProfileEdit.getName());
        userService.saveUser(userToEdit);

        return ResponseEntity.ok(userToEdit); // 200 OK
    }
}
