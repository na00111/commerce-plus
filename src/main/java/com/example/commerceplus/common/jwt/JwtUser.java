package com.example.commerceplus.common.jwt;

import com.example.commerceplus.domain.member.entity.Role;
import com.example.commerceplus.domain.member.entity.Status;

public record JwtUser(Long id, String email, Role role, Status status) {
}
