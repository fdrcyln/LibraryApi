package com.fdrcyln.service.impl;

import com.fdrcyln.dto.request.CreateMemberRequest;
import com.fdrcyln.dto.request.UpdateMemberRequest;
import com.fdrcyln.dto.response.MemberResponse;
import com.fdrcyln.entities.Member;
import com.fdrcyln.exception.ResourceNotFoundException;
import com.fdrcyln.mapper.MemberMapper;
import com.fdrcyln.repository.IMemberRepository;
import com.fdrcyln.service.IMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements IMemberService {

    private final IMemberRepository memberRepository;
    private final MemberMapper memberMapper;

    public MemberServiceImpl(IMemberRepository memberRepository, MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
    }

    @Override
    public MemberResponse save(CreateMemberRequest request) {
        Member member = memberMapper.toEntity(request);
        Member savedMember = memberRepository.save(member);
        return memberMapper.toResponse(savedMember);
    }

    @Override
    public List<MemberResponse> getAll() {
        return memberRepository.findByActiveTrue()
                .stream()
                .map(memberMapper::toResponse)
                .toList();
    }

    @Override
    public MemberResponse getById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı. ID: " + id));

        return memberMapper.toResponse(member);
    }

    @Override
    public MemberResponse update(Long id, UpdateMemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı. ID: " + id));

        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setAddress(request.getAddress());
        member.setActive(request.getActive());

        Member updatedMember = memberRepository.save(member);

        return memberMapper.toResponse(updatedMember);
    }

    @Override
    public void delete(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı. ID: " + id));

        member.setActive(false);
        memberRepository.save(member);
    }
}