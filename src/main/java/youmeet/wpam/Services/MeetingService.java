package youmeet.wpam.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import youmeet.wpam.DTO.MeetingDTO;
import youmeet.wpam.Entities.Meeting;
import youmeet.wpam.Repository.MeetingRepository;

import javax.transaction.Transactional;
import java.util.List;

import static youmeet.wpam.config.utils.UtilsKeys.DESCRIPTION;
import static youmeet.wpam.config.utils.UtilsKeys.PLACE_DESCRIPTION;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

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

        return saveMeeting(meeting);
    }

    public List<Meeting> getMeetings(Long user_id) {
        return meetingRepository.getMeetings(user_id);
    }
}
