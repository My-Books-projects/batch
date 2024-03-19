package com.mybooks.batch.user.model;

import lombok.Getter;

/**
 * packageName    : com.mybooks.batch.user.model
 * fileName       : UserStatus
 * author         : damho-lee
 * date           : 3/19/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/19/24          damho-lee          최초 생성
 */
@Getter
public enum UserStatus {
    ACTIVE("활성"),
    INACTIVE("휴면");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }
}
