package com.cts.auth_service.services;

import com.cts.auth_service.models.UserPrinciple;
import com.cts.auth_service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserPrinciple(userRepo.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException("user not found")));
    }
}
