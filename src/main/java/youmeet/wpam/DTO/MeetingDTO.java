package youmeet.wpam.DTO;

import javax.validation.constraints.NotNull;

public class MeetingDTO  {

    private Long meeting_id;

    @NotNull
    private String place_longitude;

    @NotNull
    private String place_latitude;

    @NotNull
    private Boolean is_one_to_one;

    @NotNull
    private Long inviter_id;

    @NotNull
    private Long category;

    @NotNull
    private String pickedTime;

    private String description;

    private String placeDescription;

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

    public Boolean getIs_one_to_one() {
        return is_one_to_one;
    }

    public void setIs_one_to_one(Boolean is_one_to_one) {
        this.is_one_to_one = is_one_to_one;
    }

    public Long getInviter_id() {
        return inviter_id;
    }

    public void setInviter_id(Long inviter_id) {
        this.inviter_id = inviter_id;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public String getPickedTime() {
        return pickedTime;
    }

    public void setPickedTime(String pickedTime) {
        this.pickedTime = pickedTime;
    }
}
