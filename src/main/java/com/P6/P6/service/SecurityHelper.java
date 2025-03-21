package com.P6.P6.service;

import com.P6.P6.model.UserEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityHelper {


    public static boolean isConnected(){
        return SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null
                || SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null
                || !(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserEntity);
    }
    /**
     * get the user currently connected
     * @return user currently signed-in
     */
    public static UserEntity getConnectedUser()
    {
        if(isConnected()) {
            throw new BadCredentialsException("User not connected");
        }

        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
