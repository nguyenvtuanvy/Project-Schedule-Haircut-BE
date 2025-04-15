package com.example.projectschedulehaircutserver.service.customer;

import com.example.projectschedulehaircutserver.dto.CustomerDTO;
import com.example.projectschedulehaircutserver.exeption.CustomerException;

public interface CustomerService {
    void createCustomer(CustomerDTO customerDTO);

    CustomerDTO getInformationCustomer(String username) throws CustomerException;
}
