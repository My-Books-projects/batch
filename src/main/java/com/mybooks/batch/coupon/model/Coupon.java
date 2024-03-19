package com.mybooks.batch.coupon.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * packageName    : com.mybooks.batch.coupon.model
 * fileName       : Coupon
 * author         : damho-lee
 * date           : 3/12/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/12/24          damho-lee          최초 생성
 */
@Getter
@AllArgsConstructor
public class Coupon {
    private Long id;
    private String name;
    private Integer bookId;
    private Integer categoryId;
    private Integer orderMin;
    private Integer discountCost;
    private Integer maxDiscountCost;
    private Integer discountRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isRate;
    private Boolean isTargetOrder;
    private LocalDate createdDate;
}
