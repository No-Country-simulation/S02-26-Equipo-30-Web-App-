package com.nc.horseretail.service;

import com.nc.horseretail.model.user.User;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;

public interface UserService {

//    User getProfile(Authentication auth);

    User getCurrentUser();
}