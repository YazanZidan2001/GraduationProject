package com.example.GraduationProject;

import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Enums.Role;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;

public class SessionManagement {
    public void validateLoggedInAdmin(User user) throws UserNotFoundException {
        if(user.getRole() != Role.ADMIN){
            throw new UserNotFoundException("You are not authorized to perform this operation");
        }
    }

    public void validateLoggedInDoctor(User user) throws UserNotFoundException {
        if(user.getRole() != Role.DOCTOR){
            throw new UserNotFoundException("You are not authorized to perform this operation");
        }
    }


    public void validateLoggedInPatient(User user) throws UserNotFoundException {
        if(user.getRole() != Role.PATIENT){
            throw new UserNotFoundException("You are not authorized to perform this operation");
        }
    }

    public void validateLoggedInPatientAndDoctor(User user) throws  UserNotFoundException{
        if(user.getRole() != Role.PATIENT&& user.getRole() != Role.DOCTOR){
            throw new UserNotFoundException("You are not authorized to perform this operation");
        }
    }


    public void validateLoggedInDoctorOrAdmin(User user) throws UserNotFoundException {
        if(user.getRole() != Role.ADMIN
                && user.getRole() != Role.DOCTOR){
            throw new UserNotFoundException("You are not authorized to perform this operation");
        }
    }




    public void validateLoggedInCheckInOut(User user) throws UserNotFoundException {
        if(user.getRole() != Role.ADMIN  && user.getRole() != Role.DOCTOR ){
            throw new UserNotFoundException("You are not authorized to perform this operation");
        }
    }
    public void validateLoggedInAllUser(User user) throws UserNotFoundException {
        if(user.getRole() != Role.ADMIN  && user.getRole() != Role.DOCTOR
                && user.getRole() != Role.PATIENT){
            throw new UserNotFoundException("You are not authorized to perform this operation");
        }
    }



}

