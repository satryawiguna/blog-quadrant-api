package com.quadrant.blog.controller;

import com.quadrant.blog.dto.BaseDataResponse;
import com.quadrant.blog.dto.auth.*;
import com.quadrant.blog.entity.RefreshTokenEntity;
import com.quadrant.blog.entity.RoleEntity;
import com.quadrant.blog.entity.UserEntity;
import com.quadrant.blog.exception.RefreshTokenException;
import com.quadrant.blog.service.AuthService;
import com.quadrant.blog.service.RefreshTokenService;
import com.quadrant.blog.util.JwtTokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final ModelMapper modelMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final HttpServletResponse httpServletResponse;

    public AuthController(AuthService authService,
                          RefreshTokenService refreshTokenService,
                          ModelMapper modelMapper,
                          JwtTokenUtil jwtTokenUtil,
                          HttpServletResponse httpServletResponse) {
        this.modelMapper = modelMapper;
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.httpServletResponse = httpServletResponse;
    }

    @PostMapping("/login")
    public ResponseEntity<BaseDataResponse<?>> loginUser(@Valid @RequestBody LoginDataRequest request, Errors errors) {
        BaseDataResponse<Map<String, Object>> response =  new BaseDataResponse<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()) {
                response.getMessages().add(error.getDefaultMessage());
            }

            response.setStatus("ERROR");
            response.setCode(HttpStatus.BAD_REQUEST);
            response.setPayload(null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        BaseDataResponse<Map<String, Object>> loginResponse = authService.login(request.getEmail(), request.getPassword(), response);

        return ResponseEntity.status(loginResponse.getCode()).body(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<BaseDataResponse<?>> registerUser(@Valid @RequestBody RegisterDataRequest request, Errors errors) {

        BaseDataResponse<RegisterDataResponse> response =  new BaseDataResponse<>();

        if (errors.hasErrors()) {
            for (ObjectError error : errors.getAllErrors()) {
                response.getMessages().add(error.getDefaultMessage());
            }

            response.setStatus("ERROR");
            response.setCode(HttpStatus.BAD_REQUEST);
            response.setPayload(null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            response.getMessages().add("Confirm password should be same with password");
            response.setStatus("ERROR");
            response.setCode(HttpStatus.BAD_REQUEST);
            response.setPayload(null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        UserEntity user = modelMapper.map(request, UserEntity.class);
        RoleEntity role = authService.getRoleAsUser();

        user.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        user.setRole(role);

        BaseDataResponse<RegisterDataResponse> registerResponse = authService.register(user, response);

        return ResponseEntity.status(registerResponse.getCode()).body(registerResponse);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshtoken(@CookieValue(name = "refresh_token", defaultValue = "") String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    String accessToken = jwtTokenUtil.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new RefreshTokenDataResponse(accessToken, refreshToken));
                })
                .orElseThrow(() -> new RefreshTokenException(refreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@CookieValue(name = "refresh_token", defaultValue = "") String refreshToken) {
        refreshTokenService.deleteByRefreshToken(refreshToken);

        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setSecure(false);
        cookie.setHttpOnly(true);

        httpServletResponse.addCookie(cookie);

        return ResponseEntity.ok("Log out successful!");
    }
}
