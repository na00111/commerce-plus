package com.example.commerceplus.domain.member.sevice;

import com.example.commerceplus.common.bean.PasswordEncoder;
import com.example.commerceplus.common.exception.BusinessException;
import com.example.commerceplus.common.exception.ErrorCode;
import com.example.commerceplus.domain.member.dto.request.CreateAdminRequest;
import com.example.commerceplus.domain.member.dto.request.CreateMemberRequest;
import com.example.commerceplus.domain.member.dto.response.CreateAdminResponse;
import com.example.commerceplus.domain.member.dto.response.CreateMemberResponse;
import com.example.commerceplus.domain.member.entity.Member;
import com.example.commerceplus.domain.member.entity.MemberRole;
import com.example.commerceplus.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public CreateMemberResponse createMember(CreateMemberRequest request) {

        // 생성패스워드와 검증패스워드가 다름
        if (!request.isPasswordCorrect()) {
            throw new BusinessException(ErrorCode.PASSWORD_CONFIRMATION_MISMATCH);
        }

        // 이미 존재하는 이메일
        boolean findMember = memberRepository.existsByEmail(request.email());
        if (findMember) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.createNormalMember(request.email(),encodedPassword, request.name(), request.phoneNumber());
        Member savedMember = memberRepository.save(member);

        return CreateMemberResponse.from(savedMember);
    }

    public CreateAdminResponse createAdmin(CreateAdminRequest request) {

        if (request.role() == MemberRole.NORMAL) {
            throw new BusinessException(ErrorCode.Invalid_Admin_Role_Exception);
        }

        // 생성패스워드와 검증패스워드가 다름
        if (!request.isPasswordCorrect()) {
            throw new BusinessException(ErrorCode.PASSWORD_CONFIRMATION_MISMATCH);
        }

        // 이미 존재하는 이메일
        boolean findMember = memberRepository.existsByEmail(request.email());
        if (findMember) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.createAdminMember(request.email(), encodedPassword, request.name(), request.phoneNumber(), request.role());
        Member savedMember = memberRepository.save(member);

        return CreateAdminResponse.from(savedMember);
    }

}
