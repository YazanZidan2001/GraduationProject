package com.example.GraduationProject.WebApi.Controllers.Admin.Users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.Common.Enums.Role;
import com.example.GraduationProject.Common.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController extends SessionManagement {

    private final AuthenticationService service;

    //    @PostMapping("/")
//    public ResponseEntity<AuthenticationResponse> adduser(@RequestBody @Valid User request,HttpServletRequest httpServletRequest) throws UserNotFoundException, IOException {
//        String token = service.extractToken(httpServletRequest);
//        User user = service.extractUserFromToken(token);
//        validateLoggedInAdmin(user);
//        return ResponseEntity.ok(service.adduser(request));
//    }
    @PostMapping("/")
    public ResponseEntity<AuthenticationResponse> adduser(@RequestBody @Valid User request) throws UserNotFoundException, IOException {
        // Debugging: Log the incoming request
        System.out.println("Incoming user request: " + request);

        // Ensure UserID is set in the request
        if (request.getUserID() == null) {
            throw new IllegalArgumentException("UserID must be provided.");
        }

        // Call the service to add the user with the provided UserID
        return ResponseEntity.ok(service.addUser(request.getUserID(), request));
    }





    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateUser(@RequestBody @Valid User request, @PathVariable Long id, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return ResponseEntity.ok(service.UpdateUser(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteUser(@PathVariable Long id, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return ResponseEntity.ok(service.DeleteUser(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return ResponseEntity.ok(service.GetUser(id));
    }

    @GetMapping("")
    public PaginationDTO<User> getAllUsers(@RequestParam(defaultValue = "1", required = false) int page,
                                           @RequestParam(defaultValue = "10", required = false) int size,
                                           @RequestParam(defaultValue = "", required = false) String search,
                                           @RequestParam(defaultValue = "", required = false) Role role, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return service.GetAllUsers(page, size, search, role);
    }

    @GetMapping("byRole/{role}")
    public Page<User> getAllUsersByRole(@PathVariable Role role, @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return service.getAllUsersByRole(role, page, size);
    }


    @PostMapping("/changePassword")
    public ResponseEntity<AuthenticationResponse> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            HttpServletRequest httpServletRequest) throws UserNotFoundException {

        // Extract token and user
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);

        // Validate that the logged-in user exists
        validateLoggedInAllUser(user);

        // Call the service to change the password
        AuthenticationResponse response = service.changePasswordForToken(user, oldPassword, newPassword);
        return ResponseEntity.ok(response);
    }


}
