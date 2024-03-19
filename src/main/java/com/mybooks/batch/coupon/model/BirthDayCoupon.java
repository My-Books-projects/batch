package com.mybooks.batch.coupon.model;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * packageName    : com.mybooks.batch.coupon.model
 * fileName       : BirthDayCoupon
 * author         : damho-lee
 * date           : 3/16/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/16/24          damho-lee          최초 생성
 */
@Getter
@Builder
public class BirthDayCoupon {
    private Long id;
    private String name;
    private Integer orderMin;
    private Integer maxDiscountCost;
    private Integer discountRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isRate;
    private Boolean isTargetOrder;
    private LocalDate createdDate;
}
