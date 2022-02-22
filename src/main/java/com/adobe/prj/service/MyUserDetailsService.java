package com.adobe.prj.service;

import com.adobe.prj.dao.UserDao;
import com.adobe.prj.entity.MyUserDetails;
import com.adobe.prj.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(userDao.findByEmail(emailId));
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new MyUserDetails(user.get());
    }
}
