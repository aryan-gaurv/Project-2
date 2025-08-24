package com.example.authify.service;

import com.example.authify.entity.UserEntity;
import com.example.authify.repository.UserRepostory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    final UserRepostory userRepostory;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity emailNotFound = userRepostory.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        return new User(emailNotFound.getEmail(),emailNotFound.getPassword(),new ArrayList<>());
    }
}
