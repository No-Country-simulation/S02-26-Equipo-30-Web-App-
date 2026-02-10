package com.nc.horseretail.security.currentuser;

import com.nc.horseretail.model.user.User;

public interface CurrentUserProvider {
    User getCurrentUser();
}