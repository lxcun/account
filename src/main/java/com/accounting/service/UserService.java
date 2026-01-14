package com.accounting.service;

import com.accounting.dto.UserDTO;
import com.accounting.entity.User;
import com.accounting.mapper.UserMapper;
import com.accounting.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final AuthService authService;

    public void createUser(UserDTO userDTO) {
        User existingUser = userMapper.findByUsername(userDTO.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(authService.encodePassword(userDTO.getPassword()));
        user.setRealName(userDTO.getRealName());
        user.setPhone(userDTO.getPhone());
        user.setEmail(userDTO.getEmail());
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : 1);

        userMapper.insert(user);
    }
}
