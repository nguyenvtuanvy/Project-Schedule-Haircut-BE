package com.example.projectschedulehaircutserver.service.order;

import com.example.projectschedulehaircutserver.dto.OrderDTO;
import com.example.projectschedulehaircutserver.entity.*;
import com.example.projectschedulehaircutserver.exeption.LoginException;
import com.example.projectschedulehaircutserver.exeption.OrderException;
import com.example.projectschedulehaircutserver.repository.*;
import com.example.projectschedulehaircutserver.request.ActionOrderByCustomerRequest;
import com.example.projectschedulehaircutserver.request.AllOrderEmployeeAndDateRequest;
import com.example.projectschedulehaircutserver.request.OrderScheduleHaircutRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final EmployeeRepo employeeRepo;
    private final ComboRepo comboRepo;
    private final ServiceRepo serviceRepo;
    private final CouponsRepo couponsRepo;
    private final CartItemRepo cartItemRepo;
    private final CartRepo cartRepo;

    @Override
    @Transactional
    public String bookingScheduleHaircut(OrderScheduleHaircutRequest request) throws LoginException, OrderException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)){
            try {
                Customer customer = (Customer) authentication.getPrincipal();
                Set<Employee> employees = new HashSet<>();
                request.getEmployeeId().forEach(eid -> {
                    employees.add(employeeRepo.findById(eid).orElseThrow());
                });


                // Kiểm tra lịch trùng
                for (Employee employee : employees) {
                    List<Orders> orders = orderRepo.findAllOrderBooking(employee.getId());
                    if (!orders.isEmpty()) {
                        for (Orders o : orders) {
                            if (o.getOrderDate().equals(request.getOrderDate()) &&
                                    isTimeBetween(request.getOrderStartTime(), o.getOrderStartTime(), o.getOrderEndTime())) {
                                throw new OrderException(
                                        "Nhân viên bạn muốn đặt lịch hiện đang có lịch trùng với lịch hẹn của bạn, vui lòng chọn ngày khác hoặc thay đổi giờ",
                                        "SCHEDULE_CONFLICT"
                                );
                            }
                        }
                    }
                }

                Coupons coupons = couponsRepo.findCouponByCustomerId(customer.getId()).orElse(null);

                BigDecimal discount = BigDecimal.ZERO;
                if (coupons != null && coupons.getDiscount() != null) {
                    discount = request.getTotalPrice()
                            .multiply(BigDecimal.valueOf(coupons.getDiscount()));
                }
                BigDecimal totalOrder = request.getTotalPrice().subtract(discount);


                Orders orders = Orders.builder()
                        .orderDate(request.getOrderDate())
                        .orderStartTime(request.getOrderStartTime())
                        .orderEndTime(request.getOrderStartTime().plusMinutes(request.getHaircutTime()))
                        .status(0)
                        .haircutTime(request.getHaircutTime())
                        .totalPrice(totalOrder)
                        .customer(customer)
                        .employees(employees)
                        .build();

                Orders saveOrder = orderRepo.save(orders);

                Combo combo = comboRepo.findComboById(request.getComboId());

                // add combo
                if (combo != null && !employees.isEmpty()){
                    OrderItem orderItem = OrderItem.builder()
                            .orders(saveOrder)
                            .combo(combo)
                            .service(null)
                            .price(combo.getPrice())
                            .build();

                    orderItemRepo.save(orderItem);
                }

                Set<com.example.projectschedulehaircutserver.entity.Service> services = new HashSet<>();

                request.getServiceId().forEach(sid -> {
                    services.add(serviceRepo.findById(sid).orElseThrow());
                });

                if (combo != null){
                    for (com.example.projectschedulehaircutserver.entity.Service service : services) {
                        if (combo.getServices().stream().anyMatch(check -> Objects.equals(check.getId(), service.getId()))) {
                            throw new OrderException(
                                    "Dịch vụ đã có trong combo, vui lòng thêm một dịch vụ khác ngoài combo đã chọn",
                                    "DUPLICATE_SERVICE"
                            );
                        }
                    }
                }

                // add service
                if (!services.isEmpty() && !employees.isEmpty()){
                    services.forEach(service -> {
                        OrderItem orderItem = OrderItem.builder()
                                .orders(saveOrder)
                                .combo(null)
                                .service(service)
                                .price(service.getPrice())
                                .build();

                        orderItemRepo.save(orderItem);
                    });
                }

                Cart cart = cartRepo.findCartByCustomerId(customer.getId()).orElseThrow();

                cartItemRepo.clearCartItemsByCartId(cart.getId());

                return "Đặt Lịch Cắt Tóc Thành Công !!!";
            } catch (OrderException e) {
                throw e; // Re-throw để GlobalExceptionHandler xử lý
            } catch (Exception e) {
                throw new RuntimeException("Lỗi hệ thống khi đặt lịch: " + e.getMessage(), e);
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public Set<OrderDTO> showOrderByCustomerStatus_0() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Customer customer = (Customer) authentication.getPrincipal();
                return showOrderByCustomerStatus(customer.getId(), 0);
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public Set<OrderDTO> showOrderByCustomerStatus_1() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Customer customer = (Customer) authentication.getPrincipal();
                return showOrderByCustomerStatus(customer.getId(), 1);
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public Set<OrderDTO> showOrderByCustomerStatus_2() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Customer customer = (Customer) authentication.getPrincipal();
                return showOrderByCustomerStatus(customer.getId(), 2);
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    @Transactional
    public void updateBookingStatus(Integer bookingId, Integer status) {
        Orders orders = orderRepo.findOrderByOrderId(bookingId).orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        orders.setStatus(status);
        orderRepo.save(orders);
    }

    @Override
    public Set<OrderDTO> findAllOrderByEmployeeAndDate(AllOrderEmployeeAndDateRequest request) throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
//                Employee employee = (Employee) authentication.getPrincipal();
//                return orderRepo.findAllOrderByEmployeeAndDate(employee.getId(), 1, request.getOrderDate())
//                        .stream()
//                        .map(OrderDTO::new)
//                        .collect(Collectors.toSet());

                return null;
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Transactional
    @Override
    public String ConfirmDoneOrCancelOrderedByCustomer(ActionOrderByCustomerRequest request) throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Customer customer = (Customer) authentication.getPrincipal();

                orderRepo.updateOrdersByStatus(request.getOrderId(), customer.getId(), request.getStatus());

                return "Thành Công";
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    private Set<OrderDTO> convertToOrderDTO(List<Object[]> rawOrders) {
        Map<String, OrderDTO> groupedOrders = new LinkedHashMap<>();

        for (Object[] row : rawOrders) {
            Integer id = (Integer) row[0];
            Date orderDate = (Date) row[1];
            java.sql.Time orderStartTime = (java.sql.Time) row[2];
            java.sql.Time orderEndTime = (java.sql.Time) row[3];
            String employeeName = (String) row[4];
            String comboName = (String) row[5];
            String serviceName = (String) row[6];
            BigDecimal totalPrice = (BigDecimal) row[7];
            Integer status = (Integer) row[8];

            String key = id + "-" + orderDate + "-" + orderStartTime + "-" + orderEndTime;

            OrderDTO dto = groupedOrders.getOrDefault(key,
                    OrderDTO.builder()
                            .id(id)
                            .orderDate(orderDate)
                            .orderStartTime(orderStartTime)
                            .orderEndTime(orderEndTime)
                            .employeeName(new ArrayList<>())
                            .serviceName(new ArrayList<>())
                            .totalPrice(totalPrice)
                            .status(status)
                            .build()
            );

            if (employeeName != null && !dto.getEmployeeName().contains(employeeName)) {
                dto.getEmployeeName().add(employeeName);
            }

            String finalService = comboName != null ? comboName : serviceName;
            if (finalService != null && !dto.getServiceName().contains(finalService)) {
                dto.getServiceName().add(finalService);
            }

            groupedOrders.put(key, dto);
        }

        return new LinkedHashSet<>(groupedOrders.values());
    }

    public Set<OrderDTO> showOrderByCustomerStatus(Integer customerId, Integer status){
        List<Object[]> objects = orderRepo.findOrdersByCustomerId(customerId, status);
        return convertToOrderDTO(objects);
    }


    public boolean isTimeBetween(LocalTime target, LocalTime startTime, LocalTime endTime) {
        if (endTime.isAfter(startTime)) {
            return target.equals(startTime) || target.isAfter(startTime) && target.isBefore(endTime);
        } else {
            return target.isAfter(startTime) || target.isBefore(endTime);
        }
    }


}
