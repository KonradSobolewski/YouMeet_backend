package youmeet.wpam.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import youmeet.wpam.DTO.MeetingDTO;
import youmeet.wpam.Entities.Meeting;
import youmeet.wpam.Entities.User;
import youmeet.wpam.Repository.CategoryRepository;
import youmeet.wpam.Repository.MeetingRepository;
import youmeet.wpam.Repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static youmeet.wpam.config.utils.UtilsKeys.*;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public Meeting saveMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }


    public Meeting createMeeting(MeetingDTO dto) {
        Meeting meeting = new Meeting();

        meeting.setPlace_latitude(dto.getPlace_latitude());
        meeting.setPlace_longitude(dto.getPlace_longitude());
        meeting.setIs_one_to_one(dto.getIs_one_to_one());
        meeting.setInviter_id(dto.getInviter_id());
        meeting.setCategory(dto.getCategory());
        if (dto.getDescription() != null)
            meeting.addParam(DESCRIPTION, dto.getDescription());

        if (dto.getPlaceDescription() != null)
            meeting.addParam(PLACE_DESCRIPTION, dto.getPlaceDescription());

        meeting.addParam(CREATION_DATE, ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC).toString());

        return saveMeeting(meeting);
    }

    public List<Meeting> getMeetings(Long user_id) {
        return meetingRepository.getMeetings(user_id);
    }


    public Optional<Meeting> startMeeting(Long id) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        return meeting.map(m -> {
            m.addParam(START_DATE, ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC).toString());
            m.addParam(IS_SUCCESSFUL, true);
            return meetingRepository.save(m);
        });
    }

    public List<Meeting> getUserMeetingHistory(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if(!user.isPresent())
            return Collections.emptyList();

        List<Meeting> meetings = meetingRepository.findAllByInviterId(user.get().getId());
        if(meetings.isEmpty())
            return Collections.emptyList();

        meetings.forEach(meeting -> {
            Optional<User> invited = userRepository.findByEmail(meeting.getStringParam(INVITED_ONE, ""));
            invited.ifPresent( in -> {
                meeting.addParam(FIRST_NAME, in.getFirstName());
                meeting.addParam(LAST_NAME, in.getLastName());
                meeting.addParam(GENDER, in.getStringParam(GENDER,""));
                meeting.addParam(AGE, in.getParam(AGE));
                meeting.addParam(PHOTO, in.getStringParam(PHOTO, null));
            });

            categoryRepository.findById(meeting.getCategory()).ifPresent(c -> {
                meeting.addParam(CATEGORY_NAME, c.getType());
            });
        });

        return meetings.stream().filter(m -> m.hasParam(FIRST_NAME)).collect(Collectors.toList());
    }
}
