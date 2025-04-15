package com.example.projectschedulehaircutserver.repository;

import com.example.projectschedulehaircutserver.dto.EmployeeDTO;
import com.example.projectschedulehaircutserver.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface EmployeeRepo extends JpaRepository<Employee, Integer> {
    Optional<Employee> findEmployeeByAccount_UserName(String account_userName);

    @Query("select new com.example.projectschedulehaircutserver.dto.EmployeeDTO(e.id, e.userName, e.fullName, e.age, e.address, e.phone, e.avatar) from Employee e where e.isDeleted = false")
    Set<EmployeeDTO> findAllEmployee();

    @Query(value = "CALL totalPriceByEmployeeAndDay(:employeeId, :day)", nativeQuery = true)
    Object[] totalPriceByEmployeeAndDay(@Param("employeeId") Integer employeeId,@Param("day") Integer day);
}
