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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public MemberResponse save(CreateMemberRequest request) {
        String trimmedEmail = request.getEmail() != null ? request.getEmail().trim() : "";
        java.util.List<Member> existingList = memberRepository.findByEmailIgnoreCase(trimmedEmail);
        if (!existingList.isEmpty()) {
            boolean anyActive = existingList.stream().anyMatch(Member::getActive);
            if (anyActive) {
                throw new BadRequestException("Bu e-posta adresiyle kayıtlı aktif bir üye zaten mevcut.");
            } else {
                Member existingMember = existingList.get(0);
                existingMember.setActive(true);
                existingMember.setFirstName(request.getFirstName() != null ? request.getFirstName().trim() : "");
                existingMember.setLastName(request.getLastName() != null ? request.getLastName().trim() : "");
                existingMember.setEmail(trimmedEmail);
                existingMember.setPhone(request.getPhone() != null ? request.getPhone().trim() : "");
                existingMember.setAddress(request.getAddress() != null ? request.getAddress().trim() : "");
                if (existingMember.getMembershipDate() == null) {
                    existingMember.setMembershipDate(java.time.LocalDate.now());
                }
                Member savedMember = memberRepository.save(existingMember);
                return memberMapper.toResponse(savedMember);
            }
        }

        Member member = memberMapper.toEntity(request);
        member.setEmail(trimmedEmail);
        if (member.getFirstName() != null) {
            member.setFirstName(member.getFirstName().trim());
        }
        if (member.getLastName() != null) {
            member.setLastName(member.getLastName().trim());
        }
        if (member.getMembershipDate() == null) {
            member.setMembershipDate(java.time.LocalDate.now());
        }
        member.setActive(true);
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
    @Transactional
    public MemberResponse update(Long id, UpdateMemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı. ID: " + id));

        String trimmedEmail = request.getEmail() != null ? request.getEmail().trim() : "";
        if (memberRepository.existsByEmailIgnoreCaseAndIdNotAndActiveTrue(trimmedEmail, id)) {
            throw new BadRequestException("Bu e-posta adresiyle kayıtlı aktif bir üye zaten mevcut.");
        }

        member.setFirstName(request.getFirstName() != null ? request.getFirstName().trim() : "");
        member.setLastName(request.getLastName() != null ? request.getLastName().trim() : "");
        member.setEmail(trimmedEmail);
        member.setPhone(request.getPhone() != null ? request.getPhone().trim() : "");
        member.setAddress(request.getAddress() != null ? request.getAddress().trim() : "");
        member.setActive(request.getActive());

        Member updatedMember = memberRepository.save(member);

        return memberMapper.toResponse(updatedMember);
    }

    @Override
    @Transactional
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