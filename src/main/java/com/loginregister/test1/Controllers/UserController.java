package com.loginregister.test1.Controllers;


import com.loginregister.test1.Controllers.dto.UserDTO;
import com.loginregister.test1.Controllers.dto.UserListDTO;
import com.loginregister.test1.entities.UserEntity;
import com.loginregister.test1.repository.UserRepository;
import com.loginregister.test1.services.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repository;


    @GetMapping("/all")
    public UserListDTO allUsers() {
        List<UserEntity> users = repository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(user.getEmail(), user.getUsername()))
                .collect(Collectors.toList());

        return new UserListDTO(userDTOs);
    }

}
