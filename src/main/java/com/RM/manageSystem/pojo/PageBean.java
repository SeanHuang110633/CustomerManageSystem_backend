package com.RM.manageSystem.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//封裝pagehelper分頁查詢返回結果
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBean<T> {
    private Long total;  //資料總數(條)
    private List<T> items; //每頁顯示資料數(條)
}
