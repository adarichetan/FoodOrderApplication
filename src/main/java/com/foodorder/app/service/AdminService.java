package com.foodorder.app.service;

import com.foodorder.app.utility.Response;

public interface AdminService {
    Response grantAccess(String name);

    Response fetchAllUsers() ;

    Response setLoginStatus(String email);

    Response logoutUser(String email);
}
