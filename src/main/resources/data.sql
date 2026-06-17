-- src/main/resources/data.sql

-- 插入用户数据（如果已存在则忽略）
INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `phone`, `email`, `avatar`) VALUES
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138001', 'zhangsan@example.com', 'https://example.com/avatar1.jpg'),
('lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13800138002', 'lisi@example.com', 'https://example.com/avatar2.jpg'),
('wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13800138003', 'wangwu@example.com', 'https://example.com/avatar3.jpg');

-- -- 插入商品分类数据（如果已存在则忽略）
-- INSERT IGNORE INTO `category` (`name`) VALUES
-- -- 电子产品分类
-- ('手机'),
-- ('电脑'),
-- ('平板'),
-- ('数码配件'),
-- ('智能设备'),
-- ('摄影摄像'),
--
-- -- 服装鞋帽分类
-- ('男装'),
-- ('女装'),
-- ('童装'),
-- ('鞋类'),
-- ('配饰'),
--
-- -- 图书音像分类
-- ('文学小说'),
-- ('科技图书'),
-- ('教育教材'),
-- ('艺术设计'),
-- ('音像制品'),
--
-- -- 家居用品分类
-- ('家具'),
-- ('家纺'),
-- ('厨具'),
-- ('装饰品'),
-- ('灯具'),
--
-- -- 食品饮料分类
-- ('零食'),
-- ('饮料'),
-- ('生鲜'),
-- ('粮油'),
-- ('调味品'),
--
-- -- 运动户外分类
-- ('运动服饰'),
-- ('运动器材'),
-- ('户外装备'),
-- ('健身器材'),


-- -- 插入一些示例商品数据（如果已存在则忽略）
-- INSERT IGNORE INTO `product` (`title`, `description`, `price`, `category`, `category_id`, `status`, `images`, `user_id`) VALUES
-- ('iPhone 15 Pro', '苹果最新款手机，A17芯片，4800万像素', 8999.00, '手机', (SELECT id FROM `category` WHERE name = '手机' LIMIT 1), '出售中', 'iphone1.jpg,iphone2.jpg', 1),
-- ('MacBook Pro 16', '苹果笔记本电脑，M3芯片，16英寸屏幕', 23999.00, '电脑', (SELECT id FROM `category` WHERE name = '电脑' LIMIT 1), '出售中', 'macbook1.jpg,macbook2.jpg', 1),
-- ('华为Mate 60', '华为旗舰手机，麒麟9000S芯片', 6999.00, '手机', (SELECT id FROM `category` WHERE name = '手机' LIMIT 1), '出售中', 'huawei1.jpg,huawei2.jpg', 2),
-- ('小米14 Pro', '小米旗舰手机，徕卡影像系统', 4999.00, '手机', (SELECT id FROM `category` WHERE name = '手机' LIMIT 1), '出售中', 'xiaomi1.jpg,xiaomi2.jpg', 2),
-- ('联想拯救者Y9000P', '游戏笔记本电脑，RTX 4060显卡', 9999.00, '电脑', (SELECT id FROM `category` WHERE name = '电脑' LIMIT 1), '出售中', 'lenovo1.jpg,lenovo2.jpg', 3),
-- ('索尼PS5', '索尼游戏主机，支持4K游戏', 3899.00, '智能设备', (SELECT id FROM `category` WHERE name = '智能设备' LIMIT 1), '出售中', 'ps5.jpg', 1),
-- ('佳能EOS R6', '全画幅微单相机，2010万像素', 15999.00, '摄影摄像', (SELECT id FROM `category` WHERE name = '摄影摄像' LIMIT 1), '出售中', 'camera1.jpg,camera2.jpg', 2),
-- ('耐克Air Force 1', '经典白色运动鞋', 699.00, '鞋类', (SELECT id FROM `category` WHERE name = '鞋类' LIMIT 1), '出售中', 'nike1.jpg,nike2.jpg', 3),
-- ('《三体》全集', '刘慈欣科幻小说三部曲', 128.00, '文学小说', (SELECT id FROM `category` WHERE name = '文学小说' LIMIT 1), '出售中', 'book1.jpg', 1),
-- ('宜家书桌', '简约现代风格书桌', 599.00, '家具', (SELECT id FROM `category` WHERE name = '家具' LIMIT 1), '出售中', 'desk1.jpg,desk2.jpg', 2),
-- ('星巴克咖啡豆', '哥伦比亚进口咖啡豆，中度烘焙', 98.00, '饮料', (SELECT id FROM `category` WHERE name = '饮料' LIMIT 1), '出售中', 'coffee.jpg', 1),
-- ('瑜伽垫', '加厚防滑瑜伽垫，环保材质', 89.00, '健身器材', (SELECT id FROM `category` WHERE name = '健身器材' LIMIT 1), '出售中', 'yoga.jpg', 2),
-- ('兰蔻小黑瓶', '精华肌底液，50ml', 1080.00, '护肤品', (SELECT id FROM `category` WHERE name = '护肤品' LIMIT 1), '出售中', 'lancome.jpg', 3),
-- ('乐高积木', '城市系列消防局套装', 399.00, '玩具', (SELECT id FROM `category` WHERE name = '玩具' LIMIT 1), '出售中', 'lego.jpg', 1);

