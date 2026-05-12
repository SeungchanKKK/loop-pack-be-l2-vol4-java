package com.loopers.interfaces.api.member;

import com.loopers.application.member.MemberInfo;
import com.loopers.domain.member.Gender;

import java.time.LocalDate;

public class MemberV1Dto {

    public record RegisterRequest(String userId, String email, String birthDate, Gender gender) {}

    public record MemberResponse(Long id, String userId, String email, LocalDate birthDate, Gender gender) {
        public static MemberResponse from(MemberInfo info) {
            return new MemberResponse(
                info.id(),
                info.userId(),
                info.email(),
                info.birthDate(),
                info.gender()
            );
        }
    }

    public record PointResponse(String userId, long points) {
        public static PointResponse from(MemberInfo info) {
            return new PointResponse(info.userId(), info.points());
        }
    }
}
