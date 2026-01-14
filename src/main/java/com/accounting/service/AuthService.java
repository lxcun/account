package com.accounting.service;

import com.accounting.dto.LoginRequest;
import com.accounting.dto.LoginResponse;
import com.accounting.entity.Role;
import com.accounting.mapper.RoleMapper;
import com.accounting.security.CustomUserDetails;
import com.accounting.security.CustomUserDetailsService;
import com.accounting.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        System.out.println("尝试登录: " + request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtUtil.generateToken(userDetails.getId(), userDetails.getUsername(), userDetails.getRoleCode());

        Role role = roleMapper.findRolesByUserId(userDetails.getId()).get(0);

        System.out.println("登录成功: " + userDetails.getUsername() + ", 角色: " + role.getRoleName());

        return new LoginResponse(
                token,
                userDetails.getUsername(),
                userDetails.getUsername(),
                role.getRoleName()
        );
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
