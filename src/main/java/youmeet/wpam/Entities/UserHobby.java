package youmeet.wpam.Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_hobby")
public class UserHobby extends Params {

    @Id
    @Column(name = "user_id", unique = true)
    private Long user_id;

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

}
