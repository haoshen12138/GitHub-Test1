package com.jypt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jypt.entity.Transaction;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 交易服务接口
 * 继承 IService 获得 MyBatis-Plus 标准 CRUD 方法
 */
public interface TransactionService extends IService<Transaction> {

    /** 创建交易，自动生成交易编号和初始状态 */
    Transaction createTransaction(Transaction transaction);

    /** 根据交易编号查询 */
    Transaction getTransactionByNo(String transactionNo);

    /** 更新交易状态（自动记录对应时间点） */
    Transaction updateTransactionStatus(Long id, String status);

    /** 更新支付状态 */
    Transaction updatePaymentStatus(Long id, String paymentStatus);

    /** 更新发货信息 */
    Transaction updateShippingInfo(Long id, String shippingTime, String sellerRemark);

    /** 更新收货信息 */
    Transaction updateReceiveInfo(Long id, String receiveTime, String buyerRemark);

    /** 取消交易 */
    Transaction cancelTransaction(Long id, String cancelReason);

    /** 完成交易 */
    Transaction completeTransaction(Long id);

    /** 更新交易可修改字段（交易地点、备注等） */
    Transaction updateTransaction(Transaction transaction);

    /** 动态搜索交易 */
    List<Transaction> searchTransactions(Long buyerId, Long sellerId, Long productId,
                                        String status, String paymentStatus, String keyword);

    /** 买家交易统计 */
    Map<String, Object> getBuyerTransactionStats(Long buyerId);

    /** 卖家交易统计 */
    Map<String, Object> getSellerTransactionStats(Long sellerId);

    /** 按价格区间查询 */
    List<Transaction> getTransactionsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /** 按时间区间查询 */
    List<Transaction> getTransactionsByTimeRange(String startTime, String endTime);

    /** 根据买家ID查询交易列表 */
    List<Transaction> getTransactionsByBuyerId(Long buyerId);

    /** 根据卖家ID查询交易列表 */
    List<Transaction> getTransactionsBySellerId(Long sellerId);

    /** 根据商品ID查询交易列表 */
    List<Transaction> getTransactionsByProductId(Long productId);

    /** 根据状态查询交易列表 */
    List<Transaction> getTransactionsByStatus(String status);

    /** 根据支付状态查询交易列表 */
    List<Transaction> getTransactionsByPaymentStatus(String paymentStatus);

}
