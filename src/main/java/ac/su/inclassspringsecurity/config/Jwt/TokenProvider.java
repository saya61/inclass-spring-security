package ac.su.inclassspringsecurity.config.Jwt;

import ac.su.inclassspringsecurity.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TokenProvider {
    private final JwtProperties jwtProperties;  // JwtProperties 객체 주입(@Component 는 Bean)

    // JWT 토큰을 외부에 전달
    public String generateToken(User user, Duration expiry) {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + expiry.toMillis());
        return makeToken(now, expiredAt, user);
    }

    // JWT 토큰을 내부에서 생성
    private String makeToken(Date now, Date expiredAt, User user) {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .setSubject(user.getUsername())
                .claim("userId", user.getId())  // 헤더 + 하나로 뭉치는 역할
                // 암호키를 사용해서 시그니처를 작성
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    // JWT 토근을 외부에서 수신 후 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())    // 암호키를 알고있는 서버가 토큰을 해석한다.
                    .parseClaimsJws(token);   // String 처리할때는 parseClaimsJws, 이 상태에서 유효한지 나옴
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // JWT 토큰을 외부에서 수신 후 TOKEN 소유자 조회
    public Authentication getAuthentication(String token) {
        // 토근 정보를 토앻 유저 인증 정보 확인
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        return new UsernamePasswordAuthenticationToken(
                claims.getSubject(),    // 유저 정보 조회
                token,
                authorities
        );
    }

    private Claims getClaims(String token) {    // claims : 토큰에 담긴 정보(포장하다)
        // 토근 기반 클레임 데이터 해독
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}