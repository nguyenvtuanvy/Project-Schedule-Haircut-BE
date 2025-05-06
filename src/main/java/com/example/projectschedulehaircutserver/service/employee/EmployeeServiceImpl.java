package com.example.projectschedulehaircutserver.service.employee;

import com.example.projectschedulehaircutserver.dto.EmployeeDTO;
import com.example.projectschedulehaircutserver.entity.*;
import com.example.projectschedulehaircutserver.exeption.LoginException;
import com.example.projectschedulehaircutserver.repository.AccountRepo;
import com.example.projectschedulehaircutserver.repository.EmployeeRepo;
import com.example.projectschedulehaircutserver.repository.RoleRepo;
import com.example.projectschedulehaircutserver.request.TotalPriceByEmployeeAndDayRequest;
import com.example.projectschedulehaircutserver.response.EmployeeAppointmentByHourResponse;
import com.example.projectschedulehaircutserver.response.EmployeeAppointmentNeedsConfirmationResponse;
import com.example.projectschedulehaircutserver.response.EmployeeBookedStaffResponse;
import com.example.projectschedulehaircutserver.response.TotalPriceByEmployeeAndDayResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{
    private EmployeeRepo employeeRepo;
    private AccountRepo accountRepo;
    private RoleRepo roleRepo;
    private PasswordEncoder encoder;
    @Override
    public void createEmployee(EmployeeDTO employeeDTO) {
        try {
            Role role = roleRepo.findById(1).orElseThrow(() -> new RuntimeException("No roles specified."));

            Account account = Account.builder()
                    .fullName(employeeDTO.getFullName())
                    .userName(employeeDTO.getUserName())
                    .email(employeeDTO.getEmail())
                    .password(encoder.encode(employeeDTO.getPassword()))
                    .age(employeeDTO.getAge())
                    .address(employeeDTO.getAddress())
                    .role(role)
                    .phone(employeeDTO.getPhone())
                    .build();

            Account savedAccount = accountRepo.save(account);

            Employee employee = new Employee();

            employee.setFullName(employeeDTO.getFullName());
            employee.setUserName(employeeDTO.getUserName());
            employee.setEmail(employeeDTO.getEmail());
            employee.setPassword(encoder.encode(employeeDTO.getPassword()));
            employee.setAge(employeeDTO.getAge());
            employee.setAddress(employeeDTO.getAddress());
            employee.setPhone(employeeDTO.getPhone());
            employee.setRole(role);
            employee.setIsDeleted(false);
            employee.setAvatar(employeeDTO.getAvatar());

            if (employeeDTO.getType() == 0){
                employee.setEmployeeType(Employee.EmployeeType.HAIR_STYLIST_STAFF);
            } else {
                employee.setEmployeeType(Employee.EmployeeType.SPA_STAFF);
            }

            employee.setAccount(savedAccount);

            employeeRepo.save(employee);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Set<EmployeeDTO> showAllEmployee() {
        return employeeRepo.findAllEmployee();
    }

    @Override
    public TotalPriceByEmployeeAndDayResponse totalPriceByEmployeeAndDay(TotalPriceByEmployeeAndDayRequest request) throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Employee employee = (Employee) authentication.getPrincipal();

                Object[] objects = employeeRepo.totalPriceByEmployeeAndDay(employee.getId(), request.getDays());

                return new TotalPriceByEmployeeAndDayResponse(objects);
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public EmployeeBookedStaffResponse getEmployeeBookingStats() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Employee employee = (Employee) authentication.getPrincipal();

                return employeeRepo.getEmployeeBookingStats(employee.getId());
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public List<EmployeeAppointmentByHourResponse> getAppointmentsByHour() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Employee employee = (Employee) authentication.getPrincipal();

                return employeeRepo.getAppointmentsByHour(employee.getId());
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    @Override
    public List<EmployeeAppointmentNeedsConfirmationResponse> getAppointmentsNeedsConfirmation() throws LoginException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            try {
                Employee employee = (Employee) authentication.getPrincipal();

                List<Object[]> rawData = employeeRepo.getRawAppointmentsData(employee.getId());

                Map<Integer, EmployeeAppointmentNeedsConfirmationResponse> resultMap = new LinkedHashMap<>();

                for (Object[] row : rawData) {
                    Orders order = (Orders) row[0];
                    String customerName = (String) row[1];
                    String serviceName = (String) row[2];

                    Integer orderId = order.getId();

                    resultMap.computeIfAbsent(orderId, id ->
                            new EmployeeAppointmentNeedsConfirmationResponse(
                                    orderId,
                                    formatTime(order.getOrderStartTime(), order.getOrderEndTime()),
                                    customerName,
                                    new ArrayList<>(),
                                    order.getStatus()
                            )
                    );

                    resultMap.get(orderId).getServices().add(serviceName);
                }

                return new ArrayList<>(resultMap.values());
            } catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new LoginException("Bạn Chưa Đăng Nhập");
        }
    }

    private String formatTime(LocalTime start, LocalTime end) {
        return start.toString() + " - " + end.toString();
    }
}
