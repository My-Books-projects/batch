package com.mybooks.batch.usercoupon.model;

import java.time.LocalDate;
import java.util.Date;
import lombok.Getter;

/**
 * packageName    : com.mybooks.batch.usercoupon.model
 * fileName       : UserCoupon
 * author         : damho-lee
 * date           : 3/12/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/12/24          damho-lee          최초 생성
 */
@Getter
public class UserCoupon {
    private Long userId;
    private Long couponId;
    private Date userCouponCreatedDate;
    private Date userCouponDate;
    private boolean isUsed;

    /**
     * Instantiates a new User coupon.
     *
     * @param userId   Long
     * @param couponId Long
     */
    public UserCoupon(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
        this.userCouponCreatedDate = java.sql.Date.valueOf(LocalDate.now());
        this.userCouponDate = null;
        this.isUsed = true;
    }
}
