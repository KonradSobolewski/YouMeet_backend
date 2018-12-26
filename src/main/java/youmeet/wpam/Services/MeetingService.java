package youmeet.wpam.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import youmeet.wpam.DTO.MeetingDTO;
import youmeet.wpam.Entities.Meeting;
import youmeet.wpam.Entities.User;
import youmeet.wpam.Repository.CategoryRepository;
import youmeet.wpam.Repository.MeetingRepository;
import youmeet.wpam.Repository.UserRepository;
import youmeet.wpam.config.utils.functionService;

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

        List<Meeting> meetings = meetingRepository.getMeetings(user_id);

        meetings.forEach( meeting -> {
            Optional<User> invited = userRepository.findById(meeting.getInviter_id());
            invited.ifPresent( in -> {
                meeting.addParam(PHOTO, in.getStringParam(PHOTO, null));
                meeting.addParam(FIRST_NAME, in.getFirstName());
                meeting.addParam(LAST_NAME, in.getLastName());
                meeting.addParam(GENDER, in.getStringParam(GENDER,""));
                meeting.addParam(AGE, in.getParam(AGE));
                meeting.addParam(EMAIL, in.getEmail());
            });
        });
        return meetings;
    }


    public Optional<Meeting> startMeeting(Long id) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        return meeting.map(m -> {
            m.addParam(START_DATE, ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC).toString());
            m.addParam(IS_SUCCESSFUL, true);
            return meetingRepository.save(m);
        });
    }

    public HttpStatus cancelMeeting(Long id) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        if(meeting.isPresent()) {
            meetingRepository.delete(meeting.get());
            return HttpStatus.OK;
        }
        return HttpStatus.BAD_REQUEST;
    }

    public Optional<Meeting> joinMeeting(Long id, Long joinerId) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        if(!meeting.isPresent())
            return Optional.empty();
        return meeting.map(m -> {
                if(!m.hasParam(JOINER_ID))
                    m.addParam(JOINER_ID, new ArrayList<>(Arrays.asList(joinerId)));
                else if(m.hasParam(JOINER_ID) && m.getIs_one_to_one() == true)
                    return m;
                else {
                    List<Integer> existingJoiners = functionService.getIntegerArray(m.getParam(JOINER_ID));
                    existingJoiners.add(joinerId.intValue());
                    m.addParam(JOINER_ID, existingJoiners);
                }
                m.addParam(NEW_JOINER, true);
                return meetingRepository.save(m);
        });
    }

    public List<Meeting> getUserSubscripedToMeetings(Long id) {
        List<Meeting> meetings = meetingRepository.getAllMeetingsWithSubscribers();
        return meetings.stream().filter(m -> functionService.getIntegerArray(m.getParam(JOINER_ID)).contains(id.intValue())).collect(Collectors.toList());
    }

    public List<Meeting> getMeetingsWithNewJoiners(Long id) {
        List<Meeting> meetings = meetingRepository.getAllMeetingsForInviter(id);
        return meetings.stream().filter(m -> !functionService.getIntegerArray(m.getParam(NEW_JOINER)).isEmpty()).collect(Collectors.toList());
    }

    public Optional<Meeting> acceptNewJoinerInMeeting(Long id, Long newJoinerId) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        if(!meeting.isPresent())
            return Optional.empty();
        meeting.filter(m-> m.hasParam(JOINER_ID) && functionService.getIntegerArray(m.getParam(JOINER_ID)).contains(new Integer(newJoinerId.intValue())))
                .map(m -> {
                List<Integer> newJoinersList = functionService.getIntegerArray(m.getParam(JOINER_ID));
                newJoinersList.remove(new Integer(newJoinerId.intValue()));
                m.addParam(JOINER_ID, newJoinersList);
                if(m.hasParam(ACCEPTED_JOINER)) {
                    List<Integer> acceptedJoinersList = functionService.getIntegerArray(m.getParam(ACCEPTED_JOINER));
                    acceptedJoinersList.add(newJoinerId.intValue());
                    m.addParam(ACCEPTED_JOINER, acceptedJoinersList);
                }
                else
                    m.addParam(ACCEPTED_JOINER, new ArrayList<>(Arrays.asList(newJoinerId.intValue())));
                return m;
        });
        meetingRepository.save(meeting.get());
        return meeting;
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
