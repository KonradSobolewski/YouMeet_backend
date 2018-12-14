package youmeet.wpam.Contollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import youmeet.wpam.DTO.MeetingDTO;
import youmeet.wpam.Entities.Meeting;
import youmeet.wpam.Services.MeetingService;

import javax.validation.Valid;

import static youmeet.wpam.config.utils.UtilsKeys.ROLE_ADMIN;
import static youmeet.wpam.config.utils.UtilsKeys.ROLE_USER;

@Controller
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/api/getMeetings")
    public ResponseEntity getMeetings(@RequestParam(value = "user_id") Long user_id) {
        return ResponseEntity.ok(meetingService.getMeetings(user_id));

    }

    @PostMapping(value = "api/createMeeting")
    public ResponseEntity createMeeting(@Valid @RequestBody MeetingDTO dto) {
        if (dto == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(meetingService.createMeeting(dto));
    }
}
