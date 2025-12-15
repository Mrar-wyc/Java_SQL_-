-- ===================== 基础字典表（无外键，先创建） =====================
-- 1. 用户权限表（区分管理员/普通操作员）
CREATE TABLE IF NOT EXISTS user_role (
    role_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(20) NOT NULL UNIQUE COMMENT '角色名称：admin/operator',
    role_desc VARCHAR(100) COMMENT '角色描述：管理员（全权限）/操作员（仅销售/查询）'
);

-- 2. 商品分类表（统一管理分类，避免重复）
CREATE TABLE IF NOT EXISTS product_category (
    category_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    category_name VARCHAR(30) NOT NULL UNIQUE COMMENT '分类名称：饮料/零食/日用品/生鲜/粮油',
    parent_id INT DEFAULT 0 COMMENT '父分类ID（0=一级分类）',
    sort INT DEFAULT 0 COMMENT '排序权重'
);

-- 3. 供应商表（统一管理供应商信息）
CREATE TABLE IF NOT EXISTS supplier (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '供应商ID',
    supplier_name VARCHAR(50) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(20) COMMENT '联系人',
    phone VARCHAR(20) COMMENT '联系电话',
    address VARCHAR(200) COMMENT '地址',
    email VARCHAR(50) COMMENT '邮箱',
    is_valid TINYINT(1) DEFAULT 1 COMMENT '是否有效：1=是，0=否'
);

-- 4. 仓库表（多仓库管理）
CREATE TABLE IF NOT EXISTS warehouse (
    warehouse_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '仓库ID',
    warehouse_name VARCHAR(50) NOT NULL COMMENT '仓库名称：总仓/北京朝阳仓/上海浦东仓',
    address VARCHAR(200) COMMENT '仓库地址',
    manager VARCHAR(20) COMMENT '仓库管理员',
    phone VARCHAR(20) COMMENT '仓库电话'
);

-- ===================== 核心业务表（带外键，后创建） =====================
-- 5. 管理员/操作员表（关联角色）
CREATE TABLE IF NOT EXISTS sys_user (
    user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(20) NOT NULL UNIQUE COMMENT '登录名',
    password VARCHAR(50) NOT NULL COMMENT '密码（简单版，实际项目需加密）',
    real_name VARCHAR(20) COMMENT '真实姓名',
    role_id INT COMMENT '角色ID',
    is_valid TINYINT(1) DEFAULT 1 COMMENT '是否有效：1=是，0=否',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (role_id) REFERENCES user_role(role_id)
);

-- 6. 商品表（核心，关联分类/供应商/仓库）
CREATE TABLE IF NOT EXISTS product (
    product_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    category_id INT COMMENT '分类ID',
    supplier_id INT COMMENT '供应商ID',
    warehouse_id INT COMMENT '所属仓库ID',
    price DECIMAL(10,2) NOT NULL COMMENT '原价',
    discount_price DECIMAL(10,2) DEFAULT 0.00 COMMENT '折扣价',
    stock INT DEFAULT 0 COMMENT '库存数量',
    bar_code VARCHAR(30) UNIQUE COMMENT '条形码（唯一）',
    production_date DATE COMMENT '生产日期',
    shelf_life_days INT DEFAULT 365 COMMENT '保质期（天）',
    expiry_date DATE COMMENT '保质期截止日期',
    is_on_sale TINYINT(1) DEFAULT 1 COMMENT '是否上架：1=是，0=否',
    purchase_price DECIMAL(10,2) COMMENT '采购价（成本）',
    unit VARCHAR(10) DEFAULT '件' COMMENT '单位：件/瓶/包/kg',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (category_id) REFERENCES product_category(category_id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouse(warehouse_id)
);

-- 7. 销售单表（主表，记录销售单整体信息）
CREATE TABLE IF NOT EXISTS sale_order (
    order_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '销售单ID',
    user_id INT COMMENT '操作员ID（谁卖的）',
    sale_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '销售时间',
    total_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '销售总金额',
    pay_type VARCHAR(10) DEFAULT '现金' COMMENT '支付方式：现金/微信/支付宝',
    remark VARCHAR(200) COMMENT '备注',
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id)
);

-- 8. 销售明细表（子表，记录单条商品销售）
CREATE TABLE IF NOT EXISTS sale_item (
    item_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    order_id INT COMMENT '销售单ID',
    product_id INT COMMENT '商品ID',
    sale_quantity INT NOT NULL COMMENT '销售数量',
    sale_price DECIMAL(10,2) NOT NULL COMMENT '销售单价',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计（数量×单价）',
    FOREIGN KEY (order_id) REFERENCES sale_order(order_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- 9. 库存变动日志（溯源：谁、何时、为何变动库存）
CREATE TABLE IF NOT EXISTS stock_log (
    log_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    product_id INT COMMENT '商品ID',
    change_type VARCHAR(20) NOT NULL COMMENT '变动类型：采购入库/销售出库/调拨/盘点/报损',
    change_quantity INT NOT NULL COMMENT '变动数量（正数=入库，负数=出库）',
    before_stock INT NOT NULL COMMENT '变动前库存',
    after_stock INT NOT NULL COMMENT '变动后库存',
    operator_id INT COMMENT '操作员ID',
    change_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '变动时间',
    remark VARCHAR(200) COMMENT '备注',
    FOREIGN KEY (product_id) REFERENCES product(product_id),
    FOREIGN KEY (operator_id) REFERENCES sys_user(user_id)
);

-- 10. 仓库调拨表（不同仓库间调拨商品）
CREATE TABLE IF NOT EXISTS warehouse_transfer (
    transfer_id INT PRIMARY KEY AUTO_INCREMENT COMMENT '调拨单ID',
    from_warehouse_id INT COMMENT '调出仓库ID',
    to_warehouse_id INT COMMENT '调入仓库ID',
    product_id INT COMMENT '商品ID',
    transfer_quantity INT NOT NULL COMMENT '调拨数量',
    transfer_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '调拨时间',
    operator_id INT COMMENT '操作员ID',
    status VARCHAR(10) DEFAULT '已完成' COMMENT '状态：已完成/待审核/已取消',
    FOREIGN KEY (from_warehouse_id) REFERENCES warehouse(warehouse_id),
    FOREIGN KEY (to_warehouse_id) REFERENCES warehouse(warehouse_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id),
    FOREIGN KEY (operator_id) REFERENCES sys_user(user_id)
);