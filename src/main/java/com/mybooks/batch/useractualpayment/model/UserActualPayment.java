package com.mybooks.batch.useractualpayment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * packageName    : com.mybooks.batch.order.model
 * fileName       : Order
 * author         : damho-lee
 * date           : 3/20/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/20/24          damho-lee          최초 생성
 */
@Getter
@AllArgsConstructor
public class UserActualPayment {
    private Long userId;
    private Integer actualPayment;
    private Integer userGradeId;
}
