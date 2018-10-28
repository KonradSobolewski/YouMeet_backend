package youmeet.wpam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("youmeet.wpam.*")
public class WpamApplication {

    public static void main(String[] args) {
        SpringApplication.run(WpamApplication.class, args);
    }
}
