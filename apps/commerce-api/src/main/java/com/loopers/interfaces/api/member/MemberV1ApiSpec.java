package com.loopers.interfaces.api.member;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Member V1 API", description = "Loopers 회원 API 입니다.")
public interface MemberV1ApiSpec {

    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
    ApiResponse<MemberV1Dto.MemberResponse> register(MemberV1Dto.RegisterRequest request);

    @Operation(summary = "회원 정보 조회", description = "userId로 회원 정보를 조회합니다.")
    ApiResponse<MemberV1Dto.MemberResponse> getMember(String userId);

    @Operation(summary = "포인트 조회", description = "X-USER-ID 헤더로 보유 포인트를 조회합니다.")
    ApiResponse<MemberV1Dto.PointResponse> getPoints(String userId);
}
