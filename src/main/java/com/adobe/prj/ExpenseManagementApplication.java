package com.adobe.prj;


import com.adobe.prj.dao.UserDao;
import com.adobe.prj.entity.User;
import com.adobe.prj.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@SpringBootApplication
@RestController
@EnableJpaRepositories(basePackageClasses = UserDao.class)
public class ExpenseManagementApplication implements CommandLineRunner{

	@Autowired
	UserDao userDao;

	public static void main(String[] args) {
		SpringApplication.run(ExpenseManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws EntityNotFoundException {
		Optional<User> manager = userDao.findById("ashish@gmail.com");
		if (!manager.isPresent()) {
			User user = User.builder()
					.name("ashish")
					.emailId("ashish@gmail.com")
					.password("$2a$12$diDQyqrPXnzBH1.sGOO4PuL8SMyibqlLerDQOnYqr.MG/LsYPnM6O")
					.isNewUser(true)
					.isManager(true)
					.build();
			userDao.save(user);
		}
		Optional<User> user = userDao.findById("megha@gmail.com");
		if (!user.isPresent()) {
			User user_temp = User.builder()
					.name("megha")
					.emailId("megha@gmail.com")
					.password("$2a$12$diDQyqrPXnzBH1.sGOO4PuL8SMyibqlLerDQOnYqr.MG/LsYPnM6O")
					.isNewUser(true)
					.isManager(false)
					.build();
			userDao.save(user_temp);
		}
	}

	@GetMapping("/user")
	public String greetingUser() {
		return "Welcome User";
	}

	@GetMapping("/manager")
	public String greetingAdmin() {
		return "Welcome Manager";
	}

}
