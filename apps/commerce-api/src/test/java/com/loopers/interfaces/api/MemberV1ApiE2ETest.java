package com.loopers.interfaces.api;

import com.loopers.domain.member.Gender;
import com.loopers.domain.member.MemberModel;
import com.loopers.infrastructure.member.MemberJpaRepository;
import com.loopers.interfaces.api.member.MemberV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberV1ApiE2ETest {

    private static final String ENDPOINT_REGISTER = "/api/v1/members";
    private static final String ENDPOINT_GET_MEMBER = "/api/v1/members/{userId}";
    private static final String ENDPOINT_GET_POINTS = "/api/v1/members/points";

    private final TestRestTemplate testRestTemplate;
    private final MemberJpaRepository memberJpaRepository;
    private final DatabaseCleanUp databaseCleanUp;

    @Autowired
    public MemberV1ApiE2ETest(
        TestRestTemplate testRestTemplate,
        MemberJpaRepository memberJpaRepository,
        DatabaseCleanUp databaseCleanUp
    ) {
        this.testRestTemplate = testRestTemplate;
        this.memberJpaRepository = memberJpaRepository;
        this.databaseCleanUp = databaseCleanUp;
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/members")
    @Nested
    class Post {

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsCreatedMemberInfo_whenRegistrationIsSuccessful() {
            // arrange
            MemberV1Dto.RegisterRequest request = new MemberV1Dto.RegisterRequest(
                "testuser1", "test@test.com", "1990-01-01", Gender.MALE
            );
            HttpEntity<MemberV1Dto.RegisterRequest> httpEntity = new HttpEntity<>(request);

            // act
            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberV1Dto.MemberResponse>> response =
                testRestTemplate.exchange(ENDPOINT_REGISTER, HttpMethod.POST, httpEntity, responseType);

            // assert
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().userId()).isEqualTo(request.userId()),
                () -> assertThat(response.getBody().data().email()).isEqualTo(request.email()),
                () -> assertThat(response.getBody().data().gender()).isEqualTo(request.gender())
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void throwsBadRequest_whenGenderIsNotProvided() {
            // arrange
            MemberV1Dto.RegisterRequest request = new MemberV1Dto.RegisterRequest(
                "testuser1", "test@test.com", "1990-01-01", null
            );
            HttpEntity<MemberV1Dto.RegisterRequest> httpEntity = new HttpEntity<>(request);

            // act
            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberV1Dto.MemberResponse>> response =
                testRestTemplate.exchange(ENDPOINT_REGISTER, HttpMethod.POST, httpEntity, responseType);

            // assert
            assertAll(
                () -> assertTrue(response.getStatusCode().is4xxClientError()),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }

    @DisplayName("GET /api/v1/members/{userId}")
    @Nested
    class GetMember {

        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        void returnsMemberInfo_whenMemberExists() {
            // arrange
            MemberModel member = memberJpaRepository.save(
                new MemberModel("testuser1", "test@test.com", "1990-01-01", Gender.MALE)
            );

            // act
            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberV1Dto.MemberResponse>> response =
                testRestTemplate.exchange(ENDPOINT_GET_MEMBER, HttpMethod.GET, new HttpEntity<>(null), responseType, member.getUserId());

            // assert
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().userId()).isEqualTo(member.getUserId()),
                () -> assertThat(response.getBody().data().email()).isEqualTo(member.getEmail()),
                () -> assertThat(response.getBody().data().gender()).isEqualTo(member.getGender())
            );
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, 404 Not Found 응답을 받는다.")
        @Test
        void throwsNotFound_whenMemberNotExists() {
            // arrange
            String nonExistentUserId = "noone";

            // act
            ParameterizedTypeReference<ApiResponse<MemberV1Dto.MemberResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberV1Dto.MemberResponse>> response =
                testRestTemplate.exchange(ENDPOINT_GET_MEMBER, HttpMethod.GET, new HttpEntity<>(null), responseType, nonExistentUserId);

            // assert
            assertAll(
                () -> assertTrue(response.getStatusCode().is4xxClientError()),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }
    }

    @DisplayName("GET /api/v1/members/points")
    @Nested
    class GetPoints {

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void returnsPoints_whenMemberExists() {
            // arrange
            MemberModel member = memberJpaRepository.save(
                new MemberModel("testuser1", "test@test.com", "1990-01-01", Gender.MALE)
            );
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", member.getUserId());
            HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

            // act
            ParameterizedTypeReference<ApiResponse<MemberV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberV1Dto.PointResponse>> response =
                testRestTemplate.exchange(ENDPOINT_GET_POINTS, HttpMethod.GET, httpEntity, responseType);

            // assert
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().userId()).isEqualTo(member.getUserId()),
                () -> assertThat(response.getBody().data().points()).isEqualTo(member.getPoints())
            );
        }

        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void throwsBadRequest_whenUserIdHeaderIsNotProvided() {
            // arrange (no header)
            HttpEntity<Void> httpEntity = new HttpEntity<>(null);

            // act
            ParameterizedTypeReference<ApiResponse<MemberV1Dto.PointResponse>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<MemberV1Dto.PointResponse>> response =
                testRestTemplate.exchange(ENDPOINT_GET_POINTS, HttpMethod.GET, httpEntity, responseType);

            // assert
            assertAll(
                () -> assertTrue(response.getStatusCode().is4xxClientError()),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }
}
