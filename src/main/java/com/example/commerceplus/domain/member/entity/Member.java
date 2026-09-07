package com.example.commerceplus.domain.member.entity;

import com.example.commerceplus.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 50 )
    private String password;

    @Column(nullable = false, length = 50 )
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private int point;

    // 기본값은 일반 사용자
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.NORMAL;

    // 기본값은 활성
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    // 일반 사용자 가입
    public Member(String email, String password, String name, String phoneNumber)
    {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.point = 3000;
    }

    // 관리자 사용자 가입
    public Member(
            String email, String password, String name, String phoneNumber, Role role)
    {

        if (role == Role.NORMAL) {
            Member member = new Member(email, password, name, phoneNumber);
            return;
        }

        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = Status.PENDING;
        this.point = 0;
    }

    public void activeAdmin(){
        this.status = Status.ACTIVE;
    }

    public void inactiveAdmin(){
        this.status = Status.INACTIVE;
    }

    // 포인트 차감
    public void usePoint(int amount) {
        if (this.point < amount) {
            // 추후 포인트 사용자 point enum 관련 추가 및 수정
            throw new IllegalArgumentException("보유한 포인트가 부족합니다.");
        }
        this.point -= amount;
    }

    // 포인트 충전
    public void earnPoint(int amount) {
        if (amount < 0) {
            // 추후 포인트 사용자 point enum 관련 추가 및 수정
            throw new IllegalArgumentException("적립/환불 금액은 음수일 수 없습니다.");
        }
        this.point += amount;
    }

}
