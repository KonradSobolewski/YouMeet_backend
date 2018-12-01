package youmeet.wpam.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import youmeet.wpam.DTO.Meeting;
import youmeet.wpam.DTO.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long Id);

    @Query(value = "SELECT * from users where email = ?1", nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT * FROM meeting", nativeQuery = true)
    List<Meeting> getMeetings();

    boolean existsByEmail(String email);
}
