package com.foodorder.app.service;


import com.foodorder.app.utility.Response;

public interface AdminService {
    Response grantAccess(int id);

    Response fetchAllCustomers();

    Response fetchAllUsers() ;

}
