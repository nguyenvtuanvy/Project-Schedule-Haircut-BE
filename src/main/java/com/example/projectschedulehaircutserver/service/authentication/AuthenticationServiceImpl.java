package com.example.projectschedulehaircutserver.service.authentication;

import com.example.projectschedulehaircutserver.entity.Account;
import com.example.projectschedulehaircutserver.entity.Cart;
import com.example.projectschedulehaircutserver.entity.Customer;
import com.example.projectschedulehaircutserver.entity.Role;
import com.example.projectschedulehaircutserver.exeption.*;
import com.example.projectschedulehaircutserver.repository.*;
import com.example.projectschedulehaircutserver.request.LoginRequest;
import com.example.projectschedulehaircutserver.request.RefreshTokenRequest;
import com.example.projectschedulehaircutserver.request.RegisterRequest;
import com.example.projectschedulehaircutserver.response.AuthenticationResponse;
import com.example.projectschedulehaircutserver.service.email.EmailService;
import com.example.projectschedulehaircutserver.service.jwt.JwtService;
import com.example.projectschedulehaircutserver.service.redis.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{
    private final CustomerRepo customerRepo;
    private final CartRepo cartRepo;
    private final PasswordEncoder encoder;
    private final RoleRepo roleRepo;
    private final AccountRepo accountRepo;
    private final EmployeeRepo employeeRepo;
    private final ManagerRepo managerRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RedisService redisService;
    private final EmailService emailService;

    @Override
    @Transactional
    public String registerUser(RegisterRequest request) throws RegisterException {
        try {
            Role role = roleRepo.findById(2)
                    .orElseThrow(() -> new RegisterException("Không tìm thấy vai trò mặc định."));

            if (accountRepo.existsByUserName(request.getUserName())) {
                throw new RuntimeException("Tên đăng nhập đã được sử dụng");
            }

            if (accountRepo.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email đã được sử dụng");
            }

            if (accountRepo.existsByPhone(request.getPhone())) {
                throw new RuntimeException("Số điện thoại đã được sử dụng");
            }

            // Tạo customer như một account
            Customer customer = new Customer();
            customer.setFullName(request.getFullName());
            customer.setUserName(request.getUserName());
            customer.setEmail(request.getEmail());
            customer.setAvatar("https://i.postimg.cc/pVs3qTMy/image.png");
            customer.setPassword(encoder.encode(request.getPassword()));
            customer.setRole(role);
            customer.setAge(request.getAge());
            customer.setAddress(request.getAddress());
            customer.setPhone(request.getPhone());
            customer.setIsBlocked(false);

            customerRepo.save(customer);

            // Gán cart cho customer
            Cart cart = Cart.builder()
                    .customer(customer)
                    .build();
            cartRepo.save(cart);

            return "Đăng ký thành công";
        } catch (DataIntegrityViolationException e) {
            throw new RegisterException("Lỗi hệ thống khi đăng ký");
        }
    }



    @Override
    public AuthenticationResponse authenticate(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );


        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails instanceof Account account) {
            if (Boolean.TRUE.equals(account.getIsBlocked())) {
                throw new AccountBlockedException("Tài khoản của bạn bị khóa.");
            }
        }

        if (jwtService.isTokenInWhiteList(userDetails.getUsername())){
            throw new AlreadyLoggedInException("Tài khoản đang được đăng nhập ở một nơi khác.");
        }


        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateAndSaveRefreshToken(userDetails);

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        return AuthenticationResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .role(role)
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws RefreshTokenException {
        String newAccessToken = jwtService.generateNewAccessTokenFromRefreshToken(request.getRefreshToken());
        String username = jwtService.extractUsername(request.getRefreshToken());

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        return AuthenticationResponse.builder()
                .token(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .username(username)
                .role(role)
                .build();
    }

    @Override
    public String requestChangePassword(String email) throws CustomerException {
        // Validate email format first
        if (!isValidEmail(email)) {
            throw new CustomerException("Địa chỉ email không hợp lệ");
        }

        Customer customer = customerRepo.findCustomerByEmail(email)
                .orElseThrow(() -> new CustomerException("Email không tồn tại hoặc không có quyền đổi mật khẩu"));

        // Check request limit
        if (redisService.getOTPRequestCount(email) >= 3) {
            throw new CustomerException("Bạn đã yêu cầu quá nhiều lần. Vui lòng thử lại sau 30 phút");
        }

        String code = generateRandomCode(6);
        redisService.saveOTP(email, code, 10);
        redisService.incrementOTPRequestCount(email);

        emailService.sendPasswordResetEmail(email, customer.getFullName(), code);

        return "Mã xác thực đã được gửi. Vui lòng kiểm tra email của bạn.";
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private String generateRandomCode(int length) {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, length)
                .toUpperCase();
    }


    // Method changePassword
    @Override
    public String changePassword(String email, String code, String newPassword) throws CustomerException {
        String savedCode = redisService.getOTP(email);
        if (savedCode == null) {
            throw new CustomerException("Mã xác thực hết hạn hoặc không tồn tại");
        }
        if (!savedCode.equals(code)) {
            throw new CustomerException("Mã xác thực không đúng");
        }

        Customer customer = customerRepo.findCustomerByEmail(email)
                .orElseThrow(() -> new CustomerException("Không tìm thấy tài khoản với email này"));

        if (encoder.matches(newPassword, customer.getPassword())) {
            throw new CustomerException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        customer.setPassword(encoder.encode(newPassword));
        customerRepo.save(customer);

        redisService.deleteOTP(email);
        redisService.deleteOTPRequestCount(email);

        return "Đổi mật khẩu thành công";
    }

}
