package com.loopers.interfaces.api.member;

import com.loopers.application.member.MemberFacade;
import com.loopers.application.member.MemberInfo;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberV1Controller implements MemberV1ApiSpec {

    private final MemberFacade memberFacade;

    @PostMapping
    @Override
    public ApiResponse<MemberV1Dto.MemberResponse> register(@RequestBody MemberV1Dto.RegisterRequest request) {
        MemberInfo info = memberFacade.register(
            request.userId(),
            request.email(),
            request.birthDate(),
            request.gender()
        );
        return ApiResponse.success(MemberV1Dto.MemberResponse.from(info));
    }

    @GetMapping("/{userId}")
    @Override
    public ApiResponse<MemberV1Dto.MemberResponse> getMember(@PathVariable String userId) {
        MemberInfo info = memberFacade.getMember(userId);
        if (info == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "[userId = " + userId + "] 회원을 찾을 수 없습니다.");
        }
        return ApiResponse.success(MemberV1Dto.MemberResponse.from(info));
    }

    @GetMapping("/points")
    @Override
    public ApiResponse<MemberV1Dto.PointResponse> getPoints(
        @RequestHeader(value = "X-USER-ID", required = false) String userId
    ) {
        if (userId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "X-USER-ID 헤더가 필요합니다.");
        }
        MemberInfo info = memberFacade.getMember(userId);
        if (info == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "[userId = " + userId + "] 회원을 찾을 수 없습니다.");
        }
        return ApiResponse.success(MemberV1Dto.PointResponse.from(info));
    }
}
