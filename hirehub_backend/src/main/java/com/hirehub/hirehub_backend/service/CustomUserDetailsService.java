package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.config.UserDetailsImpl;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isEmpty())
        {
               throw  new UsernameNotFoundException("User not found with email"+email);
        }
        return new UserDetailsImpl(user.get());


    }
}
