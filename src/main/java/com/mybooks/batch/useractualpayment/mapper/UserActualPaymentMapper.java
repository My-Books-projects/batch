package com.mybooks.batch.useractualpayment.mapper;

import com.mybooks.batch.useractualpayment.model.UserActualPayment;
import com.mybooks.batch.useractualpayment.model.UserGrade;
import org.apache.ibatis.annotations.Mapper;

/**
 * packageName    : com.mybooks.batch.useractualpayment.mapper
 * fileName       : UserActualPaymentMapper
 * author         : damho-lee
 * date           : 3/21/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/21/24          damho-lee          최초 생성
 */
@Mapper
public interface UserActualPaymentMapper {
    UserActualPayment getUserActualPayment();

    void updateUserGrade(UserGrade userGrade);
}
