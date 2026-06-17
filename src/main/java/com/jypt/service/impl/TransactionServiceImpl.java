package com.jypt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jypt.entity.Product;
import com.jypt.entity.Transaction;
import com.jypt.mapper.TransactionMapper;
import com.jypt.service.TransactionService;
import com.jypt.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class TransactionServiceImpl
        extends ServiceImpl<TransactionMapper, Transaction>
        implements TransactionService {

    @Autowired
    private ProductService productService;

    @Override
    public Transaction createTransaction(Transaction transaction) {
        // 校验商品是否存在且为"出售中"状态
        Product product = productService.getById(transaction.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在或已下架");
        }
        if (!"出售中".equals(product.getStatus())) {
            throw new RuntimeException("该商品已被他人购买，无法下单");
        }
        // 生成唯一交易编号
        String transactionNo = "T" + System.currentTimeMillis()
                + UUID.randomUUID().toString().substring(0, 8);
        transaction.setTransactionNo(transactionNo);
        // 初始状态
        transaction.setStatus("待付款");
        transaction.setPaymentStatus("未支付");
        // this.save() 来自 ServiceImpl
        this.save(transaction);
        // 商品状态改为已售出
        productService.updateProductStatus(transaction.getProductId(), "已售出");
        return transaction;
    }

    @Override
    public Transaction getTransactionByNo(String transactionNo) {
        // LambdaQueryWrapper 链式查询，替代手写 SQL
        Transaction transaction = this.lambdaQuery()
                .eq(Transaction::getTransactionNo, transactionNo)
                .one();
        if (transaction == null) {
            throw new RuntimeException("交易不存在，交易编号: " + transactionNo);
        }
        return transaction;
    }

    // ========== 按条件查询交易列表（全部使用 LambdaQueryWrapper） ==========

    @Override
    public List<Transaction> getTransactionsByBuyerId(Long buyerId) {
        return this.lambdaQuery()
                .eq(Transaction::getBuyerId, buyerId)
                .orderByDesc(Transaction::getCreateTime)
                .list();
    }

    @Override
    public List<Transaction> getTransactionsBySellerId(Long sellerId) {
        return this.lambdaQuery()
                .eq(Transaction::getSellerId, sellerId)
                .orderByDesc(Transaction::getCreateTime)
                .list();
    }

    @Override
    public List<Transaction> getTransactionsByProductId(Long productId) {
        return this.lambdaQuery()
                .eq(Transaction::getProductId, productId)
                .orderByDesc(Transaction::getCreateTime)
                .list();
    }

    @Override
    public List<Transaction> getTransactionsByStatus(String status) {
        return this.lambdaQuery()
                .eq(Transaction::getStatus, status)
                .orderByDesc(Transaction::getCreateTime)
                .list();
    }

    @Override
    public List<Transaction> getTransactionsByPaymentStatus(String paymentStatus) {
        return this.lambdaQuery()
                .eq(Transaction::getPaymentStatus, paymentStatus)
                .orderByDesc(Transaction::getCreateTime)
                .list();
    }

    // ========== 状态流转操作 ==========

    @Override
    public Transaction updateTransactionStatus(Long id, String status) {
        Transaction transaction = this.getById(id);
        if (transaction == null) {
            throw new RuntimeException("交易不存在，ID: " + id);
        }
        transaction.setStatus(status);
        // 状态变更时自动记录对应时间
        if ("待发货".equals(status)) {
            transaction.setPaymentTime(LocalDateTime.now());
        } else if ("待收货".equals(status)) {
            transaction.setShippingTime(LocalDateTime.now());
        } else if ("已完成".equals(status)) {
            transaction.setCompleteTime(LocalDateTime.now());
        }
        this.updateById(transaction);
        return transaction;
    }

    @Override
    public Transaction updatePaymentStatus(Long id, String paymentStatus) {
        Transaction transaction = this.getById(id);
        if (transaction == null) {
            throw new RuntimeException("交易不存在，ID: " + id);
        }
        transaction.setPaymentStatus(paymentStatus);
        if ("已支付".equals(paymentStatus)) {
            transaction.setPaymentTime(LocalDateTime.now());
            transaction.setStatus("待发货");     // 付款后自动进入待发货
        }
        this.updateById(transaction);
        return transaction;
    }

    @Override
    public Transaction updateShippingInfo(Long id, String shippingTime, String sellerRemark) {
        Transaction transaction = this.getById(id);
        if (transaction == null) {
            throw new RuntimeException("交易不存在，ID: " + id);
        }
        if (shippingTime != null) {
            transaction.setShippingTime(
                    LocalDateTime.parse(shippingTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        if (sellerRemark != null) {
            transaction.setSellerRemark(sellerRemark);
        }
        transaction.setStatus("待收货");
        this.updateById(transaction);
        return transaction;
    }

    @Override
    public Transaction updateReceiveInfo(Long id, String receiveTime, String buyerRemark) {
        Transaction transaction = this.getById(id);
        if (transaction == null) {
            throw new RuntimeException("交易不存在，ID: " + id);
        }
        if (receiveTime != null) {
            transaction.setReceiveTime(
                    LocalDateTime.parse(receiveTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        if (buyerRemark != null) {
            transaction.setBuyerRemark(buyerRemark);
        }
        transaction.setStatus("已完成");
        transaction.setCompleteTime(LocalDateTime.now());
        this.updateById(transaction);
        return transaction;
    }

    @Override
    public Transaction cancelTransaction(Long id, String cancelReason) {
        Transaction transaction = this.getById(id);
        if (transaction == null) {
            throw new RuntimeException("交易不存在，ID: " + id);
        }
        transaction.setStatus("已取消");
        transaction.setCancelReason(cancelReason);
        this.updateById(transaction);
        // 取消交易后恢复商品状态为出售中
        productService.updateProductStatus(transaction.getProductId(), "出售中");
        return transaction;
    }

    @Override
    public Transaction completeTransaction(Long id) {
        Transaction transaction = this.getById(id);
        if (transaction == null) {
            throw new RuntimeException("交易不存在，ID: " + id);
        }
        transaction.setStatus("已完成");
        transaction.setCompleteTime(LocalDateTime.now());
        this.updateById(transaction);
        return transaction;
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {
        Transaction existing = this.getById(transaction.getId());
        if (existing == null) {
            throw new RuntimeException("交易不存在，ID: " + transaction.getId());
        }
        // 只允许修改部分字段
        if (transaction.getTradingLocation() != null) {
            existing.setTradingLocation(transaction.getTradingLocation());
        }
        if (transaction.getBuyerRemark() != null) {
            existing.setBuyerRemark(transaction.getBuyerRemark());
        }
        if (transaction.getSellerRemark() != null) {
            existing.setSellerRemark(transaction.getSellerRemark());
        }
        this.updateById(existing);
        return existing;
    }

    // ========== 动态搜索 ==========

    @Override
    public List<Transaction> searchTransactions(Long buyerId, Long sellerId, Long productId,
                                                String status, String paymentStatus, String keyword) {
        // 用 LambdaQueryWrapper 动态构建查询条件，替代手写动态 SQL
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        if (buyerId != null) {
            wrapper.eq(Transaction::getBuyerId, buyerId);
        }
        if (sellerId != null) {
            wrapper.eq(Transaction::getSellerId, sellerId);
        }
        if (productId != null) {
            wrapper.eq(Transaction::getProductId, productId);
        }
        if (status != null) {
            wrapper.eq(Transaction::getStatus, status);
        }
        if (paymentStatus != null) {
            wrapper.eq(Transaction::getPaymentStatus, paymentStatus);
        }
        if (keyword != null) {
            wrapper.and(w -> w
                    .like(Transaction::getProductTitle, keyword)
                    .or()
                    .like(Transaction::getTransactionNo, keyword));
        }
        wrapper.orderByDesc(Transaction::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Transaction> getTransactionsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        if (minPrice != null) {
            wrapper.ge(Transaction::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Transaction::getPrice, maxPrice);
        }
        wrapper.orderByAsc(Transaction::getPrice);
        return this.list(wrapper);
    }

    @Override
    public List<Transaction> getTransactionsByTimeRange(String startTime, String endTime) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        if (startTime != null) {
            wrapper.ge(Transaction::getCreateTime,
                    LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        if (endTime != null) {
            wrapper.le(Transaction::getCreateTime,
                    LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        wrapper.orderByDesc(Transaction::getCreateTime);
        return this.list(wrapper);
    }

    // ========== 统计 ==========

    @Override
    public Map<String, Object> getBuyerTransactionStats(Long buyerId) {
        List<Transaction> transactions = getTransactionsByBuyerId(buyerId);
        return buildStats(transactions, "buyer");
    }

    @Override
    public Map<String, Object> getSellerTransactionStats(Long sellerId) {
        List<Transaction> transactions = getTransactionsBySellerId(sellerId);
        return buildStats(transactions, "seller");
    }

    /** 通用的统计计算 */
    private Map<String, Object> buildStats(List<Transaction> transactions, String role) {
        Map<String, Object> stats = new HashMap<>();
        long total = transactions.size();
        long pendingPayment = transactions.stream()
                .filter(t -> "待付款".equals(t.getStatus())).count();
        long pendingShipping = transactions.stream()
                .filter(t -> "待发货".equals(t.getStatus())).count();
        long pendingReceive = transactions.stream()
                .filter(t -> "待收货".equals(t.getStatus())).count();
        long completed = transactions.stream()
                .filter(t -> "已完成".equals(t.getStatus())).count();
        long cancelled = transactions.stream()
                .filter(t -> "已取消".equals(t.getStatus())).count();

        BigDecimal totalAmount = transactions.stream()
                .filter(t -> "已完成".equals(t.getStatus()))
                .map(Transaction::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalTransactions", total);
        stats.put("pendingPayment", pendingPayment);
        if ("seller".equals(role)) {
            stats.put("pendingShipping", pendingShipping);
        } else {
            stats.put("pendingReceive", pendingReceive);
        }
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);
        stats.put("totalAmount", totalAmount);
        stats.put("completionRate", total > 0 ? (double) completed / total * 100 : 0);
        return stats;
    }
}
