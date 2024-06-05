package com.RM.manageSystem.service.imp;

import com.RM.manageSystem.exception.BusinessException;
import com.RM.manageSystem.exception.DatabaseOperationException;
import com.RM.manageSystem.exception.SystemException;
import com.RM.manageSystem.mapper.CustomerMapper;
import com.RM.manageSystem.pojo.Customer;
import com.RM.manageSystem.pojo.PageBean;
import com.RM.manageSystem.service.CustomerService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.RM.manageSystem.controller.ResponseCode.*;

/**
 * Customer Service
 */
@Slf4j
@Service
public class CustomerServiceImp implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 查詢客戶列表
     *
     * @param pageNum  頁碼
     * @param pageSize 每頁資料筆數
     * @param gender   性別(可選)
     * @param coachId  所屬教練(可選)
     * @return PageBean<Customer> 分頁查詢結果
     */
    @Override
    public PageBean<Customer> listCustomers(Integer pageNum, Integer pageSize, Character gender, Integer coachId) {
        log.info("查詢客戶列表: pageNum={}, pageSize={},  gender={}, coachId={}",
                pageNum, pageSize, gender, coachId);

        // 參數檢查
        if (pageNum == null || pageNum <= 0) {
            throw new BusinessException(INVALID_INPUT, "頁碼必須為正數");
        }
        if (pageSize == null || pageSize <= 0) {
            throw new BusinessException(INVALID_INPUT, "每頁顯示數量必須為正數");
        }

        // 創建一個PageBean對象存放分頁結果
        PageBean<Customer> pageBean = new PageBean<>();
        // 使用PageHelper分頁，傳入頁數和每頁顯示的數量
        PageMethod.startPage(pageNum, pageSize);
        List<Customer> customerList = customerMapper.listCustomer(gender, coachId);
        if (customerList == null) {
            log.error("客戶列表查詢返回值為空");
            throw new BusinessException(CUSTOMER_NOT_FOUND, "查詢客戶列表失敗");
        }
        // 類型轉換檢查
        if (!(customerList instanceof Page<Customer>)) {
            log.error("客戶列表查詢返回值類型轉換異常");
            throw new SystemException(TYPE_CONVERSION_ERROR, "數據類型轉換失敗");
        }
        Page<Customer> customerPage = (Page<Customer>) customerList;
        pageBean.setTotal(customerPage.getTotal());
        pageBean.setItems(customerPage.getResult());
        log.info("查詢客戶列表成功: {}", pageBean);
        return pageBean;
    }

    /**
     * 新增客戶
     *
     * @param customer 客戶對象
     */
    @Override
    public void addCustomer(Customer customer) {
        log.info("新增客戶: {}", customer);
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        int addedCustomerNum = customerMapper.addCustomer(customer);
        if (addedCustomerNum > 0) {
            log.info("新增客戶成功: {}", customer);
            return;
        }
        log.error("新增客戶失敗: {}", customer);
        throw new SystemException(SYSTEM_ERR, "系統異常請聯繫管理員");
    }

    /**
     * 更新客戶資料
     *
     * @param customer 客戶對象
     */
    @Override
    public void updateCustomer(Customer customer) {
        log.info("更新客戶資料: {}", customer);
        customer.setUpdateTime(LocalDateTime.now());
        int updatedCustomerNum = customerMapper.updateCustomer(customer);
        if (updatedCustomerNum > 0) {
            log.info("更新客戶資料成功: {}", customer);
            return;
        }
        log.error("更新客戶資料失敗: {}", customer);
        throw new SystemException(SYSTEM_ERR, "系統異常請聯繫管理員");
    }


    /**
     * 刪除客戶
     *
     * @param id 客戶id
     */
    @Override
    public void deleteCustomerById(Integer id) {
        log.info("刪除客戶ID: {}", id);
        int deletedCustomerNum = customerMapper.deleteCustomerById(id);
        if (deletedCustomerNum > 0) {
            log.info("刪除客戶成功ID: {}", id);
            return;
        }
        log.error("刪除客戶失敗ID: {}", id);
        throw new SystemException(SYSTEM_ERR, "系統異常請聯繫管理員");
    }
}
