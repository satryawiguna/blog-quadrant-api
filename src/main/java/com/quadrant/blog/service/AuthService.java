package com.quadrant.blog.service;

import com.quadrant.blog.dto.BaseDataResponse;
import com.quadrant.blog.dto.auth.RegisterDataResponse;
import com.quadrant.blog.entity.RefreshTokenEntity;
import com.quadrant.blog.entity.RoleEntity;
import com.quadrant.blog.entity.UserEntity;
import com.quadrant.blog.repository.RoleRepository;
import com.quadrant.blog.repository.UserRepository;
import com.quadrant.blog.util.JwtTokenUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper modelMapper;
    private final HttpServletResponse httpServletResponse;

    private final Log logger = LogFactory.getLog(getClass());

    public AuthService(ModelMapper modelMapper,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       AuthenticationManager authenticationManager,
                       JwtUserDetailsService userDetailsService,
                       RefreshTokenService refreshTokenService,
                       JwtTokenUtil jwtTokenUtil,
                       HttpServletResponse httpServletResponse) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.httpServletResponse = httpServletResponse;
    }

    public BaseDataResponse<Map<String, Object>> login(String email, String password, BaseDataResponse<Map<String, Object>> response) {
        Optional<UserEntity> user = userRepository.findUserByEmail(email);

        if (user.isPresent()) {
            try {
                Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

                if (auth.isAuthenticated()) {
                    logger.info("Logged in");

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    Map<String, Object> generateTokenResponse = new HashMap<>();
                    generateTokenResponse.put("id", user.get().getId());
                    generateTokenResponse.put("access_token", jwtTokenUtil.generateToken(userDetails));

                    RefreshTokenEntity createRefreshTokenResponse = refreshTokenService.createRefreshToken(user.get().getId());
                    generateTokenResponse.put("refresh_token", createRefreshTokenResponse.getToken());

                    // Set cookie Http Only
                    Cookie cookie = new Cookie("refresh_token", createRefreshTokenResponse.getToken());
                    cookie.setMaxAge(86400000);
                    cookie.setSecure(false);
                    cookie.setHttpOnly(true);

                    httpServletResponse.addCookie(cookie);

                    response.getMessages().add("Logged in");
                    response.setStatus("SUCCESS");
                    response.setCode(HttpStatus.OK);
                    response.setPayload(generateTokenResponse);
                }
            } catch (DisabledException e) {
                e.printStackTrace();

                logger.info(e.getMessage());

                response.getMessages().add("User is disabled");
                response.setStatus("ERROR");
                response.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setPayload(null);

            } catch (BadCredentialsException e) {
                logger.info(e.getMessage());

                response.getMessages().add("Invalid Credentials");
                response.setStatus("ERROR");
                response.setCode(HttpStatus.UNAUTHORIZED);
                response.setPayload(null);

            } catch (Exception e) {
                e.printStackTrace();

                logger.info(e.getMessage());

                response.getMessages().add("Something went wrong");
                response.setStatus("ERROR");
                response.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setPayload(null);
            }
        } else {
            logger.info("Login failed");

            response.getMessages().add("Login failed, please check email and password");
            response.setStatus("ERROR");
            response.setCode(HttpStatus.UNAUTHORIZED);
            response.setPayload(null);
        }

        return response;
    }

    public BaseDataResponse<RegisterDataResponse> register(UserEntity userEntity, BaseDataResponse<RegisterDataResponse> response) {

        try {
            UserEntity user = userRepository.save(userEntity);

            RegisterDataResponse register = modelMapper.map(user, RegisterDataResponse.class);

            response.getMessages().add("Register succeed");
            response.setStatus("SUCCESS");
            response.setCode(HttpStatus.OK);
            response.setPayload(register);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            logger.info(e.getMessage());

            response.getMessages().add("Something went wrong");
            response.setStatus("ERROR");
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setPayload(null);

        }

        return response;
    }

    public RoleEntity getRoleAsUser() {
        return roleRepository.findByName("USER");
    }
}
