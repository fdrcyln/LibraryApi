package com.fdrcyln.mapper;

import com.fdrcyln.dto.request.CreateMemberRequest;
import com.fdrcyln.dto.response.MemberResponse;
import com.fdrcyln.entities.Member;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MemberMapper {

    public Member toEntity(CreateMemberRequest request) {
        Member member = new Member();

        BeanUtils.copyProperties(request, member);

        member.setMembershipDate(LocalDate.now());
        member.setActive(true);

        return member;
    }

    public MemberResponse toResponse(Member member) {
        MemberResponse response = new MemberResponse();

        BeanUtils.copyProperties(member, response);

        return response;
    }
}