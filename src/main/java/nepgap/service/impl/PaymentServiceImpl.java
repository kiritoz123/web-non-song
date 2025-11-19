package nepgap.service.impl;

import lombok.RequiredArgsConstructor;
import nepgap.dto.CreatePaymentRequest;
import nepgap.dto.PaymentDTO;
import nepgap.dto.UpdatePaymentStatusRequest;
import nepgap.exception.ApiException;
import nepgap.model.Payment;
import nepgap.model.PaymentStatus;
import nepgap.repository.PaymentRepository;
import nepgap.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public PaymentDTO createPayment(Long userId, CreatePaymentRequest request) {
        // Verify order exists and belongs to user

        // Create payment
        Payment payment = Payment.builder()
                .userId(userId)
                .amount(request.getAmount())
                .currency(request.getCurrency() != null ? request.getCurrency() : "VND")
                .provider(request.getProvider() != null ? request.getProvider() : "MANUAL")
                .phone(request.getPhone())
                .address(request.getAddress())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return toDTO(savedPayment);
    }

    @Override
    public PaymentDTO getPaymentById(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new ApiException("Payment not found", HttpStatus.NOT_FOUND));
        return toDTO(payment);
    }

    @Override
    public List<PaymentDTO> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentDTO updatePaymentStatus(Long userId, Long paymentId, UpdatePaymentStatusRequest request) {
        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new ApiException("Payment not found", HttpStatus.NOT_FOUND));

        PaymentStatus oldStatus = payment.getStatus();
        payment.setStatus(request.getStatus());
        Payment savedPayment = paymentRepository.save(payment);

        return toDTO(savedPayment);
    }

    private PaymentDTO toDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .provider(payment.getProvider())
                .status(payment.getStatus())
                .build();
    }
}