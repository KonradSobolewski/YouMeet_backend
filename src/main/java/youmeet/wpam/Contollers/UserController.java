package youmeet.wpam.Contollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import youmeet.wpam.DTO.User;
import youmeet.wpam.DTO.SmallDTO.UserSmallDTO;
import youmeet.wpam.Services.UserService;
import youmeet.wpam.exceptions.UserAlreadyExists;
import youmeet.wpam.exceptions.UserNotFoundException;

import static youmeet.wpam.config.UtilsKeys.*;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    @Autowired
    private UserService userService;

    //    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/getAll")
    public ResponseEntity getAllPosts() {
        return new ResponseEntity(userService.findAll(), HttpStatus.OK);
    }

    //    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/getUserById")
    public ResponseEntity getEquipmentById(@RequestParam(value = "id") Long id) {
        try {
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (UserNotFoundException e) {
            return (ResponseEntity) ResponseEntity.notFound();
        }

    }

    //    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @DeleteMapping(value = "deleteUser")
    public ResponseEntity deleteUserById(@RequestParam(value = "id") Long id) {
        if (id != null) {
            userService.deleteUserById(id);
            return ResponseEntity.ok(HttpEntity.EMPTY);
        }
        return (ResponseEntity) ResponseEntity.notFound();
    }

    //    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @PostMapping(value = "createUser")
    public ResponseEntity createUser(@RequestBody UserSmallDTO dto) throws UserAlreadyExists {
        if (dto == null) {
            return (ResponseEntity) ResponseEntity.badRequest();
        }

        if (userService.checkIfUserExistsByEmail(dto.getEmail())) {
            throw new UserAlreadyExists();
        }

        User user = userService.saveUser(
                userService.createUserBody(dto)
        );

        return ResponseEntity.ok(user);
    }
}
