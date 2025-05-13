package com.example.projectschedulehaircutserver.controller.maneger;

import com.example.projectschedulehaircutserver.exeption.CustomerException;
import com.example.projectschedulehaircutserver.service.account.AccountService;
import com.example.projectschedulehaircutserver.service.customer.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AccountManagementController {
    private final AccountService accountService;

    @GetMapping("/accounts")
    public ResponseEntity<?> getAccountManagement() throws CustomerException {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }
}
