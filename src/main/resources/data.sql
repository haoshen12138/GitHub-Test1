-- src/main/resources/data.sql

-- 插入用户数据（如果已存在则忽略）
INSERT IGNORE INTO `user` (`username`, `password`, `nickname`, `phone`, `email`, `avatar`) VALUES
('zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138001', 'zhangsan@example.com', 'https://example.com/avatar1.jpg'),
('lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13800138002', 'lisi@example.com', 'https://example.com/avatar2.jpg'),
('wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13800138003', 'wangwu@example.com', 'https://example.com/avatar3.jpg');

-- 插入商品分类数据（如果已存在则忽略）
INSERT IGNORE INTO `category` (`name`) VALUES
-- 电子产品分类
('手机'),
('电脑'),
('平板'),
('数码配件'),
('智能设备'),
('摄影摄像'),

-- 服装鞋帽分类
('男装'),
('女装'),
('童装'),
('鞋类'),
('配饰'),

-- 图书音像分类
('文学小说'),
('科技图书'),
('教育教材'),
('艺术设计'),
('音像制品'),

-- 家居用品分类
('家具'),
('家纺'),
('厨具'),
('装饰品'),
('灯具'),

-- 食品饮料分类
('零食'),
('饮料'),
('生鲜'),
('粮油'),
('调味品'),

-- 运动户外分类
('运动服饰'),
('运动器材'),
('户外装备'),
('健身器材'),

-- 美妆个护分类
('护肤品'),
('化妆品'),
('个人护理'),
('香水'),

-- 母婴玩具分类
('母婴用品'),
('童车童床'),
('玩具'),
('学习用品'),

-- 其他常用分类
('汽车用品'),
('办公用品'),
('宠物用品'),
('乐器'),
('珠宝首饰'),
('钟表眼镜'),
('箱包'),
('文具'),
('绿植园艺'),
('二手闲置');

-- 插入一些示例商品数据（如果已存在则忽略）
INSERT IGNORE INTO `product` (`title`, `description`, `price`, `category`, `category_id`, `status`, `images`, `user_id`) VALUES
('iPhone 15 Pro', '苹果最新款手机，A17芯片，4800万像素', 8999.00, '手机', (SELECT id FROM `category` WHERE name = '手机' LIMIT 1), '出售中', 'iphone1.jpg,iphone2.jpg', 1),
('MacBook Pro 16', '苹果笔记本电脑，M3芯片，16英寸屏幕', 23999.00, '电脑', (SELECT id FROM `category` WHERE name = '电脑' LIMIT 1), '出售中', 'macbook1.jpg,macbook2.jpg', 1),
('华为Mate 60', '华为旗舰手机，麒麟9000S芯片', 6999.00, '手机', (SELECT id FROM `category` WHERE name = '手机' LIMIT 1), '出售中', 'huawei1.jpg,huawei2.jpg', 2),
('小米14 Pro', '小米旗舰手机，徕卡影像系统', 4999.00, '手机', (SELECT id FROM `category` WHERE name = '手机' LIMIT 1), '出售中', 'xiaomi1.jpg,xiaomi2.jpg', 2),
('联想拯救者Y9000P', '游戏笔记本电脑，RTX 4060显卡', 9999.00, '电脑', (SELECT id FROM `category` WHERE name = '电脑' LIMIT 1), '出售中', 'lenovo1.jpg,lenovo2.jpg', 3),
('索尼PS5', '索尼游戏主机，支持4K游戏', 3899.00, '智能设备', (SELECT id FROM `category` WHERE name = '智能设备' LIMIT 1), '出售中', 'ps5.jpg', 1),
('佳能EOS R6', '全画幅微单相机，2010万像素', 15999.00, '摄影摄像', (SELECT id FROM `category` WHERE name = '摄影摄像' LIMIT 1), '出售中', 'camera1.jpg,camera2.jpg', 2),
('耐克Air Force 1', '经典白色运动鞋', 699.00, '鞋类', (SELECT id FROM `category` WHERE name = '鞋类' LIMIT 1), '出售中', 'nike1.jpg,nike2.jpg', 3),
('《三体》全集', '刘慈欣科幻小说三部曲', 128.00, '文学小说', (SELECT id FROM `category` WHERE name = '文学小说' LIMIT 1), '出售中', 'book1.jpg', 1),
('宜家书桌', '简约现代风格书桌', 599.00, '家具', (SELECT id FROM `category` WHERE name = '家具' LIMIT 1), '出售中', 'desk1.jpg,desk2.jpg', 2),
('星巴克咖啡豆', '哥伦比亚进口咖啡豆，中度烘焙', 98.00, '饮料', (SELECT id FROM `category` WHERE name = '饮料' LIMIT 1), '出售中', 'coffee.jpg', 1),
('瑜伽垫', '加厚防滑瑜伽垫，环保材质', 89.00, '健身器材', (SELECT id FROM `category` WHERE name = '健身器材' LIMIT 1), '出售中', 'yoga.jpg', 2),
('兰蔻小黑瓶', '精华肌底液，50ml', 1080.00, '护肤品', (SELECT id FROM `category` WHERE name = '护肤品' LIMIT 1), '出售中', 'lancome.jpg', 3),
('乐高积木', '城市系列消防局套装', 399.00, '玩具', (SELECT id FROM `category` WHERE name = '玩具' LIMIT 1), '出售中', 'lego.jpg', 1);

-- 更新现有商品的category_id（如果之前没有设置）
UPDATE `product` p
SET p.`category_id` = (
    SELECT c.`id`
    FROM `category` c
    WHERE c.`name` = p.`category`
    LIMIT 1
)
WHERE p.`category_id` IS NULL AND p.`category` IS NOT NULL;
