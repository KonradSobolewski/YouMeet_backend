package youmeet.wpam.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import youmeet.wpam.Entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

}
