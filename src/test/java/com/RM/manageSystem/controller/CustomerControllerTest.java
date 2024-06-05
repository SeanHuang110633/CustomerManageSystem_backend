package com.RM.manageSystem.controller;


import com.RM.manageSystem.pojo.Customer;
import com.RM.manageSystem.pojo.PageBean;
import com.RM.manageSystem.pojo.User;
import com.RM.manageSystem.service.CustomerService;
import com.RM.manageSystem.utils.JWTUtil;
import com.RM.manageSystem.utils.MD5Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@ExtendWith(SpringExtension.class)
public class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;


    private User mockUser;
    private String token;
    private Customer mockCustomer;
    private PageBean<Customer> mockPageBean;

    @BeforeEach
    void setUp() {
        // 模擬Redis
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // 設置測試用的user並產生JWT token
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("mockUser");
        HashMap<String, Object> mockClaims = new HashMap<>();
        mockClaims.put("id", mockUser.getId());
        mockClaims.put("username", mockUser.getUsername());
        token = JWTUtil.genToken(mockClaims);

        //設置測試用的customer
        mockCustomer = Customer.builder()
                .id(1)
                .customerName("Sean")
                .gender('M')
                .phoneNumber("0909000000")
                .firstLesson(LocalDate.of(2024, 5, 29))
                .lastLesson(LocalDate.of(2024, 5, 29))
                .coachId(1)
                .build();

        //設置測試用的pageBean<customer>
        List<Customer> list = new ArrayList<>();
        list.add(mockCustomer);
        mockPageBean = new PageBean<>();
        mockPageBean.setItems(list);
        mockPageBean.setTotal(1L);


        when(valueOperations.get(anyString())).thenReturn(token);

    }

    @Test
    public void testListCustomers_withoutCondition() throws Exception {
        when(customerService.listCustomers(anyInt(), anyInt(), any(), isNull())).thenReturn(mockPageBean);
        mockMvc.perform(get("/customer")
                        .header("Authorization", token)
                        .param("pageNum", "1")
                        .param("pageSize", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(customerService, times(1)).listCustomers(1, 3, null, null);
    }

    @Test
    public void testAddCustomer() throws Exception {
        doNothing().when(customerService).addCustomer(mockCustomer);

        // 創建 ObjectMapper 並註冊 JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 將用戶對象轉換為 JSON 字符串
        String userJson = objectMapper.writeValueAsString(mockCustomer);

        mockMvc.perform(post("/customer")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20010))
                .andExpect(jsonPath("$.message").value("新增客戶成功"));

        verify(customerService, times(1)).addCustomer(mockCustomer);
    }


    @Test
    public void testUpdateCustomer() throws Exception {
        mockCustomer.setEmail("wl@gmail.com");
        doNothing().when(customerService).updateCustomer(any(Customer.class));

        // 創建 ObjectMapper 並註冊 JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 將用戶對象轉換為 JSON 字符串
        String userJson = objectMapper.writeValueAsString(mockCustomer);

        mockMvc.perform(put("/customer")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20010))
                .andExpect(jsonPath("$.message").value("更新客戶資料成功"));

        verify(customerService, times(1)).updateCustomer(any(Customer.class));
    }


    @Test
    public void testDeleteCustomer() throws Exception {
        doNothing().when(customerService).deleteCustomerById(anyInt());

        mockMvc.perform(delete("/customer")
                        .header("Authorization", token)
                        .param("id", String.valueOf(mockCustomer.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(20010))
                .andExpect(jsonPath("$.message").value("刪除客戶成功"));

        verify(customerService, times(1)).deleteCustomerById(anyInt());
    }
}
