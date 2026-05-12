package com.loopers.domain.member;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "member")
public class MemberModel extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private long points = 0;

    protected MemberModel() {}

    public MemberModel(String userId, String email, String birthDate, Gender gender) {
        if (userId == null || !userId.matches("[a-zA-Z0-9]{1,10}")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자 10자 이내여야 합니다.");
        }
        if (email == null || !email.matches("^[^@]+@[^@]+\\.[^@]+$")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "올바른 이메일 형식이 아닙니다.");
        }
        if (birthDate != null) {
            try {
                LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 yyyy-MM-dd 형식이어야 합니다.");
            }
        }
        if (gender == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 필수입니다.");
        }

        this.userId = userId;
        this.email = email;
        this.birthDate = birthDate != null
            ? LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            : null;
        this.gender = gender;
    }

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public LocalDate getBirthDate() { return birthDate; }
    public Gender getGender() { return gender; }
    public long getPoints() { return points; }
}
