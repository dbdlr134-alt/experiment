package com.mnu.jpstudy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mnu.jpstudy.entity.ProfileRequest;

public interface ProfileRequestRepository extends JpaRepository<ProfileRequest, Long> {
    List<ProfileRequest> findByStatus(ProfileRequest.RequestStatus status);
}
