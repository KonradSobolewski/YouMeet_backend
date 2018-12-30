package youmeet.wpam.DTO;

import javax.validation.constraints.NotNull;

public class ModifyMeetingDTO {

    @NotNull
    private Long meeting_id;

    private String pickedTime;

    private String description;

    private Boolean is_one_to_one;

    private Long category;

    public Long getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(Long meeting_id) {
        this.meeting_id = meeting_id;
    }

    public String getpickedTime() {
        return pickedTime;
    }

    public void setpickedTime(String pickedTime) {
        this.pickedTime = pickedTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIs_one_to_one() {
        return is_one_to_one;
    }

    public void setIs_one_to_one(Boolean is_one_to_one) {
        this.is_one_to_one = is_one_to_one;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }
}
