package ac.su.inclassspringsecurity.controller;

import ac.su.inclassspringsecurity.constant.UserRole;
import ac.su.inclassspringsecurity.domain.User;
import ac.su.inclassspringsecurity.domain.UserCreateForm;
import ac.su.inclassspringsecurity.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

// SPRING SECURITY 가 제공하는 인증, 권한 관련 객체를 가져오기 위한 임포트 문
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signup(
            UserCreateForm userCreateForm
    ) {
        return "signup_form";
    }
    @PostMapping("/signup")
    public String createUser(
            @Valid UserCreateForm userCreateForm,
            BindingResult bindingResult
    ) {
        // form 데이터 검증
        // 1-1. 입력값 바인딩 검사
        if(bindingResult.hasErrors()) {
            return "signup_form";
        }
        // 1-2. 입력값 내용 검사
        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue(
                    "password2",
                    "passwordDoubleCheckError",
                    "패스워드 확인 값이 일치하지 않습니다."
            );
            return "signup_form";
        }

        // 2. 백엔드 Validation
        try {
            userService.create(
                    userCreateForm.getUsername(),
                    userCreateForm.getPassword1(),
                    userCreateForm.getEmail()
            );
        } catch (IllegalStateException e) {
            bindingResult.reject(
                    "signup failed",
                    "이미 등록된 사용자"
            );
            return "signup_form";
        } catch (Exception e) {
            bindingResult.reject(
                    "signup failed",
                    e.getMessage()
            );
            return "signup_form";
        }

        // 3. 회원 가입 성공시
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(User user) {
//        // 로그인 정보가 정확한 경우 Token 발급
//        AccessTokenDTO accessToken = userService.getAccessToken(user);
//        if (accessToken == null) {
//            return ResponseEntity.badRequest().body("로그인 실패");
//        }
//        return ResponseEntity.ok(accessToken);
//    }

    @GetMapping("/make-dummy")
    public ResponseEntity<List<User>> makeDummyData(
            @RequestParam(value = "count", required = false, defaultValue = "20") int count) {
        // count 값이 1 이상 100 이하가 되도록 제약조건 추가하기!
        if (count < 1 || count > 100) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
        List<User> users = userService.makeDummyData(count);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getUsers(){
        Authentication userAuth = SecurityContextHolder.getContext().getAuthentication();
        String roleStr = userAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        UserRole role = UserRole.valueOf(
                roleStr.replace("ROLE_", "")
        );

        if (role == UserRole.USER) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        // 호출한 유저의 Role 에 따라서 적절한 대상 유저 목록을 검색
        List<User> users = userService.getUserByRole(role);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
