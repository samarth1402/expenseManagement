package com.adobe.prj.service;

import com.adobe.prj.dao.UserDao;
import com.adobe.prj.dto.ResetPasswordDTO;
import com.adobe.prj.entity.User;
import com.adobe.prj.exception.DuplicateEntityException;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.helper.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSpecification userSpecification;

    public User getUserByEmailId(String emailId) throws EntityNotFoundException {
       Optional<User> userOptional = userDao.findById(emailId);
       if(userOptional.isPresent()) {
           return userOptional.get();
       }    else{
           throw new EntityNotFoundException("No User found with given emailId");
       }
    }

    public User getUserByName(String name) {
        List<User> userList = userDao.findByName(name);
        if(userList.size()==1){
            return userList.get(0);
        }
        return null;
    }

    public User addUser(User u) throws DuplicateEntityException {
        Optional<User> userOptional = userDao.findById(u.getEmailId());
        if(userOptional.isPresent()){
            throw new DuplicateEntityException("Given user EmailId already exists");
        }
        return userDao.save(u);
    }

    public List<User> getUserList(Boolean isManager) {
        Specification<User> specification = where(userSpecification.isDeletedQuery(false));
        if (!Objects.isNull(isManager))     specification = specification.and(userSpecification.isManagerQuery(isManager));

        return userDao.findAll(specification);
    }

    public void updateNewUser(User user){
        user.setNewUser(false);
        userDao.save(user);
    }

    public void updateUser(ResetPasswordDTO passwordDTO) throws EntityNotFoundException
    {
        User user = getUserByEmailId(passwordDTO.getEmailId());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())){
            String encodedNewPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
            user.setPassword(encodedNewPassword);
            userDao.save(user);
        }
        else{
            throw new EntityNotFoundException("Invalid Credentials");
        }
    }
}
