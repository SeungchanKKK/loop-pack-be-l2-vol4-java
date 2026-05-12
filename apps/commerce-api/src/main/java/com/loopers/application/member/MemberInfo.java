package com.loopers.application.member;

import com.loopers.domain.member.Gender;
import com.loopers.domain.member.MemberModel;

import java.time.LocalDate;

public record MemberInfo(Long id, String userId, String email, LocalDate birthDate, Gender gender, long points) {
    public static MemberInfo from(MemberModel member) {
        return new MemberInfo(
            member.getId(),
            member.getUserId(),
            member.getEmail(),
            member.getBirthDate(),
            member.getGender(),
            member.getPoints()
        );
    }
}
