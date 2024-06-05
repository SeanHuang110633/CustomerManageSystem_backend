package com.RM.manageSystem.controller;

import com.RM.manageSystem.anno.Log;
import com.RM.manageSystem.pojo.Customer;
import com.RM.manageSystem.pojo.PageBean;
import com.RM.manageSystem.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.RM.manageSystem.controller.ResponseCode.*;

@Slf4j
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 查詢客戶列表
     *
     * @param pageNum  當前頁碼
     * @param pageSize 每頁顯示的記錄數
     * @param gender   客戶性別
     * @param coachId  教練ID
     * @return 包含客戶列表的PageBean對象
     */
    @GetMapping
    public Result<PageBean<Customer>> listCustomers(Integer pageNum,
                                                    Integer pageSize,
                                                    @RequestParam(required = false) Character gender,
                                                    @RequestParam(required = false) Integer coachId) {
        log.info("查詢客戶列表傳入的參數: pageNum={}, pageSize={},  gender={}, coachId={}",
                pageNum, pageSize, gender, coachId);
        PageBean<Customer> pb = customerService.listCustomers(pageNum, pageSize, gender, coachId);
        log.info("查詢客戶列表返回結果: {}", pb);
        return Result.success(SUCCESS, "查詢所有客戶成功", pb);
    }

    /**
     * 新增客戶
     *
     * @param customer 客戶對象
     * @return 結果對象
     */
    @Log
    @PostMapping
    public Result addCustomer(@RequestBody @Validated(Customer.Add.class) Customer customer) {
        log.info("新增客戶: {}", customer);
        customerService.addCustomer(customer);
        log.info("新增客戶成功: {}", customer);
        return Result.success(SUCCESS,"新增客戶成功");
    }

    /**
     * 編輯客戶資訊
     *
     * @param customer
     * @return 結果對象
     */
    @Log
    @PutMapping
    public Result updateCustomer(@RequestBody @Validated(Customer.Update.class) Customer customer) {
        log.info("更新客戶資料: {}", customer);
        customerService.updateCustomer(customer);
        log.info("更新客戶資料成功: {}", customer);
        return Result.success(SUCCESS,"更新客戶資料成功");
    }

    /**
     * 刪除客戶
     *
     * @param id 客戶id
     * @return 結果對象
     */
    @Log
    @DeleteMapping
    public Result deleteCustomerById(@RequestParam Integer id) {
        log.info("刪除客戶ID: {}", id);
        customerService.deleteCustomerById(id);
        log.info("刪除客戶成功ID: {}", id);
        return Result.success(SUCCESS,"刪除客戶成功");
    }
}