-- 更新现有商品的category_id（如果之前没有设置）
UPDATE `product` p
SET p.`category_id` = (
    SELECT c.`id`
    FROM `category` c
    WHERE c.`name` = p.`category`
    LIMIT 1
)
WHERE p.`category_id` IS NULL AND p.`category` IS NOT NULL;

-- ========== 插入交易示例数据（如果已存在则忽略） ==========
-- 用户关系：张三(id=1)、李四(id=2)、王五(id=3)
-- 商品关系：id=1(iPhone/张三)、id=2(华为/李四)、id=3(联想/王五)、id=4(小米/李四)、id=5(PS5/张三)

INSERT IGNORE INTO `transaction`
(`transaction_no`, `product_id`, `product_title`, `price`, `seller_id`, `seller_name`, `buyer_id`, `buyer_name`, `status`, `payment_status`, `trading_location`, `payment_time`, `shipping_time`, `receive_time`, `complete_time`, `buyer_remark`, `seller_remark`, `cancel_reason`)
VALUES
-- ===== 待付款 (3条) =====
-- 1. 李四想买张三的iPhone
('T20240601001ABC001', 1, 'iPhone 15 Pro', 8999.00, 1, 'zhangsan', 2, 'lisi', '待付款', '未支付', '图书馆一楼', NULL, NULL, NULL, NULL, '能便宜点吗', NULL, NULL),

-- 6. 王五想买李四的小米手机
('T20240606006PQR006', 4, '小米14 Pro', 4999.00, 2, 'lisi', 3, 'wangwu', '待付款', '未支付', '食堂二楼', NULL, NULL, NULL, NULL, NULL, NULL, NULL),

-- 7. 张三想买王五的联想笔记本（跨卖家购买）
('T20240607007STU007', 3, '联想拯救者Y9000P', 9999.00, 3, 'wangwu', 1, 'zhangsan', '待付款', '未支付', '教学楼B栋', NULL, NULL, NULL, NULL, '希望包装好一点', NULL, NULL),

-- ===== 待发货 (3条) =====
-- 2. 王五买了李四的华为手机，已付款
('T20240602002DEF002', 2, '华为Mate 60', 6999.00, 2, 'lisi', 3, 'wangwu', '待发货', '已支付', '食堂门口', '2024-06-02 14:30:00', NULL, NULL, NULL, NULL, NULL, NULL),

-- 8. 李四买了张三的PS5，已付款
('T20240608008VWX008', 5, '索尼PS5', 3899.00, 1, 'zhangsan', 2, 'lisi', '待发货', '已支付', '体育馆入口', '2024-06-08 10:15:00', NULL, NULL, NULL, '什么时候可以拿', NULL, NULL),

-- 9. 张三买了李四的华为手机，已付款
('T20240609009YZA009', 2, '华为Mate 60', 6999.00, 2, 'lisi', 1, 'zhangsan', '待发货', '已支付', '宿舍楼3栋', '2024-06-09 16:00:00', NULL, NULL, NULL, NULL, NULL, NULL),

-- ===== 待收货 (3条) =====
-- 3. 张三买了王五的联想笔记本，已发货
('T20240603003GHI003', 3, '联想拯救者Y9000P', 9999.00, 3, 'wangwu', 1, 'zhangsan', '待收货', '已支付', '宿舍楼下', '2024-06-03 10:00:00', '2024-06-03 16:00:00', NULL, NULL, NULL, '已发顺丰，注意查收', NULL),

-- 10. 王五买了张三的iPhone，已发货
('T20240610010BCD010', 1, 'iPhone 15 Pro', 8999.00, 1, 'zhangsan', 3, 'wangwu', '待收货', '已支付', '图书馆二楼', '2024-06-10 08:30:00', '2024-06-10 12:00:00', NULL, NULL, NULL, '已发快递，单号SF123456', NULL),

-- 11. 李四买了王五的联想笔记本，已发货
('T20240611011EFG011', 3, '联想拯救者Y9000P', 9999.00, 3, 'wangwu', 2, 'lisi', '待收货', '已支付', '校门口', '2024-06-11 09:00:00', '2024-06-11 14:00:00', NULL, NULL, '麻烦尽快发货谢谢', '今天下午发出', NULL),

-- ===== 已完成 (4条) =====
-- 4. 李四买了张三的小米手机（实际是李四卖给自己的？修正为李四从别处买）
('T20240604004JKL004', 4, '小米14 Pro', 4999.00, 1, 'zhangsan', 2, 'lisi', '已完成', '已支付', '教学楼A栋', '2024-06-04 09:00:00', '2024-06-04 11:00:00', '2024-06-04 15:00:00', '2024-06-04 15:00:00', '东西很好，卖家靠谱', '爽快交易', NULL),

