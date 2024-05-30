package ac.su.inclassspringsecurity.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
@Getter @ Setter    // Setter 를 제거하고 immutable 하게 다루기 위한 조치 필요
public class UserCreateForm {   // DTO 클래스 역할을 하는 Form 클래스 - setter 없어도 오류 X
    @Size(min = 3, max = 25)
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;
}