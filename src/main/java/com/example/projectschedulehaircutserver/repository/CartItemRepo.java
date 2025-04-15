package com.example.projectschedulehaircutserver.repository;

import com.example.projectschedulehaircutserver.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartItemRepo extends JpaRepository<CartItem, Integer> {
    @Query("select ci from CartItem ci where ci.combo.id = :comboId and ci.cart.id = :cartId")
    Optional<CartItem> findCartItemByComboIdAndCartId(@Param("comboId") Integer comboId, @Param("cartId") Integer cartId);

    @Query("select ci from CartItem ci where ci.service.id = :serviceId and ci.cart.id = :cartId")
    Optional<CartItem> findCartItemByServiceIdAndCartId(@Param("serviceId") Integer serviceId, @Param("cartId") Integer cartId);
}
