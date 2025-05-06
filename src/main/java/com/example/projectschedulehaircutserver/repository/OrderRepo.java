package com.example.projectschedulehaircutserver.repository;

import com.example.projectschedulehaircutserver.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Orders, Integer> {
    @Query(value = "SELECT o.* FROM orders o JOIN order_employee oe ON o.id = oe.order_id WHERE oe.employee_id = :employeeId", nativeQuery = true)
    List<Orders> findAllOrderBooking(@Param("employeeId") Integer employeeId);

    @Query(value = "SELECT " +
            "o.id AS order_id , " +
            "o.order_date, " +
            "o.order_start_time, " +
            "o.order_end_time, " +
            "e.fullname AS employee_fullname, " +
            "c2.name AS combo_name, " +
            "s.name AS service_name, " +
            "o.total_price, " +
            "o.status " +
            "FROM orders o " +
            "JOIN customer c ON c.id = o.customer_id " +
            "JOIN orderitem oi ON o.id = oi.order_id " +
            "JOIN order_employee oe ON o.id = oe.order_id " +
            "JOIN employee e ON oe.employee_id = e.id " +
            "LEFT JOIN service s ON oi.service_id = s.id " +
            "LEFT JOIN combo c2 ON oi.combo_id = c2.id " +
            "WHERE c.id = :customerId AND o.status = :status", nativeQuery = true)
    List<Object[]> findOrdersByCustomerId(@Param("customerId") Integer customerId, @Param("status") Integer status);

    @Query(value = "CALL findAllOrderByEmployeeAndDate(:employeeId, :status, :orderDate)", nativeQuery = true)
    List<Object[]> findAllOrderByEmployeeAndDate(@Param("employeeId") Integer employeeId, @Param("status") Integer status, @Param("orderDate") Date orderDate);

    @Query("select o from Orders o where o.id = :orderId")
    Optional<Orders> findOrderByOrderId(@Param("orderId") Integer orderId);

//    @Query("select o from Orders o where o.customer.id = :customerId and o.status = :status")
//    Optional<Orders> findOrdersByCustomerIdAndStatus(@Param("customerId") Integer customerId, @Param("status") Integer status);

    @Modifying
    @Query("update Orders o set o.status = :status where o.id = :orderId and o.customer.id = :customerId and o.status = 1")
    void updateOrdersByStatus(@Param("orderId") Integer orderId, @Param("customerId") Integer customerId, @Param("status") Integer status);
}
