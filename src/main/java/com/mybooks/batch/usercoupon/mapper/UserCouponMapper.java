package com.mybooks.batch.usercoupon.mapper;

import com.mybooks.batch.usercoupon.model.UserCoupon;
import org.apache.ibatis.annotations.Mapper;

/**
 * packageName    : com.mybooks.batch.usercoupon.mapper
 * fileName       : UserCouponMapper
 * author         : damho-lee
 * date           : 3/12/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/12/24          damho-lee          최초 생성
 */
@Mapper
public interface UserCouponMapper {
    void createUserBirthDayCoupon(UserCoupon userCoupon);
}
