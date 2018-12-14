package youmeet.wpam.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import youmeet.wpam.Entities.UserHobby;

import java.util.Optional;

@Repository
public interface UserHobbiesRepository extends JpaRepository<UserHobby, Long> {

    @Query(value = "SELECT * FROM user_hobby WHERE user_id = ?1", nativeQuery = true)
    Optional<UserHobby> getByUserId(Long id);

}
