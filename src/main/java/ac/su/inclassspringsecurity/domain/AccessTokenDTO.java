package ac.su.inclassspringsecurity.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class AccessTokenDTO {
    private String accessToken;
    private String tokenType;   // Bearer = 가지고 있는
}