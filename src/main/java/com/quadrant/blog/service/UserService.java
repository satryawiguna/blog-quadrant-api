package com.quadrant.blog.service;

import com.quadrant.blog.entity.UserEntity;
import com.quadrant.blog.exception.ResourceNotFoundException;
import com.quadrant.blog.repository.RoleRepository;
import com.quadrant.blog.repository.UserRepository;
import com.quadrant.blog.util.JwtTokenUtil;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity getUserEntity(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User", "Id", id));
    }
}
