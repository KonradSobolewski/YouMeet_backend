package youmeet.wpam.config.JWTConfig;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security
        .authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class TokenAuthenticationService {
    private static final long EXPIRATIONTIME = 864_000_000; // 10 days
    private static final String SECRET = "ThisIsASecret";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    static void addAuthentication(HttpServletResponse res, String username) {
        String JWT = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        res.addHeader(TOKEN_PREFIX, JWT);
    }

   public static Authentication getAuthentication(HttpServletRequest request)  {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String login = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();
                if(auth!=null) {
                    return login != null ?
                            new UsernamePasswordAuthenticationToken(login, null, auth.getAuthorities()) :
                            null;
                }

        }
        return null;
    }
}