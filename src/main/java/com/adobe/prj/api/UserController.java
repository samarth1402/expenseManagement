package com.adobe.prj.api;

import com.adobe.prj.dto.ResetPasswordDTO;
import com.adobe.prj.entity.User;
import com.adobe.prj.exception.DuplicateEntityException;
import com.adobe.prj.exception.EntityNotFoundException;
import com.adobe.prj.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(value = "User controller")
@RequestMapping("api/user")
public class UserController {

    @Autowired
    UserService service;

    @ApiOperation(value = "Get user by email id")
    @GetMapping("/{emailId}")
    public @ResponseBody
    User getUserByEmailId(@PathVariable("emailId") String emailId) throws EntityNotFoundException {
        return service.getUserByEmailId(emailId);
    }

    @ApiOperation(value = "Add a new user")
    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public @ResponseBody User addUser(@RequestBody @Valid User u) throws DuplicateEntityException {
        return service.addUser(u);
    }

    @ApiOperation(value = "Get user lists")
    @GetMapping("/list")
    public @ResponseBody List<User> getUserList(@RequestParam(value = "isManager", required = false) Boolean isManager) {
        return service.getUserList(isManager);
    }

    @ApiOperation(value = "Modify user")
    @PutMapping()
    public void modifyUser(@RequestBody ResetPasswordDTO passwordDTO) throws EntityNotFoundException {
       service.updateUser(passwordDTO);
    }

}
