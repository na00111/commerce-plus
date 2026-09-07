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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    public static Member createNormalMember(String email, String password, String name, String phoneNumber) {
        return new Member(email, password, name, phoneNumber, Role.NORMAL, Status.ACTIVE);
    }

    public static Member createAdminMember(String email, String password, String name, String phoneNumber, Role role) {
        return new Member(email, password, name, phoneNumber, role, Status.INACTIVE);
    }

    public void activeAdmin(){
        this.status = Status.ACTIVE;
    }

    public void inactiveAdmin(){
        this.status = Status.INACTIVE;
    }


    private Member(String email, String password, String name, String phoneNumber,  Role role, Status status) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = status;
    }
}
