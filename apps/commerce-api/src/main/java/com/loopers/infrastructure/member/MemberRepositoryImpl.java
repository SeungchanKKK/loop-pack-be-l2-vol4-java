package com.loopers.infrastructure.member;

import com.loopers.domain.member.MemberModel;
import com.loopers.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public MemberModel save(MemberModel member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public Optional<MemberModel> findByUserId(String userId) {
        return memberJpaRepository.findByUserId(userId);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return memberJpaRepository.existsByUserId(userId);
    }
}
