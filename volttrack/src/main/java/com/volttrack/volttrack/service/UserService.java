package com.volttrack.volttrack.service;

import com.volttrack.volttrack.dto.user.UserRequestDto;
import com.volttrack.volttrack.dto.user.UserResponseDto;

import java.util.List;

import com.volttrack.volttrack.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto createUser(UserRequestDto requestDto);
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto getUserById(Long id);
    void deleteUser(Long id);
    UserResponseDto createConsumerActive(UserRequestDto requestDto);
    Page<UserResponseDto> getConsumers(Pageable pageable);
    UserResponseDto approveOfficer(String publicId);
    UserResponseDto getUserByPublicId(String publicId);
    void deleteUserByPublicId(String publicId);
    Page<UserResponseDto> getConsumersByName(String name, Pageable pageable);
    Page<UserResponseDto> getPendingOfficers(Pageable pageable);
    UserResponseDto approveOfficerBySubDistrict(String publicId, Long approverId);
    UserResponseDto rejectOfficerBySubDistrict(String publicId, Long approverId);
    Page<UserResponseDto> getOfficersInSubDistrict(Long subDistrictOfficerId, Pageable pageable);
    UserResponseDto suspendOfficer(String publicId, Long approverId);
    Page<UserResponseDto> getSubDistrictCustomerReport(Long subDistrictOfficerId, Pageable pageable);

}
