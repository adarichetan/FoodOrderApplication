package com.foodorder.app.service;


import com.foodorder.app.utility.Response;

public interface AdminService {
    Response grantAccess(String name);

    Response fetchAllCustomers();

    Response fetchAllUsers() ;

}
