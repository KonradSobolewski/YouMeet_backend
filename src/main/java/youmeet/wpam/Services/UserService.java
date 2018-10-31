package youmeet.wpam.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import youmeet.wpam.DTO.Role;
import youmeet.wpam.DTO.SmallDTO.UserSmallDTO;
import youmeet.wpam.Repository.UserRepository;
import youmeet.wpam.DTO.User;
import youmeet.wpam.exceptions.UserNotFoundException;
import youmeet.wpam.DTO.UserSecured;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static youmeet.wpam.config.UtilsKeys.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

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
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
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
                }}
        );

        User returnedUser = saveUser(user);
        Set<Role> roles = new HashSet<Role>(){{
            add(new Role("USER", returnedUser));
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
}
