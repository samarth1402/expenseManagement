package com.adobe.prj.dao;

import com.adobe.prj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, String> , JpaSpecificationExecutor<User> {
    List<User> findByName(String name);

    @Query("SELECT u FROM User u WHERE u.emailId = ?1")
    public User findByEmail(String email);
}
