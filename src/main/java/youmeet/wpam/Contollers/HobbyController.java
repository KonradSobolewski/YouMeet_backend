package youmeet.wpam.Contollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import youmeet.wpam.Services.HobbyService;

import static youmeet.wpam.config.utils.UtilsKeys.ROLE_ADMIN;
import static youmeet.wpam.config.utils.UtilsKeys.ROLE_USER;

@Controller
public class HobbyController {

    @Autowired
    private HobbyService hobbyService;

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/api/getAllHobbies")
    public ResponseEntity getUserHobbies() {
        return ResponseEntity.ok(hobbyService.getAllHobbies());
    }

}
