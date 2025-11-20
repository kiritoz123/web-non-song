package nepgap.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nepgap.dto.*;
import nepgap.model.Payment;
import nepgap.repository.PaymentRepository;
import nepgap.security.UserPrincipal;
import nepgap.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDTO>> createPayment(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreatePaymentRequest request,
            HttpServletRequest req) {
        PaymentDTO payment = paymentService.createPayment(user.getId(), request);
        ApiResponse<PaymentDTO> response = ApiResponse.<PaymentDTO>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CREATED.value())
                .message("Payment created successfully")
                .data(payment)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            HttpServletRequest req) {
        PaymentDTO payment = paymentService.getPaymentById(user.getId(), id);
        ApiResponse<PaymentDTO> response = ApiResponse.<PaymentDTO>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Payment retrieved successfully")
                .data(payment)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getUserPayments(
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest req) {
        List<PaymentDTO> payments = paymentService.getUserPayments(user.getId());
        ApiResponse<List<PaymentDTO>> response = ApiResponse.<List<PaymentDTO>>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Payments retrieved successfully")
                .data(payments)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments(
            HttpServletRequest req) {
        List<Payment> payments = paymentRepository.findAll();
        ApiResponse<List<Payment>> response = ApiResponse.<List<Payment>>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Payments retrieved successfully")
                .data(payments)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PaymentDTO>> updatePaymentStatus(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentStatusRequest request,
            HttpServletRequest req) {
        PaymentDTO payment = paymentService.updatePaymentStatus(id, request);
        ApiResponse<PaymentDTO> response = ApiResponse.<PaymentDTO>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Payment status updated successfully")
                .data(payment)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }
}