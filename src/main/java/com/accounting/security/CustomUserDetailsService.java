package com.accounting.security;

import com.accounting.entity.Role;
import com.accounting.entity.User;
import com.accounting.mapper.RoleMapper;
import com.accounting.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        Role role = roleMapper.findRolesByUserId(user.getId()).stream()
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("用户未分配角色"));

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .roleCode(role.getRoleCode())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode())))
                .build();
    }
}
