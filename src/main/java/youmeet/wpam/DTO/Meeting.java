package youmeet.wpam.DTO;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "meeting")
public class Meeting extends Params{

    @Id
    @Column(name = "meeting_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long meeting_id;

    @Column(name = "place_longitude")
    private String place_longitude;

    @Column(name = "place_latitude")
    private String place_latitude;

    @Column(name = "is_one_to_one", nullable = false)
    private Boolean isOneToOne;

    @Column(name = "inviter_id", nullable = false)
    private Long inviter_id;


    public Meeting() {

    }

    public Boolean getOneToOne() {
        return isOneToOne;
    }

    public void setOneToOne(Boolean oneToOne) {
        isOneToOne = oneToOne;
    }

    public Meeting(String place_longitude, String place_latitude, Long inviter_id) {
        this.place_longitude = place_longitude;
        this.place_latitude = place_latitude;
        this.inviter_id = inviter_id;
    }

    public Long getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(Long meeting_id) {
        this.meeting_id = meeting_id;
    }

    public String getPlace_longitude() {
        return place_longitude;
    }

    public void setPlace_longitude(String place_longitude) {
        this.place_longitude = place_longitude;
    }

    public String getPlace_latitude() {
        return place_latitude;
    }

    public void setPlace_latitude(String place_latitude) {
        this.place_latitude = place_latitude;
    }

    public Long getInviter_id() {
        return inviter_id;
    }

    public void setInviter_id(Long inviter_id) {
        this.inviter_id = inviter_id;
    }
}
