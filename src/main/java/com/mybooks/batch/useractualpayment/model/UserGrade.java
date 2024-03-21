package com.mybooks.batch.useractualpayment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * packageName    : com.mybooks.batch.useractualpayment.model
 * fileName       : UserGrade
 * author         : damho-lee
 * date           : 3/21/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/21/24          damho-lee          최초 생성
 */
@Getter
@AllArgsConstructor
public class UserGrade {
    private Long userId;
    private Integer userGradeId;
}
