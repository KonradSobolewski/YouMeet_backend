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
import youmeet.wpam.DTO.ModifyMeetingDTO;
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
    public ResponseEntity getMeetings(@RequestParam(value = "user_id") Long user_id,
                                      @RequestParam(value = "minAge") Long minAge,
                                      @RequestParam(value = "maxAge") Long maxAge,
                                      @RequestParam(value = "gender") String gender) {
        return ResponseEntity.ok(meetingService.getMeetings(user_id, minAge, maxAge, gender));

    }

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/api/deleteMeeting")
    public ResponseEntity deleteMeeting(@RequestParam(value = "id") Long id) {
        meetingService.deleteMeetingById(id);
        return ResponseEntity.ok(HttpStatus.OK);

    }

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @PostMapping(value = "/api/modifyMeeting")
    public ResponseEntity modifyMeeting(@Valid @RequestBody ModifyMeetingDTO dto) {
        meetingService.modifyMeeting(dto);
        return ResponseEntity.ok(HttpStatus.OK);

    }

    @Secured(value = {ROLE_ADMIN, ROLE_USER})
    @GetMapping(value = "/api/getRecentMeetings")
    public ResponseEntity getRecentUserMeetings(@RequestParam(value = "email") String email) {
        return ResponseEntity.ok(meetingService.getRecentUserMeetings(email));

    }

    @PostMapping(value = "api/createMeeting")
    public ResponseEntity createMeeting(@Valid @RequestBody MeetingDTO dto) {
        if (dto == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(meetingService.createMeeting(dto));
    }

    @GetMapping(value = "api/getUserMeetingHistory")
    public ResponseEntity getUserMeetingHistory(@RequestParam(value = "email") String email) {
        return ResponseEntity.ok(meetingService.getUserMeetingHistory(email));
    }

    @GetMapping(value = "api/joinMeeting")
    public ResponseEntity joinMeeting(@RequestParam(value = "id") Long id,
                                      @RequestParam(value = "joiner_id") Long joinerId) {
        return meetingService.joinMeeting(id, joinerId).
               map(ResponseEntity::ok).
               orElseGet(ResponseEntity.badRequest()::build) ;
    }

    @GetMapping(value = "api/cancelMeeting")
    public ResponseEntity cancelMeeting(@RequestParam(value="id") Long id) {
        return ResponseEntity.ok(meetingService.cancelMeeting(id));
    }

    @GetMapping(value = "api/getMeetingWithSubscribers")
    public ResponseEntity getUserSubscripedToMeetings(@RequestParam(value="id") Long id) {
        return ResponseEntity.ok(meetingService.getUserSubscripedToMeetings(id));
    }

    @GetMapping(value = "api/getMeetingsWithNewJoiners")
    public ResponseEntity getMeetingsWithNewJoiners(@RequestParam(value="id") Long id) {
        return ResponseEntity.ok(meetingService.getMeetingsWithNewJoiners(id));
    }

    @GetMapping(value = "api/acceptNewJoinerInMeeting")
    public ResponseEntity getMeetingsWithNewJoiners(@RequestParam(value="id") Long id,
                                                    @RequestParam(value="newJoinerId") Long newJoinerId) {
        return ResponseEntity.ok(meetingService.acceptNewJoinerInMeeting(id, newJoinerId));
    }

}
