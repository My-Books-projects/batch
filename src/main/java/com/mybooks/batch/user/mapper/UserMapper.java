package com.mybooks.batch.user.mapper;

import com.mybooks.batch.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * packageName    : com.mybooks.batch.user
 * fileName       : User
 * author         : damho-lee
 * date           : 3/11/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/11/24          damho-lee          최초 생성
 */
@Mapper
public interface UserMapper {
    Long getUserIdWhoWasBornThisMonth(@Param("month") String month);

    Long getUserStatusChangeTarget(@Param("user_status") String userStatus);

    void updateUserStatus(User user);
}