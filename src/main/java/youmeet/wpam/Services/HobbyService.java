package youmeet.wpam.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import youmeet.wpam.Entities.Hobby;
import youmeet.wpam.Repository.HobbyRepository;

import java.util.List;

@Service
public class HobbyService {

    @Autowired
    private HobbyRepository hobbyRepository;

    public List<Hobby> getAllHobbies() {
        return hobbyRepository.findAll();
    }

    public void addHobby(Hobby hobby) {
        hobbyRepository.save(hobby);
    }
}
