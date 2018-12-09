package youmeet.wpam.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import youmeet.wpam.DTO.Category;
import youmeet.wpam.DTO.Meeting;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query(value = "SELECT * FROM meeting WHERE inviter_id <> ?1", nativeQuery = true)
    List<Meeting> getMeetings(Long user_id);



}
