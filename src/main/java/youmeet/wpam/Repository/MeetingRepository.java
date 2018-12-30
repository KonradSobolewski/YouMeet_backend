package youmeet.wpam.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import youmeet.wpam.Entities.Meeting;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query(value = "SELECT * FROM meeting WHERE inviter_id <> ?1 and params->>'isSuccessful' is null", nativeQuery = true)
    List<Meeting> getMeetings(Long user_id);

    @Query(value = "SELECT * FROM meeting WHERE inviter_id = ?1 and params->>'isSuccessful' is null", nativeQuery = true)
    List<Meeting> getRecentMeetings(Long user_id);

    @Query(value = "SELECT * FROM meeting WHERE inviter_id = ?1 and " +
            "params->>'isSuccessful' = 'true' " +
            "ORDER BY to_timestamp(params->>'startDate', 'yyyy-MM-dd HH24:MI:SS') DESC",nativeQuery = true)
    List<Meeting> findAllByInviterId(Long id);

    @Query(value = "SELECT * FROM meeting WHERE params->>'joinerId' is not null", nativeQuery = true)
    List<Meeting> getAllMeetingsWithSubscribers();

    @Query(value = "SELECT * FROM meeting WHERE params->>'joinerId' is not null AND inviter_id = ?1", nativeQuery = true)
    List<Meeting> getAllMeetingsForInviter(Long id);

}
