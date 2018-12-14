package youmeet.wpam.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import youmeet.wpam.Entities.Hobby;

@Repository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    @Query(value = "SELECT * FROM hobby WHERE name = ?1", nativeQuery = true)
    Hobby findByName(String name);


    @Query(value = "SELECT * FROM hobby WHERE id = ?1", nativeQuery = true)
    Hobby findById(int id);
}
