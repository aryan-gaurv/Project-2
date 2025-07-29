package com.example.CRUD.backend.expection;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException (Long id){
        super("Could Not found the user "+id);
    }
}
