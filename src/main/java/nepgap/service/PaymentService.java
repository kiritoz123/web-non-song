package nepgap.service;


import nepgap.dto.CreatePaymentRequest;
import nepgap.dto.PaymentDTO;
import nepgap.dto.UpdatePaymentStatusRequest;

import java.util.List;

public interface PaymentService {
    PaymentDTO createPayment(Long userId, CreatePaymentRequest request);
    PaymentDTO getPaymentById(Long userId, Long paymentId);
    List<PaymentDTO> getUserPayments(Long userId);
    PaymentDTO updatePaymentStatus(Long paymentId, UpdatePaymentStatusRequest request);
}
