package com.jypt.controller;

import com.jypt.common.Result;
import com.jypt.entity.Transaction;
import com.jypt.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 交易控制器
 * 统一使用 Result 响应格式，与 ProductController、UserController 保持一致
 */
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /** 创建新交易 */
    @PostMapping("/create")
    public Result<?> createTransaction(@RequestBody Transaction transaction) {
        Transaction created = transactionService.createTransaction(transaction);
        if (created != null) {
            return Result.success(created);
        }
        return Result.error("创建交易失败");
    }

    /** 根据ID获取交易 */
    @GetMapping("/{id}")
    public Result<?> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getById(id);
        if (transaction != null) {
            return Result.success(transaction);
        }
        return Result.error("交易不存在");
    }

    /** 根据交易编号获取交易 */
    @GetMapping("/no/{transactionNo}")
    public Result<?> getTransactionByNo(@PathVariable String transactionNo) {
        Transaction transaction = transactionService.getTransactionByNo(transactionNo);
        if (transaction != null) {
            return Result.success(transaction);
        }
        return Result.error("交易不存在");
    }

    /** 获取所有交易 */
    @GetMapping("/all")
    public Result<?> getAllTransactions() {
        List<Transaction> transactions = transactionService.list();
        return Result.success(transactions);
    }

    /** 获取买家交易列表 */
    @GetMapping("/buyer/{buyerId}")
    public Result<?> getTransactionsByBuyerId(@PathVariable Long buyerId) {
        List<Transaction> transactions = transactionService.getTransactionsByBuyerId(buyerId);
        return Result.success(transactions);
    }

    /** 获取卖家交易列表 */
    @GetMapping("/seller/{sellerId}")
    public Result<?> getTransactionsBySellerId(@PathVariable Long sellerId) {
        List<Transaction> transactions = transactionService.getTransactionsBySellerId(sellerId);
        return Result.success(transactions);
    }

    /** 根据商品ID获取交易 */
    @GetMapping("/product/{productId}")
    public Result<?> getTransactionsByProductId(@PathVariable Long productId) {
        List<Transaction> transactions = transactionService.getTransactionsByProductId(productId);
        return Result.success(transactions);
    }

    /** 根据状态获取交易 */
    @GetMapping("/status/{status}")
    public Result<?> getTransactionsByStatus(@PathVariable String status) {
        List<Transaction> transactions = transactionService.getTransactionsByStatus(status);
        return Result.success(transactions);
    }

    /** 根据支付状态获取交易 */
    @GetMapping("/payment-status/{paymentStatus}")
    public Result<?> getTransactionsByPaymentStatus(@PathVariable String paymentStatus) {
        List<Transaction> transactions = transactionService.getTransactionsByPaymentStatus(paymentStatus);
        return Result.success(transactions);
    }

    /** 更新交易状态 */
    @PutMapping("/status/{id}")
    public Result<?> updateTransactionStatus(@PathVariable Long id,
                                             @RequestBody Map<String, String> request) {
        Transaction updated = transactionService.updateTransactionStatus(id, request.get("status"));
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("更新状态失败");
    }

    /** 更新支付状态 */
    @PutMapping("/payment-status/{id}")
    public Result<?> updatePaymentStatus(@PathVariable Long id,
                                         @RequestBody Map<String, String> request) {
        Transaction updated = transactionService.updatePaymentStatus(id, request.get("paymentStatus"));
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("更新支付状态失败");
    }

    /** 更新发货信息 */
    @PutMapping("/shipping/{id}")
    public Result<?> updateShippingInfo(@PathVariable Long id,
                                        @RequestBody Map<String, String> request) {
        Transaction updated = transactionService.updateShippingInfo(
                id, request.get("shippingTime"), request.get("sellerRemark"));
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("更新发货信息失败");
    }

    /** 更新收货信息 */
    @PutMapping("/receive/{id}")
    public Result<?> updateReceiveInfo(@PathVariable Long id,
                                       @RequestBody Map<String, String> request) {
        Transaction updated = transactionService.updateReceiveInfo(
                id, request.get("receiveTime"), request.get("buyerRemark"));
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("更新收货信息失败");
    }

    /** 取消交易 */
    @PutMapping("/cancel/{id}")
    public Result<?> cancelTransaction(@PathVariable Long id,
                                       @RequestBody Map<String, String> request) {
        Transaction updated = transactionService.cancelTransaction(id, request.get("cancelReason"));
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("取消交易失败");
    }

    /** 完成交易 */
    @PutMapping("/complete/{id}")
    public Result<?> completeTransaction(@PathVariable Long id) {
        Transaction updated = transactionService.completeTransaction(id);
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("完成交易失败");
    }

    /** 更新交易信息 */
    @PutMapping("/update")
    public Result<?> updateTransaction(@RequestBody Transaction transaction) {
        Transaction updated = transactionService.updateTransaction(transaction);
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("更新失败");
    }

    /** 删除交易 */
    @DeleteMapping("/{id}")
    public Result<?> deleteTransaction(@PathVariable Long id) {
        boolean success = transactionService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /** 搜索交易 */
    @GetMapping("/search")
    public Result<?> searchTransactions(@RequestParam(required = false) Long buyerId,
                                        @RequestParam(required = false) Long sellerId,
                                        @RequestParam(required = false) Long productId,
                                        @RequestParam(required = false) String status,
                                        @RequestParam(required = false) String paymentStatus,
                                        @RequestParam(required = false) String keyword) {
        List<Transaction> transactions = transactionService.searchTransactions(
                buyerId, sellerId, productId, status, paymentStatus, keyword);
        return Result.success(transactions);
    }

    /** 买家交易统计 */
    @GetMapping("/buyer/{buyerId}/stats")
    public Result<?> getBuyerTransactionStats(@PathVariable Long buyerId) {
        Map<String, Object> stats = transactionService.getBuyerTransactionStats(buyerId);
        return Result.success(stats);
    }

    /** 卖家交易统计 */
    @GetMapping("/seller/{sellerId}/stats")
    public Result<?> getSellerTransactionStats(@PathVariable Long sellerId) {
        Map<String, Object> stats = transactionService.getSellerTransactionStats(sellerId);
        return Result.success(stats);
    }

    /** 按价格区间查询 */
    @GetMapping("/price-range")
    public Result<?> getTransactionsByPriceRange(@RequestParam(required = false) BigDecimal minPrice,
                                                  @RequestParam(required = false) BigDecimal maxPrice) {
        List<Transaction> transactions = transactionService.getTransactionsByPriceRange(minPrice, maxPrice);
        return Result.success(transactions);
    }

    /** 按时间区间查询 */
    @GetMapping("/time-range")
    public Result<?> getTransactionsByTimeRange(@RequestParam(required = false) String startTime,
                                                 @RequestParam(required = false) String endTime) {
        List<Transaction> transactions = transactionService.getTransactionsByTimeRange(startTime, endTime);
        return Result.success(transactions);
    }
}
