package com.example.commerceplus.domain.member.entity;

public enum MemberStatus {
    INACTIVE, // 비활성화 계정
    ACTIVE, // 활성계정, 일반 사용자 가본값
    PENDING; //대기,  admin 일 경우 기본값
}
