package youmeet.wpam.Contollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import youmeet.wpam.DTO.User;
import youmeet.wpam.DTO.SmallDTO.UserSmallDTO;
import youmeet.wpam.Services.UserService;
import youmeet.wpam.exceptions.UserAlreadyExists;
import youmeet.wpam.exceptions.UserNotFoundException;

import javax.validation.Valid;

import static youmeet.wpam.config.utils.UtilsKeys.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/api/getAll")
    public ResponseEntity getAllPosts() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/api/getUserById")
    public ResponseEntity getUserById(@RequestParam(value = "id") Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (UserNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @DeleteMapping(value = "/api/deleteUser")
    public ResponseEntity deleteUserById(@RequestParam(value = "id") Long id) {
        if (id != null) {
            userService.deleteUserById(id);
            return ResponseEntity.ok(HttpEntity.EMPTY);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/createUser")
    public ResponseEntity createUser(@Valid @RequestBody UserSmallDTO dto) throws UserAlreadyExists {
        if (dto == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (userService.checkIfUserExistsByEmail(dto.getEmail())) {
            throw new UserAlreadyExists();
        }

        User user = userService.createUserBody(dto);

        return ResponseEntity.ok(user);
    }
}
