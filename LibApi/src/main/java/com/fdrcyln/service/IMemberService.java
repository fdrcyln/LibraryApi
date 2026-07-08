package com.fdrcyln.service;

import com.fdrcyln.dto.request.CreateMemberRequest;
import com.fdrcyln.dto.request.UpdateMemberRequest;
import com.fdrcyln.dto.response.MemberResponse;

import java.util.List;

public interface IMemberService {

    MemberResponse save(CreateMemberRequest request);

    List<MemberResponse> getAll();

    MemberResponse getById(Long id);

    MemberResponse update(Long id, UpdateMemberRequest request);

    void delete(Long id);
}