package com.example.projectschedulehaircutserver.repository;

import com.example.projectschedulehaircutserver.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Account, Integer> {
}
