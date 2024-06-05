package com.RM.manageSystem.mapper;


import com.RM.manageSystem.pojo.Customer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CustomerMapper {

    /**
     * 查詢客戶列表
     *
     * @param gender  性別(可選)
     * @param coachId 所屬教練(可選)
     * @return List<Customer> 客戶列表
     */
    List<Customer> listCustomer(Character gender, Integer coachId);

    /**
     * 新增客戶
     *
     * @param customer 客戶對象
     * @return int 新增的行數
     */
    int addCustomer(Customer customer);

    /**
     * 更新客戶資訊
     *
     * @param customer 客戶對象
     * @return int 更新的行數
     */
    int updateCustomer(Customer customer);

    /**
     * 依據id刪除客戶
     *
     * @param id 客戶id
     * @return int 刪除的行數
     */

    @Update("update customer set isDelete = 1, update_time = now() where id = #{id}")
    int deleteCustomerById(Integer id);

}
