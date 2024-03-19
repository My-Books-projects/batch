package com.mybooks.batch.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * packageName    : com.mybooks.batch.user.model
 * fileName       : User
 * author         : damho-lee
 * date           : 3/19/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/19/24          damho-lee          최초 생성
 */
@Getter
@AllArgsConstructor
public class User {
    private Long id;
    private String status;
}
