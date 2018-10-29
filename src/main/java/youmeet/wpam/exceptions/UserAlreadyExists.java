package youmeet.wpam.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User already exists")
public class UserAlreadyExists extends Exception {
    public UserAlreadyExists() {
        super("User already exists");
    }

    public UserAlreadyExists(String message) {
        super(message);
    }
}
