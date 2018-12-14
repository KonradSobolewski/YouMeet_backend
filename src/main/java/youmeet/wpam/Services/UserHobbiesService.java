package youmeet.wpam.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import youmeet.wpam.Entities.Hobby;
import youmeet.wpam.Entities.UserHobby;
import youmeet.wpam.Entities.User;
import youmeet.wpam.Repository.HobbyRepository;
import youmeet.wpam.Repository.UserHobbiesRepository;
import youmeet.wpam.Repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static youmeet.wpam.config.utils.UtilsKeys.*;
import static youmeet.wpam.config.utils.functionService.getIntegerArray;

@Service
public class UserHobbiesService {

    @Autowired
    private UserHobbiesRepository userHobbiesRepository;

    @Autowired
    private HobbyRepository hobbyRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<UserHobby> getUserHobbyByUserId (Long user_id) {
       return userHobbiesRepository.getByUserId(user_id);
    }

    public List<Hobby> getAllUserHobbies(String email){
        Optional<User> user = userRepository.findByEmail(email);
        if(!user.isPresent())
            return null;

        Optional<UserHobby> userHobby = userHobbiesRepository.getByUserId(user.get().getId());

        if (!userHobby.isPresent())
            return null;

        List<Integer> hobbies = getIntegerArray(userHobby.get().getParam(HOBBIES));

        return hobbies.stream().map( h -> hobbyRepository.findById(h)).collect(Collectors.toList());
    }

    public void addNewHobbiesToUser(User user, List<String> dtoHobbies) {
        userHobbiesRepository.getByUserId(user.getId()).ifPresent( userHobby -> {
            List<Long> hobbiesById = dtoHobbies
                    .stream()
                    .map( ah -> hobbyRepository.findByName(ah).getId())
                    .collect(Collectors.toList());

            userHobby.addParam(HOBBIES, hobbiesById);
            userHobbiesRepository.save(userHobby);
        });
    }
}
