package com.marcoantonio.dscatalog.dtos;

import java.io.Serializable;

import com.marcoantonio.dscatalog.entities.Role;

public class RoleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String authority;

    public RoleDTO() {

    }

    public RoleDTO(long id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public RoleDTO(Role role) {
        id = role.getId();
        authority = role.getAuthority();
    }

    public long getId() {
        return this.id;
    }

    public String getAuthority() {
        return this.authority;
    }
}
