package com.RM.manageSystem.mapper;

import com.RM.manageSystem.pojo.OperationLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationMapper {

    /**
     * 新增系統紀錄
     *
     * @param log 系統紀錄訊息
     */
    @Insert("insert into operation_log (operation_user,operation_time,operation_class_name, " +
            "operation_function_name,operation_params,operation_return_value,operation_duration) "+
            "values(#{operationUser},#{operationTime},#{operationClassName},#{operationFunctionName}, " +
            "#{operationParams},#{operationReturnValue},#{operationDuration})")
    void insert(OperationLog log);
}
