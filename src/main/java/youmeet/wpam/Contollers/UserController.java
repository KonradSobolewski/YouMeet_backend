package youmeet.wpam.Contollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import youmeet.wpam.Services.UserService;
import youmeet.wpam.exceptions.UserNotFoundException;


@Controller
@RequestMapping(value = "/api")
public class UserController {

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/getAll", produces = "application/json")
    public ResponseEntity getAllPosts() {
        return new ResponseEntity(userService.findAll(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping(value = "/getUserById")
    public ResponseEntity getEquipmentById(@RequestParam(value = "id") Long id) {
        try{
            return new ResponseEntity(userService.getUserById(id), HttpStatus.OK);
        }catch (UserNotFoundException e){
            return (ResponseEntity) ResponseEntity.notFound();
        }

    }
}
