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
    @Query(value = "CALL findAllBookingByEmployeeId(:employeeId)", nativeQuery = true)
    List<Orders> findAllOrderBooking(@Param("employeeId") Integer employeeId);

    @Query(value = "CALL findOrderByCustomerId(:customerId, :status)", nativeQuery = true)
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
