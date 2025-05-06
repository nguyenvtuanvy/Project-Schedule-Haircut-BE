package com.example.projectschedulehaircutserver.controller.employee;

import com.example.projectschedulehaircutserver.exeption.LoginException;
import com.example.projectschedulehaircutserver.response.EmployeeAppointmentByHourResponse;
import com.example.projectschedulehaircutserver.response.EmployeeAppointmentNeedsConfirmationResponse;
import com.example.projectschedulehaircutserver.response.EmployeeBookedStaffResponse;
import com.example.projectschedulehaircutserver.service.employee.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employee")
@AllArgsConstructor
public class HomeEmployeeController {
    private final EmployeeService employeeService;

    // Lấy thống kê booking
    @GetMapping("/booking-stats")
    public ResponseEntity<?> getBookingStats() {
        try {
            EmployeeBookedStaffResponse response = employeeService.getEmployeeBookingStats();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (LoginException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    // Lấy lịch hẹn theo giờ
    @GetMapping("/appointments-by-hour")
    public ResponseEntity<?> getAppointmentsByHour() {
        try {
            List<EmployeeAppointmentByHourResponse> response = employeeService.getAppointmentsByHour();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (LoginException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    // Lấy danh sách cần xác nhận
    @GetMapping("/appointments-confirmation")
    public ResponseEntity<?> getAppointmentsNeedsConfirmation() {
        try {
            List<EmployeeAppointmentNeedsConfirmationResponse> response = employeeService.getAppointmentsNeedsConfirmation();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (LoginException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

}