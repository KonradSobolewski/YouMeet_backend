package youmeet.wpam.Contollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import youmeet.wpam.Services.UserHobbiesService;

import static youmeet.wpam.config.utils.UtilsKeys.ROLE_ADMIN;
import static youmeet.wpam.config.utils.UtilsKeys.ROLE_USER;

@Controller
public class UserHobbiesController {

    @Autowired
    private UserHobbiesService userHobbiesService;

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/api/getUserHobbies")
    public ResponseEntity getUserHobbies(@RequestParam(value = "email") String email) {
        return ResponseEntity.ok(userHobbiesService.getAllUserHobbies(email));
    }
}
