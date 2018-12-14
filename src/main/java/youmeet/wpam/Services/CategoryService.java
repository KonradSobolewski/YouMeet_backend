package youmeet.wpam.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import youmeet.wpam.Entities.Category;
import youmeet.wpam.Repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getCategories() { return categoryRepository.getCategories(); }
}
