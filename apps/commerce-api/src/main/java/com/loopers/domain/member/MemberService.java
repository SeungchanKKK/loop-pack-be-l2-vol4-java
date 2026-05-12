package com.loopers.domain.member;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberModel register(MemberModel member) {
        if (memberRepository.existsByUserId(member.getUserId())) {
            throw new CoreException(ErrorType.CONFLICT, "이미 가입된 ID입니다.");
        }
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public MemberModel getByUserId(String userId) {
        return memberRepository.findByUserId(userId).orElse(null);
    }
}
