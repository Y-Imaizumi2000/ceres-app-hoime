package com.ceres.hoime.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ceres.hoime.repository.UserRepository;
import com.ceres.hoime.service.CustomUserDetailsService;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Security のログイン認証で利用する UserDetailsService 実装。
 *
 * <p>ビジネス層命名規則: 実装クラスは末尾に ServiceImpl。</p>
 */
@Slf4j
@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("【CustomUserDetailsService】loadUserByUsername email={}", email);

        com.ceres.hoime.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.debug("【CustomUserDetailsService】user not found email={}", email);
                    return new UsernameNotFoundException("ユーザーが見つかりません: " + email);
                });

        String role = user.getRole() == null ? "USER" : user.getRole().toUpperCase();

        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
