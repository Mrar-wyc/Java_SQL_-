package com.sms.dao;

import com.sms.model.Category;
import com.sms.model.Product;
import com.sms.model.Supplier;
import com.sms.model.Warehouse;
import com.sms.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    // ========== 1. 登录验证（适配sys_user表） ==========
    public boolean verifyAdmin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM sys_user WHERE username = ? AND password = ? AND is_valid = 1";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ========== 2. 分类相关 ==========
    // 查询所有分类（用于界面下拉框）
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, category_name FROM product_category ORDER BY sort";
        try (Connection conn = DbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categories.add(new Category(rs.getInt("category_id"), rs.getString("category_name")));
            }
        }
        return categories;
    }

    // ========== 3. 供应商相关 ==========
    // 查询所有有效供应商
    public List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT supplier_id, supplier_name FROM supplier WHERE is_valid = 1";
        try (Connection conn = DbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                suppliers.add(new Supplier(rs.getInt("supplier_id"), rs.getString("supplier_name")));
            }
        }
        return suppliers;
    }

    // ========== 4. 仓库相关 ==========
    // 查询所有仓库
    public List<Warehouse> getAllWarehouses() throws SQLException {
        List<Warehouse> warehouses = new ArrayList<>();
        String sql = "SELECT warehouse_id, warehouse_name FROM warehouse";
        try (Connection conn = DbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                warehouses.add(new Warehouse(rs.getInt("warehouse_id"), rs.getString("warehouse_name")));
            }
        }
        return warehouses;
    }

    // ========== 5. 商品查询（适配product表） ==========
    // 按名称/分类/条形码搜索
    public List<Product> searchProducts(String query) throws SQLException {
        List<Product> products = new ArrayList<>();
        // 关联分类/供应商/仓库表，显示名称（而非ID）
        String sql = "SELECT p.*, c.category_name, s.supplier_name, w.warehouse_name " +
                "FROM product p " +
                "LEFT JOIN product_category c ON p.category_id = c.category_id " +
                "LEFT JOIN supplier s ON p.supplier_id = s.supplier_id " +
                "LEFT JOIN warehouse w ON p.warehouse_id = w.warehouse_id " +
                "WHERE p.product_name LIKE ? OR c.category_name LIKE ? OR p.bar_code LIKE ?";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String param = "%" + query + "%";
            pstmt.setString(1, param);
            pstmt.setString(2, param);
            pstmt.setString(3, param);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("category_id"),
                            rs.getInt("supplier_id"),
                            rs.getInt("warehouse_id"),
                            rs.getDouble("price"),
                            rs.getDouble("discount_price"),
                            rs.getInt("stock"),
                            rs.getString("bar_code"),
                            rs.getDate("production_date"),
                            rs.getInt("shelf_life_days"),
                            rs.getDate("expiry_date"),
                            rs.getInt("is_on_sale"),
                            rs.getDouble("purchase_price"),
                            rs.getString("unit")
                    );
                    products.add(product);
                }
            }
        }
        return products;
    }

    // ========== 6. 新增商品（适配product表） ==========
    public boolean addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO product (product_name, category_id, supplier_id, warehouse_id, price, " +
                "discount_price, stock, bar_code, production_date, shelf_life_days, expiry_date, " +
                "is_on_sale, purchase_price, unit) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getCategoryId());
            pstmt.setInt(3, product.getSupplierId());
            pstmt.setInt(4, product.getWarehouseId());
            pstmt.setDouble(5, product.getPrice());
            pstmt.setDouble(6, product.getDiscountPrice());
            pstmt.setInt(7, product.getStock());
            pstmt.setString(8, product.getBarCode());
            pstmt.setDate(9, product.getProductionDate());
            pstmt.setInt(10, product.getShelfLifeDays());
            pstmt.setDate(11, product.getExpiryDate());
            pstmt.setInt(12, product.getIsOnSale());
            pstmt.setDouble(13, product.getPurchasePrice());
            pstmt.setString(14, product.getUnit());

            return pstmt.executeUpdate() > 0;
        }
    }

    // ========== 7. 修改商品（适配product表） ==========
    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE product SET product_name=?, category_id=?, supplier_id=?, warehouse_id=?, " +
                "price=?, discount_price=?, stock=?, bar_code=?, production_date=?, shelf_life_days=?, " +
                "expiry_date=?, is_on_sale=?, purchase_price=?, unit=? WHERE product_id=?";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.getProductName());
            pstmt.setInt(2, product.getCategoryId());
            pstmt.setInt(3, product.getSupplierId());
            pstmt.setInt(4, product.getWarehouseId());
            pstmt.setDouble(5, product.getPrice());
            pstmt.setDouble(6, product.getDiscountPrice());
            pstmt.setInt(7, product.getStock());
            pstmt.setString(8, product.getBarCode());
            pstmt.setDate(9, product.getProductionDate());
            pstmt.setInt(10, product.getShelfLifeDays());
            pstmt.setDate(11, product.getExpiryDate());
            pstmt.setInt(12, product.getIsOnSale());
            pstmt.setDouble(13, product.getPurchasePrice());
            pstmt.setString(14, product.getUnit());
            pstmt.setInt(15, product.getProductId()); // 最后是商品ID

            return pstmt.executeUpdate() > 0;
        }
    }

    // ========== 8. 删除商品（适配product_id） ==========
    public boolean deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM product WHERE product_id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // ========== 9. 销售出库（适配新表，扣库存+记录日志） ==========
    public boolean saleProduct(int productId, int saleQuantity, double salePrice, String operator) throws SQLException {
        Connection conn = null;
        try {
            conn = DbUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. 查库存
            String checkSql = "SELECT stock FROM product WHERE product_id = ?";
            PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
            checkPstmt.setInt(1, productId);
            ResultSet rs = checkPstmt.executeQuery();
            if (!rs.next() || rs.getInt("stock") < saleQuantity) {
                conn.rollback();
                return false;
            }
            int oldStock = rs.getInt("stock");

            // 2. 扣库存
            String updateSql = "UPDATE product SET stock = stock - ? WHERE product_id = ?";
            PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
            updatePstmt.setInt(1, saleQuantity);
            updatePstmt.setInt(2, productId);
            updatePstmt.executeUpdate();

            // 3. 记录销售单（简化版，实际可关联sale_order/sale_item）
            // 先查操作员ID
            String userSql = "SELECT user_id FROM sys_user WHERE username = ?";
            PreparedStatement userPstmt = conn.prepareStatement(userSql);
            userPstmt.setString(1, operator);
            ResultSet userRs = userPstmt.executeQuery();
            int userId = 1; // 默认admin
            if (userRs.next()) {
                userId = userRs.getInt("user_id");
            }

            // 4. 插入库存日志
            String logSql = "INSERT INTO stock_log (product_id, change_type, change_quantity, before_stock, after_stock, operator_id) " +
                    "VALUES (?, '销售出库', ?, ?, ?, ?)";
            PreparedStatement logPstmt = conn.prepareStatement(logSql);
            logPstmt.setInt(1, productId);
            logPstmt.setInt(2, -saleQuantity); // 负数=出库
            logPstmt.setInt(3, oldStock);
            logPstmt.setInt(4, oldStock - saleQuantity);
            logPstmt.setInt(5, userId);
            logPstmt.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // ========== 10. 库存预警 ==========
    public List<Product> getStockWarnProducts(int warnThreshold) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE stock < ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, warnThreshold);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("category_id"),
                            rs.getInt("supplier_id"),
                            rs.getInt("warehouse_id"),
                            rs.getDouble("price"),
                            rs.getDouble("discount_price"),
                            rs.getInt("stock"),
                            rs.getString("bar_code"),
                            rs.getDate("production_date"),
                            rs.getInt("shelf_life_days"),
                            rs.getDate("expiry_date"),
                            rs.getInt("is_on_sale"),
                            rs.getDouble("purchase_price"),
                            rs.getString("unit")
                    );
                    products.add(product);
                }
            }
        }
        return products;
    }

    // ========== 11. 导出所有商品 ==========
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (Connection conn = DbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("category_id"),
                        rs.getInt("supplier_id"),
                        rs.getInt("warehouse_id"),
                        rs.getDouble("price"),
                        rs.getDouble("discount_price"),
                        rs.getInt("stock"),
                        rs.getString("bar_code"),
                        rs.getDate("production_date"),
                        rs.getInt("shelf_life_days"),
                        rs.getDate("expiry_date"),
                        rs.getInt("is_on_sale"),
                        rs.getDouble("purchase_price"),
                        rs.getString("unit")
                );
                products.add(product);
            }
        }
        return products;
    }

    // ========== 11.多条件筛选商品 ==========
    public List<Product> filterProducts(double minPrice, double maxPrice, int minStock, int maxStock,
                                        int isOnSale, int categoryId, int supplierId) throws SQLException {
        List<Product> products = new ArrayList<>();
        // 动态拼接SQL（1=1是为了方便后续加AND条件）
        String sql = "SELECT * FROM product WHERE 1=1";
        List<Object> params = new ArrayList<>();

        // 1. 价格区间条件
        sql += " AND price BETWEEN ? AND ?";
        params.add(minPrice);
        params.add(maxPrice);

        // 2. 库存区间条件
        sql += " AND stock BETWEEN ? AND ?";
        params.add(minStock);
        params.add(maxStock);

        // 3. 是否上架（-1表示不筛选）
        if (isOnSale != -1) {
            sql += " AND is_on_sale = ?";
            params.add(isOnSale);
        }

        // 4. 分类ID（-1表示不筛选）
        if (categoryId != -1) {
            sql += " AND category_id = ?";
            params.add(categoryId);
        }

        // 5. 供应商ID（-1表示不筛选）
        if (supplierId != -1) {
            sql += " AND supplier_id = ?";
            params.add(supplierId);
        }

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 给SQL设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            // 执行查询并封装结果
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getInt("category_id"),
                            rs.getInt("supplier_id"),
                            rs.getInt("warehouse_id"),
                            rs.getDouble("price"),
                            rs.getDouble("discount_price"),
                            rs.getInt("stock"),
                            rs.getString("bar_code"),
                            rs.getDate("production_date"),
                            rs.getInt("shelf_life_days"),
                            rs.getDate("expiry_date"),
                            rs.getInt("is_on_sale"),
                            rs.getDouble("purchase_price"),
                            rs.getString("unit")
                    );
                    products.add(product);
                }
            }
        }
        return products;
    }
}