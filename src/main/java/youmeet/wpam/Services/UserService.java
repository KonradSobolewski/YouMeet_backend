package youmeet.wpam.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import youmeet.wpam.DTO.UserSmallDTO;
import youmeet.wpam.Entities.Role;
import youmeet.wpam.Entities.User;
import youmeet.wpam.Entities.UserSecured;
import youmeet.wpam.Repository.UserRepository;
import youmeet.wpam.config.JWTConfig.TokenAuthenticationService;
import youmeet.wpam.exceptions.UserNotFoundException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static youmeet.wpam.config.utils.UtilsKeys.*;
import static youmeet.wpam.config.utils.functionService.getStringArray;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserHobbiesService userHobbiesService;


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
                        put(PHOTO, dto.getStringParam(PHOTO, null));
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
    public void updateUser(UserSmallDTO dto) {
        if(dto.getEmail() != null) {
            Optional<User> user = userRepository.findByEmail(dto.getEmail());
            user.ifPresent( u -> {
                if(dto.getFirstName() != null) {
                    u.setFirstName(dto.getFirstName());
                }
                if(dto.getLastName() != null) {
                    u.setLastName(dto.getLastName());
                }
                if(dto.getPassword() != null) {
                    u.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
                }
                if(dto.hasParam(HOBBIES)) {
                    List<String> actualHobbies = getStringArray(dto.getParam(HOBBIES));
                    userHobbiesService.addNewHobbiesToUser(u, actualHobbies);
                }
                if(dto.hasParam(PHOTO)) {
                    u.addParam(PHOTO, dto.getStringParam(PHOTO,""));
                }
            });
        }
    }
}
