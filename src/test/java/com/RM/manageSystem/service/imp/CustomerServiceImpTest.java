package com.RM.manageSystem.service.imp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.RM.manageSystem.pojo.Customer;
import com.RM.manageSystem.exception.BusinessException;
import com.RM.manageSystem.exception.SystemException;
import com.RM.manageSystem.mapper.CustomerMapper;
import com.RM.manageSystem.pojo.PageBean;
import com.RM.manageSystem.service.CustomerService;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CustomerServiceImpTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImp customerService;

    private Customer mockCustomer;

    @BeforeEach
    void setUp() {
        mockCustomer = Customer.builder()
                .id(1)
                .customerName("testCustomer")
                .gender('M')
                .phoneNumber("0900000000")
                .firstLesson(LocalDate.of(2024, 5, 29))
                .coachId(1)
                .build();
    }

    @Test
    void testListCustomersSuccess() {
        Page<Customer> customerPage = new Page<>();
        customerPage.add(mockCustomer);
        customerPage.setTotal(1);

        when(customerMapper.listCustomer(null, null)).thenReturn(customerPage);

        PageBean<Customer> result = customerService.listCustomers(1, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getItems().size());
        assertEquals(mockCustomer, result.getItems().get(0));
        verify(customerMapper, times(1)).listCustomer(null, null);
    }

    @Test
    void testListCustomersInvalidPageNum() {
        BusinessException thrown = assertThrows(BusinessException.class,
                () -> customerService.listCustomers(0, 10, null, null));
        assertEquals("頁碼必須為正數", thrown.getMessage());
    }

    @Test
    void testListCustomersInvalidPageSize() {
        BusinessException thrown = assertThrows(BusinessException.class,
                () -> customerService.listCustomers(1, 0, null, null));
        assertEquals("每頁顯示數量必須為正數", thrown.getMessage());
    }

    @Test
    void testAddCustomerSuccess() {
        when(customerMapper.addCustomer(any(Customer.class))).thenReturn(1);

        customerService.addCustomer(mockCustomer);

        verify(customerMapper, times(1)).addCustomer(any(Customer.class));
    }

    @Test
    void testAddCustomerFailure() {
        when(customerMapper.addCustomer(any(Customer.class))).thenReturn(0);

        SystemException thrown = assertThrows(SystemException.class,
                () -> customerService.addCustomer(mockCustomer));
        assertEquals("系統異常請聯繫管理員", thrown.getMessage());
    }

    @Test
    void testUpdateCustomerSuccess() {
        when(customerMapper.updateCustomer(any(Customer.class))).thenReturn(1);

        customerService.updateCustomer(mockCustomer);

        verify(customerMapper, times(1)).updateCustomer(any(Customer.class));
    }

    @Test
    void testUpdateCustomerFailure() {
        when(customerMapper.updateCustomer(any(Customer.class))).thenReturn(0);

        SystemException thrown = assertThrows(SystemException.class,
                () -> customerService.updateCustomer(mockCustomer));
        assertEquals("系統異常請聯繫管理員", thrown.getMessage());
    }

    @Test
    void testDeleteCustomerByIdSuccess() {
        when(customerMapper.deleteCustomerById(anyInt())).thenReturn(1);

        customerService.deleteCustomerById(1);

        verify(customerMapper, times(1)).deleteCustomerById(1);
    }

    @Test
    void testDeleteCustomerByIdFailure() {
        when(customerMapper.deleteCustomerById(anyInt())).thenReturn(0);

        SystemException thrown = assertThrows(SystemException.class,
                () -> customerService.deleteCustomerById(1));
        assertEquals("系統異常請聯繫管理員", thrown.getMessage());
    }
}
