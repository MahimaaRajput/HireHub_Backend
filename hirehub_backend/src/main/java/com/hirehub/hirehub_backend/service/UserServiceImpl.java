package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;
    @Override
    public Optional<User> findByEmail(String email) throws Exception {
       Optional<User> user= userRepository.findByEmail(email);
       if (user.isEmpty())
       {
           throw new Exception("User not found with this email");
       }
       return user;
    }
    @Override
    public Optional<User> findUserByJwt(String jwt) {
        String email = JwtProvider.getEmailFromToken(jwt);
        Optional<User> user = userRepository.findByEmail(email);
        return user;
    }
}
