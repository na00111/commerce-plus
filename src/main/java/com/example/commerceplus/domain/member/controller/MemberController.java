package com.example.commerceplus.domain.member.controller;

import com.example.commerceplus.common.api.ApiResponse;
import com.example.commerceplus.domain.member.dto.request.CreateMemberRequest;
import com.example.commerceplus.domain.member.dto.response.CreateMemberResponse;
import com.example.commerceplus.domain.member.sevice.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<CreateMemberResponse>> CreateMember(@Valid @RequestBody CreateMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(memberService.createMember(request)));
    }

}
