package com.fdrcyln.controller.impl;

import com.fdrcyln.common.ApiResponse;
import com.fdrcyln.controller.IMemberController;
import com.fdrcyln.dto.request.CreateMemberRequest;
import com.fdrcyln.dto.request.UpdateMemberRequest;
import com.fdrcyln.dto.response.MemberResponse;
import com.fdrcyln.service.IMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MemberControllerImpl implements IMemberController {

    private final IMemberService memberService;

    public MemberControllerImpl(IMemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public ResponseEntity<ApiResponse<MemberResponse>> save(CreateMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success(memberService.save(request)));
    }

    @Override
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(memberService.getAll()));
    }

    @Override
    public ResponseEntity<ApiResponse<MemberResponse>> getById(Long id) {
        return ResponseEntity.ok(ApiResponse.success(memberService.getById(id)));
    }

    @Override
    public ResponseEntity<ApiResponse<MemberResponse>> update(Long id, UpdateMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success(memberService.update(id, request)));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        memberService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Üye başarıyla pasif hale getirildi", null));
    }
}