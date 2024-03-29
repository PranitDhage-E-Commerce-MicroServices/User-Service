package com.user.models.response;

import com.user.pojo.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Integer id;

    private String name;

    private String phone;

    private String email;

    private Integer status;

    private Role role;

    private Date date;

}
