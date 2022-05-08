package com.marcoantonio.dscatalog.dtos;

import com.marcoantonio.dscatalog.services.validation.UserInsertValid;

@UserInsertValid
public class UserInsertDTO extends UserDTO{
    private static final long serialVersionUID = 1L; 

    private String password;

    UserInsertDTO(){
        super();
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
