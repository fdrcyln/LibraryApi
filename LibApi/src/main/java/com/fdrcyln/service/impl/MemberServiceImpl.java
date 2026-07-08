package com.fdrcyln.service.impl;

import com.fdrcyln.dto.request.CreateMemberRequest;
import com.fdrcyln.dto.request.UpdateMemberRequest;
import com.fdrcyln.dto.response.MemberResponse;
import com.fdrcyln.entities.Member;
import com.fdrcyln.enums.RentalStatus;
import com.fdrcyln.exception.BadRequestException;
import com.fdrcyln.exception.ResourceNotFoundException;
import com.fdrcyln.mapper.MemberMapper;
import com.fdrcyln.repository.IMemberRepository;
import com.fdrcyln.repository.IRentalRepository;
import com.fdrcyln.service.IMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements IMemberService {

    private final IMemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final IRentalRepository rentalRepository;

    public MemberServiceImpl(IMemberRepository memberRepository, MemberMapper memberMapper, IRentalRepository rentalRepository) {
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public MemberResponse save(CreateMemberRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu e-posta adresi zaten kullanımda.");
        }
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

        if (!member.getEmail().equalsIgnoreCase(request.getEmail()) && memberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu e-posta adresi zaten kullanımda.");
        }

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

        if (rentalRepository.existsByMemberIdAndStatus(id, RentalStatus.ACTIVE)) {
            throw new BadRequestException("Bu üyenin aktif kiralama işlemleri olduğu için silinemez.");
        }

        member.setActive(false);
        memberRepository.save(member);
    }
}