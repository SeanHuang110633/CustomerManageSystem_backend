package com.RM.manageSystem.service;

import com.RM.manageSystem.pojo.Customer;
import com.RM.manageSystem.pojo.PageBean;

import java.util.List;

public interface CustomerService {

    /**
     * 查詢客戶列表
     *
     * @param pageNum 頁碼
     * @param pageSize 每頁資料筆數
     * @param gender  性別(可選)
     * @param coachId 所屬教練(可選)
     * @return PageBean<Customer> 分頁查詢結果
     */
    PageBean<Customer> listCustomers(Integer pageNum, Integer pageSize, Character gender, Integer coachId);

    /**
     * 新增客戶
     *
     * @param customer 客戶對象
     */
    void addCustomer(Customer customer);

    /**
     * 更新客戶資料
     *
     * @param customer 客戶對象
     */
    void updateCustomer(Customer customer);

    /**
     * 刪除客戶
     *
     * @param id 客戶id
     */
    void deleteCustomerById(Integer id);
}
