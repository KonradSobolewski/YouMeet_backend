package youmeet.wpam.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import youmeet.wpam.DTO.*;
import youmeet.wpam.DTO.SmallDTO.UserSmallDTO;
import youmeet.wpam.Repository.CategoryRepository;
import youmeet.wpam.Repository.MeetingRepository;
import youmeet.wpam.Repository.UserRepository;
import youmeet.wpam.config.JWTConfig.TokenAuthenticationService;
import youmeet.wpam.exceptions.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static youmeet.wpam.config.utils.UtilsKeys.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleService roleService;


    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User getUserByEmail(String email) throws UserNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UserNotFoundException("User not found");
        }

    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        user.ifPresent(u -> {
            userRepository.deleteById(id);
        });
    }


    public User createUserBody(UserSmallDTO dto) {
        User user = new User();

        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        user.setParams(
                new HashMap<String, Object>() {{
                    put(CREATION_DATE, ZonedDateTime.of(LocalDateTime.now(), ZoneOffset.UTC).toString());
                    if (dto.hasParam(PHOTO)) {
                        put(CREATION_DATE, dto.getStringParam(PHOTO, null));
                    }
                }}
        );

        User returnedUser = saveUser(user);
        Set<Role> roles = new HashSet<Role>() {{
            add(new Role(ROLE_USER, returnedUser));
        }};

        user.setRoles(roles.stream().map(r -> roleService.saveRole(r)).collect(Collectors.toSet()));

        return user;
    }

    public boolean checkIfUserExistsByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(s);
        return user.map(UserSecured::new).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public String createTokenForUser(String email) throws UserNotFoundException {
        if (userRepository.existsByEmail(email)){
            String token = TokenAuthenticationService.generateAuthentication(email);
            Authentication authentication = TokenAuthenticationService
                    .getAuthenticationForFb(token);

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            return token;
        }
        else
            throw new UserNotFoundException("Username not found");
    }

    public User createFbUserAccount(UserSmallDTO dto) {
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        return user.orElseGet(() -> createUserBody(dto));
    }

    @Transactional
    public Meeting saveMeeting(Meeting meeting) {
        return meetingRepository.save(meeting);
    }


    public Meeting createMeeting(Meeting dto) {
        Meeting meeting = new Meeting();

        meeting.setPlace_latitude("0");
        meeting.setPlace_longitude("0");
        meeting.setIs_one_to_one(dto.getIs_one_to_one());
        meeting.setInviter_id(dto.getInviter_id());
        meeting.setCategory(dto.getCategory());
        if (dto.hasParam(DESCRIPTION))
            meeting.addParam(DESCRIPTION, dto.getStringParam(DESCRIPTION, null));

        saveMeeting(meeting);
        return saveMeeting(meeting);
    }

    public List<Meeting> getMeetings(Long user_id) {
        return meetingRepository.getMeetings(user_id);
    }

    public List<Category> getCategories() { return categoryRepository.getCategories(); }
}
