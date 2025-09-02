package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email) throws Exception;

     Optional<User> findUserByJwt(String jwt) ;

}
