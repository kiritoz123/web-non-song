package nepgap.service.impl;

import lombok.RequiredArgsConstructor;
import nepgap.dto.CustomerDTO;
import nepgap.exception.ApiException;
import nepgap.model.Role;
import nepgap.model.User;
import nepgap.repository.UserRepository;
import nepgap.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CustomerDTO> getCustomers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("Không tìm thấy khách hàng với ID: " + id, HttpStatus.NOT_FOUND));
        return convertToDTO(user);
    }

    @Override
    public Long getTotalCustomers() {
        return userRepository.count();
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }
        
        // Tìm kiếm theo email, tên hoặc số điện thoại
        return userRepository.findAll().stream()
                .filter(user -> 
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(keyword.toLowerCase())) ||
                    (user.getFullName() != null && user.getFullName().toLowerCase().contains(keyword.toLowerCase())) ||
                    (user.getPhone() != null && user.getPhone().contains(keyword))
                )
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi User entity sang CustomerDTO
     */
    private CustomerDTO convertToDTO(User user) {
        return CustomerDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .map(Enum::name)
                        .collect(Collectors.toSet()))
                .build();
    }
}