-- 12. 王五买了张三的PS5
('T20240612012HIJ012', 5, '索尼PS5', 3899.00, 1, 'zhangsan', 3, 'wangwu', '已完成', '已支付', '食堂三楼', '2024-06-12 10:00:00', '2024-06-12 11:30:00', '2024-06-12 18:00:00', '2024-06-12 18:00:00', '收到货了，全新未拆封', '合作愉快', NULL),

-- 13. 张三买了李四的华为手机
('T20240613013KLM013', 2, '华为Mate 60', 6999.00, 2, 'lisi', 1, 'zhangsan', '已完成', '已支付', '图书馆三楼', '2024-06-13 08:00:00', '2024-06-13 10:00:00', '2024-06-13 13:00:00', '2024-06-13 13:00:00', NULL, NULL, NULL),

-- 14. 李四买了王五的联想笔记本
('T20240614014NOP014', 3, '联想拯救者Y9000P', 9999.00, 3, 'wangwu', 2, 'lisi', '已完成', '已支付', '教学楼C栋', '2024-06-14 09:30:00', '2024-06-14 12:00:00', '2024-06-15 10:00:00', '2024-06-15 10:00:00', '卖家很耐心解答问题', '欢迎下次再来', NULL),

-- ===== 已取消 (3条) =====
-- 5. 王五想买张三的PS5但取消了
('T20240605005MNO005', 5, '索尼PS5', 3899.00, 1, 'zhangsan', 3, 'wangwu', '已取消', '未支付', '体育馆', NULL, NULL, NULL, NULL, NULL, NULL, '临时不想要了'),

-- 15. 张三想买李四的小米但觉得太贵取消了
('T20240615015QRS015', 4, '小米14 Pro', 4999.00, 2, 'lisi', 1, 'zhangsan', '已取消', '未支付', '食堂一楼', NULL, NULL, NULL, NULL, NULL, NULL, '超出预算，抱歉'),

-- 16. 李四想买王五的联想但找到更便宜的取消了
('T20240616016TUV016', 3, '联想拯救者Y9000P', 9999.00, 3, 'wangwu', 2, 'lisi', '已取消', '未支付', '宿舍楼5栋', NULL, NULL, NULL, NULL, NULL, NULL, '朋友送了一台，不需要了');


-- ========== 插入购物车示例数据（如果已存在则忽略） ==========
-- 用户关系：张三(id=1)、李四(id=2)、王五(id=3)
-- 商品关系：id=1(iPhone/张三)、id=2(华为/李二)、id=3(联想/王五)、id=4(小米/李四)、id=5(PS5/张三)
INSERT IGNORE INTO `cart` (`user_id`, `product_id`, `product_title`, `price`, `product_image`, `seller_id`, `seller_name`, `create_time`, `update_time`, `deleted`) VALUES
-- ===== 张三的购物车 (userId=1) =====
-- 张三把李四的华为手机加入购物车
(1, 2, '华为Mate 60', 6999.00, 'https://example.com/huawei.jpg', 2, 'lisi', '2024-06-10 10:30:00', '2024-06-10 10:30:00', 0),
-- 张三把王五的联想笔记本加入购物车
(1, 3, '联想拯救者Y9000P', 9999.00, 'https://example.com/lenovo.jpg', 3, 'wangwu', '2024-06-11 14:20:00', '2024-06-11 14:20:00', 0),

-- ===== 李四的购物车 (userId=2) =====
-- 李四把张三的iPhone加入购物车
(2, 1, 'iPhone 15 Pro', 8999.00, 'https://example.com/iphone.jpg', 1, 'zhangsan', '2024-06-12 09:15:00', '2024-06-12 09:15:00', 0),
-- 李四把王五的联想笔记本加入购物车
(2, 3, '联想拯救者Y9000P', 9999.00, 'https://example.com/lenovo.jpg', 3, 'wangwu', '2024-06-13 16:45:00', '2024-06-13 16:45:00', 0),

-- ===== 王五的购物车 (userId=3) =====
-- 王五把张三的iPhone加入购物车
(3, 1, 'iPhone 15 Pro', 8999.00, 'https://example.com/iphone.jpg', 1, 'zhangsan', '2024-06-14 11:00:00', '2024-06-14 11:00:00', 0),
-- 王五把李四的小米手机加入购物车
(3, 4, '小米14 Pro', 4999.00, 'https://example.com/xiaomi.jpg', 2, 'lisi', '2024-06-15 13:30:00', '2024-06-15 13:30:00', 0),
-- 王五把张三的PS5加入购物车
(3, 5, '索尼PS5', 3899.00, 'https://example.com/ps5.jpg', 1, 'zhangsan', '2024-06-16 15:20:00', '2024-06-16 15:20:00', 0);







