package com.jypt.mapper;

import com.jypt.entity.Transaction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 交易 Mapper
 * 继承 BaseMapper 即可获得所有基础 CRUD，无需手写 SQL
 */
@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
}
