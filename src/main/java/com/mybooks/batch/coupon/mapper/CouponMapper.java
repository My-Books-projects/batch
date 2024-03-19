package com.mybooks.batch.coupon.mapper;

import com.mybooks.batch.coupon.model.BirthDayCoupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * packageName    : com.mybooks.batch.coupon.mapper
 * fileName       : CouponMapper
 * author         : damho-lee
 * date           : 3/11/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/11/24          damho-lee          최초 생성
 */
@Mapper
public interface CouponMapper {
    Integer createBirthDayCoupon(BirthDayCoupon birthDayCoupon);
}