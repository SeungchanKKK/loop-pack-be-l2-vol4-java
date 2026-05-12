package com.loopers.application.member;

import com.loopers.domain.member.Gender;
import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberFacade {

    private final MemberService memberService;

    public MemberInfo register(String userId, String email, String birthDate, Gender gender) {
        MemberModel member = new MemberModel(userId, email, birthDate, gender);
        MemberModel saved = memberService.register(member);
        return MemberInfo.from(saved);
    }

    public MemberInfo getMember(String userId) {
        MemberModel member = memberService.getByUserId(userId);
        if (member == null) return null;
        return MemberInfo.from(member);
    }
}
