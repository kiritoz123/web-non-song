package nepgap.service;

import nepgap.dto.CustomerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomerService {
    /**
     * Lấy tất cả khách hàng
     */
    List<CustomerDTO> getAllCustomers();

    /**
     * Lấy khách hàng theo phân trang
     */
    Page<CustomerDTO> getCustomers(Pageable pageable);

    /**
     * Lấy thông tin khách hàng theo ID
     */
    CustomerDTO getCustomerById(Long id);

    /**
     * Đếm tổng số khách hàng
     */
    Long getTotalCustomers();

    /**
     * Tìm kiếm khách hàng theo từ khóa (email, tên, số điện thoại)
     */
    List<CustomerDTO> searchCustomers(String keyword);
}
