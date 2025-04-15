package com.example.projectschedulehaircutserver.service.cart;

import com.example.projectschedulehaircutserver.entity.Cart;
import com.example.projectschedulehaircutserver.entity.CartItem;
import com.example.projectschedulehaircutserver.entity.Combo;
import com.example.projectschedulehaircutserver.entity.Customer;
import com.example.projectschedulehaircutserver.exeption.CartItemException;
import com.example.projectschedulehaircutserver.exeption.ComboException;
import com.example.projectschedulehaircutserver.exeption.LoginException;
import com.example.projectschedulehaircutserver.repository.*;
import com.example.projectschedulehaircutserver.request.AddComboInCartItemRequest;
import com.example.projectschedulehaircutserver.request.AddServiceInCartItemRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService{
    private final CartRepo cartRepo;
    private final CartItemRepo cartItemRepo;
    private final ComboRepo comboRepo;
    private final ServiceRepo serviceRepo;


    @Override
    public String addCartItemInCartTypeCombo(AddComboInCartItemRequest request) throws CartItemException, ComboException, LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)){
            Customer customer = (Customer) authentication.getPrincipal();
            Combo combo = comboRepo.findComboById(request.getComboId());
            Cart cart = cartRepo.findCartByCustomerId(customer.getId()).orElseThrow();
            if (combo.getId() != null && cart.getId() != null){
                Optional<CartItem> isCartItem = cartItemRepo.findCartItemByComboIdAndCartId(request.getComboId(), cart.getId());
                try {
                    if (isCartItem.isPresent()){
                        throw new CartItemException("Bạn Đã Thêm Combo Này Vào Giỏ Hàng");
                    } else {
                        var cartItem = CartItem.builder()
                                .cart(cart)
                                .combo(combo)
                                .service(null)
                                .price(combo.getPrice())
                                .build();
                        cartItemRepo.save(cartItem);
                    }
                    return "Thêm Combo Vào Giỏ Hàng Thành Công";
                } catch (Exception e){
                    throw new CartItemException("Không Thể Thêm Combo Vào Giỏ Hàng");
                }
            } else {
                throw new ComboException("Không Tìm Thấy Combo");
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public String addCartItemInCartTypeService(AddServiceInCartItemRequest request) throws CartItemException, ComboException, LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)){
            Customer customer = (Customer) authentication.getPrincipal();
            com.example.projectschedulehaircutserver.entity.Service service = serviceRepo.findServiceById(request.getServiceId()).orElseThrow();
            Cart cart = cartRepo.findCartByCustomerId(customer.getId()).orElseThrow();
            if (service.getId() != null && cart.getId() != null){
                Optional<CartItem> isCartItem = cartItemRepo.findCartItemByServiceIdAndCartId(request.getServiceId(), cart.getId());
                try {
                    if (isCartItem.isPresent()){
                        throw new CartItemException("Bạn Đã Thêm Dịch Vụ Này Vào Giỏ Hàng");
                    } else {
                        var cartItem = CartItem.builder()
                                .cart(cart)
                                .combo(null)
                                .service(service)
                                .price(service.getPrice())
                                .build();
                        cartItemRepo.save(cartItem);
                    }
                    return "Thêm Dịch Vụ Vào Giỏ Hàng Thành Công";
                } catch (Exception e){
                    throw new CartItemException("Không Thể Thêm Dịch Vụ Vào Giỏ Hàng");
                }
            } else {
                throw new ComboException("Không Tìm Thấy Dịch Vụ");
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }


}
