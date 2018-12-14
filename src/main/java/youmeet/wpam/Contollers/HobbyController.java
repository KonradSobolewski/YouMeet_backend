package youmeet.wpam.Contollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import youmeet.wpam.Services.HobbyService;

@Controller
public class HobbyController {

    @Autowired
    private HobbyService hobbyService;

}
