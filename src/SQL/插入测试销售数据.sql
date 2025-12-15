-- 插入1条销售单（admin操作）
INSERT INTO sale_order (user_id, total_amount, pay_type, remark) VALUES 
(1, 108.80, '微信', '门店零售');

-- 插入销售明细（关联上面的销售单）
INSERT INTO sale_item (order_id, product_id, sale_quantity, sale_price, subtotal) VALUES 
(1, 1, 2, 3.20, 6.40),  -- 可口可乐2瓶
(1, 5, 10, 1.20, 12.00), -- 农夫山泉10瓶
(1, 12, 5, 4.20, 21.00), -- 乐事薯片5包
(1, 28, 2, 17.90, 35.80), -- 维达抽纸2提
(1, 40, 1, 79.90, 79.90); -- 金龙鱼大米1袋（这里小计总和和总金额不一致，仅测试，实际需代码计算）

-- 插入库存变动日志（销售出库）
INSERT INTO stock_log (product_id, change_type, change_quantity, before_stock, after_stock, operator_id, remark) VALUES 
(1, '销售出库', -2, 200, 198, 1, '销售单ID：1'),
(5, '销售出库', -10, 500, 490, 1, '销售单ID：1'),
(12, '销售出库', -5, 100, 95, 1, '销售单ID：1'),
(28, '销售出库', -2, 80, 78, 1, '销售单ID：1'),
(40, '销售出库', -1, 40, 39, 1, '销售单ID：1');

-- 插入仓库调拨数据
INSERT INTO warehouse_transfer (from_warehouse_id, to_warehouse_id, product_id, transfer_quantity, operator_id, status) VALUES 
(1, 2, 1, 50, 1, '已完成'), -- 总仓调拨50瓶可口可乐到朝阳仓
(1, 3, 5, 100, 1, '已完成'); -- 总仓调拨100瓶农夫山泉到浦东仓