package edu.miu.cs.cs544.controller;

import edu.miu.cs.cs544.domain.CustomError;
import edu.miu.cs.cs544.domain.User;
import edu.miu.cs.cs544.domain.dto.UserDTO;
import edu.miu.cs.cs544.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) throws CustomError {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) throws CustomError {
        userService.deleteUser(id);
        return new ResponseEntity<>("User deleted successfully",HttpStatus.OK);
    }

    @PatchMapping ("/{id}")
    public ResponseEntity<UserDTO> updateUserDetails(@PathVariable int id, @RequestBody UserDTO user) {
        try {
            userService.updateUserDetails(id,user);
            return ResponseEntity.ok(user);
        } catch (CustomError e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}