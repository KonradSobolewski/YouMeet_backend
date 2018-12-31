package youmeet.wpam.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import youmeet.wpam.DTO.MeetingDTO;
import youmeet.wpam.DTO.ModifyMeetingDTO;
import youmeet.wpam.Entities.Meeting;
import youmeet.wpam.Entities.User;
import youmeet.wpam.Repository.CategoryRepository;
import youmeet.wpam.Repository.MeetingRepository;
import youmeet.wpam.Repository.UserRepository;
import youmeet.wpam.config.utils.functionService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static youmeet.wpam.config.utils.UtilsKeys.*;
import static youmeet.wpam.config.utils.functionService.getIntegerArray;
import static youmeet.wpam.config.utils.functionService.getLongArray;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserHobbiesService userHobbiesService;

    @Transactional
    public Meeting saveMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }

    public void deleteExpiredMeetings(List<Meeting> meetings) {
        List<Meeting> filteredMeetings = meetings
                .stream()
                .filter(m-> !m.hasParam(ACCEPTED_JOINER) && isMeetingExpired(m))
                .collect(Collectors.toList());

        filteredMeetings.forEach(m-> meetingRepository.deleteById(m.getMeeting_id()));
    }

    private boolean isMeetingExpired(Meeting meeting) {
        if (meeting.hasParam(PICKED_TIME)) {
            String pickedTime = meeting.getStringParam(PICKED_TIME,"");
            ZonedDateTime now = ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);
            ZonedDateTime meetingTime = now.with(
                    LocalTime.of(
                            Integer.parseInt(pickedTime.substring(0,2)),
                            Integer.parseInt(pickedTime.substring(3))
                    )
            );
            return meetingTime.isBefore(now);
        }
        return true;
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
        meeting.addParam(PICKED_TIME, dto.getPickedTime());

        return saveMeeting(meeting);
    }

    public List<Meeting> getMeetings(Long user_id, Long minAge, Long maxAge, String gender) {

        List<Meeting> meetings = meetingRepository.getMeetings(user_id);
        deleteExpiredMeetings(meetings);

        meetings = meetingRepository.getMeetings(user_id);

        List<Meeting> meetingToSend = new ArrayList<>();
        meetings.forEach( meeting -> {
            Optional<User> invited = userRepository.findById(meeting.getInviter_id());
            invited.ifPresent( in -> {
                List<String> commonHobbies = userHobbiesService.getCommonHobbies(in, user_id);
                if (userService.checkPeronalInformations(in ,minAge, maxAge, gender) && !commonHobbies.isEmpty()){
                    meeting.addParam(PHOTO, in.getStringParam(PHOTO, null));
                    meeting.addParam(FIRST_NAME, in.getFirstName());
                    meeting.addParam(LAST_NAME, in.getLastName());
                    meeting.addParam(GENDER, in.getStringParam(GENDER,""));
                    meeting.addParam(AGE, in.getParam(AGE));
                    meeting.addParam(EMAIL, in.getEmail());
                    meeting.addParam(COMMON_HOBBIES, commonHobbies);
                    meetingToSend.add(meeting);
                }
            });
        });
        return meetingToSend;
    }

    public List<Meeting> getRecentUserMeetings(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if(!user.isPresent()) {
            return Collections.emptyList();
        }

        List<Meeting> meetings = meetingRepository.getRecentMeetings(user.get().getId());
        deleteExpiredMeetings(meetings);

        return meetingRepository.getRecentMeetings(user.get().getId()).stream().map(meeting -> {
            categoryRepository.findById(meeting.getCategory()).ifPresent(c -> {
                meeting.addParam(CATEGORY_NAME, c.getType());
            });
            return meeting;
        }).collect(Collectors.toList());
    }

    public void startMeeting(Meeting meeting) {
            if(meeting.hasParam(ACCEPTED_JOINER)) {
                String pickedTime = meeting.getStringParam(PICKED_TIME, "");
                ZonedDateTime now = ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC);
                ZonedDateTime meetingTime = now.with(
                        LocalTime.of(
                                Integer.parseInt(pickedTime.substring(0,2)),
                                Integer.parseInt(pickedTime.substring(3))
                        )
                );
                meeting.addParam(START_DATE, meetingTime.toString());
                meeting.addParam(IS_SUCCESSFUL, true);
            }
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
                    List<Integer> existingJoiners = getIntegerArray(m.getParam(JOINER_ID));
                    existingJoiners.add(joinerId.intValue());
                    m.addParam(JOINER_ID, existingJoiners);
                }
                m.addParam(NEW_JOINER, true);
                return meetingRepository.save(m);
        });
    }

    public List<Meeting> getUserSubscripedToMeetings(Long id) {
        List<Meeting> meetings = meetingRepository.getAllMeetingsWithSubscribers();
        meetings.forEach(meeting -> {
            Optional<User> inviterOpt = userRepository.findById(meeting.getInviter_id());
            inviterOpt.ifPresent( inviter -> {
                fillMeetingWithInviterInfo(meeting, inviter);
            });

            categoryRepository.findById(meeting.getCategory()).ifPresent(c -> {
                meeting.addParam(CATEGORY_NAME, c.getType());
            });
        });
        return meetings.stream()
               .filter(m ->
               getIntegerArray(m.getParam(JOINER_ID)).contains(id.intValue()) ||
               getIntegerArray(m.getParam(ACCEPTED_JOINER)).contains(id.intValue())        )
               .collect(Collectors.toList());
    }

    public List<Meeting> getMeetingsWithNewJoiners(Long id) {
        List<Meeting> meetings = meetingRepository.getAllMeetingsForInviter(id);
        return meetings.stream().filter(m -> !getIntegerArray(m.getParam(JOINER_ID)).isEmpty()).collect(Collectors.toList());
    }

    public Optional<Meeting> acceptNewJoinerInMeeting(Long id, Long newJoinerId) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        if (!meeting.isPresent())
            return Optional.empty();
        meeting.filter(m -> m.hasParam(JOINER_ID) && getIntegerArray(m.getParam(JOINER_ID)).contains(new Integer(newJoinerId.intValue())))
                .map(m -> {
                    List<Integer> newJoinersList = getIntegerArray(m.getParam(JOINER_ID));
                    newJoinersList.remove(new Integer(newJoinerId.intValue()));
                    m.addParam(JOINER_ID, newJoinersList);
                    if (m.hasParam(ACCEPTED_JOINER)) {
                        List<Integer> acceptedJoinersList = getIntegerArray(m.getParam(ACCEPTED_JOINER));
                        acceptedJoinersList.add(newJoinerId.intValue());
                        m.addParam(ACCEPTED_JOINER, acceptedJoinersList);
                    } else
                        m.addParam(ACCEPTED_JOINER, new ArrayList<>(Arrays.asList(newJoinerId.intValue())));
                    startMeeting(m);
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
            if(meeting.getInviter_id().equals(user.get().getId())) {
                List<Integer> acceptedJoiners = getIntegerArray(meeting.getParam(ACCEPTED_JOINER));
                if (!acceptedJoiners.isEmpty()) {
                    Long id = new Long(acceptedJoiners.get(0));
                    Optional<User> invited = userRepository.findById(id);
                    invited.ifPresent( in -> {
                        fillMeetingWithInviterInfo(meeting, in);
                    });
                }
                //TODO jak w accepted joiner jest wiecej niz 1
            } else {
                Optional<User> invited = userRepository.findById(meeting.getInviter_id());
                invited.ifPresent( in -> {
                    fillMeetingWithInviterInfo(meeting, in);
                });
            }

            categoryRepository.findById(meeting.getCategory()).ifPresent(c -> {
                meeting.addParam(CATEGORY_NAME, c.getType());
            });
        });

        return meetings.stream().filter(m -> m.hasParam(FIRST_NAME)).collect(Collectors.toList());
    }

    private void fillMeetingWithInviterInfo(Meeting meeting, User user) {
        meeting.addParam(FIRST_NAME, user.getFirstName());
        meeting.addParam(LAST_NAME, user.getLastName());
        meeting.addParam(GENDER, user.getStringParam(GENDER,""));
        meeting.addParam(AGE, user.getParam(AGE));
        meeting.addParam(PHOTO, user.getStringParam(PHOTO, null));
    }

    public void deleteMeetingById(Long id) {
        meetingRepository.deleteById(id);
    }

    @Transactional
    public void modifyMeeting(ModifyMeetingDTO dto) {
        Optional<Meeting> meeting = meetingRepository.findById(dto.getMeeting_id());
        meeting.ifPresent(m-> {
            if(dto.getIs_one_to_one() != null)
                m.setIs_one_to_one(dto.getIs_one_to_one());
            if(dto.getCategory()!= null)
                m.addParam(CATEGORY_NAME,dto.getCategory());
            if(dto.getDescription() != null) {
                m.addParam(DESCRIPTION, dto.getDescription());
            }
            if(dto.getpickedTime() != null) {
                m.addParam(PICKED_TIME, dto.getpickedTime());
            }
        });
    }
}
