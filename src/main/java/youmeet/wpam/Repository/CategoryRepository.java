package youmeet.wpam.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import youmeet.wpam.DTO.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT * FROM categories", nativeQuery = true)
    List<Category> getCategories();

}
