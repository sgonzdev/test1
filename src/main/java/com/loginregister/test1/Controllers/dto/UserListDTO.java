package com.loginregister.test1.Controllers.dto;

import java.util.List;

public class UserListDTO {
    private List<UserDTO> users;

    public UserListDTO(List<UserDTO> users) {
        this.users = users;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }
}
