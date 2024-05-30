package ac.su.inclassspringsecurity.config.Jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
@ConfigurationProperties("jwt")
public class JwtProperties {
    // application.properties 에서 설정한 jwt 값을 java 에서 읽어오는 객체(DTO)
    private String issuer;
    private String secretKey;
}