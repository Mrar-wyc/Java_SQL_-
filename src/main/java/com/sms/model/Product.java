package com.sms.model;

import java.sql.Date;

public class Product {
    // 核心字段（适配新product表）
    private int productId; // 替代原id
    private String productName;
    private int categoryId; // 关联分类表
    private int supplierId; // 关联供应商表
    private int warehouseId; // 关联仓库表
    private double price; // 原价
    private double discountPrice; // 折扣价
    private int stock; // 库存
    private String barCode; // 条形码
    private Date productionDate; // 生产日期
    private int shelfLifeDays; // 保质期（天）
    private Date expiryDate; // 截止日期
    private int isOnSale; // 是否上架
    private double purchasePrice; // 采购价
    private String unit; // 单位

    // 1. 新增商品用（无productId）
    public Product(String productName, int categoryId, int supplierId, int warehouseId, double price,
                   double discountPrice, int stock, String barCode, Date productionDate, int shelfLifeDays,
                   Date expiryDate, int isOnSale, double purchasePrice, String unit) {
        this.productName = productName;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.warehouseId = warehouseId;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stock = stock;
        this.barCode = barCode;
        this.productionDate = productionDate;
        this.shelfLifeDays = shelfLifeDays;
        this.expiryDate = expiryDate;
        this.isOnSale = isOnSale;
        this.purchasePrice = purchasePrice;
        this.unit = unit;
    }

    // 2. 修改/查询用（含productId）
    public Product(int productId, String productName, int categoryId, int supplierId, int warehouseId,
                   double price, double discountPrice, int stock, String barCode, Date productionDate,
                   int shelfLifeDays, Date expiryDate, int isOnSale, double purchasePrice, String unit) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.warehouseId = warehouseId;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stock = stock;
        this.barCode = barCode;
        this.productionDate = productionDate;
        this.shelfLifeDays = shelfLifeDays;
        this.expiryDate = expiryDate;
        this.isOnSale = isOnSale;
        this.purchasePrice = purchasePrice;
        this.unit = unit;
    }

    // 全字段Getter/Setter（必须补全）
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(double discountPrice) { this.discountPrice = discountPrice; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public String getBarCode() { return barCode; }
    public void setBarCode(String barCode) { this.barCode = barCode; }
    public Date getProductionDate() { return productionDate; }
    public void setProductionDate(Date productionDate) { this.productionDate = productionDate; }
    public int getShelfLifeDays() { return shelfLifeDays; }
    public void setShelfLifeDays(int shelfLifeDays) { this.shelfLifeDays = shelfLifeDays; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public int getIsOnSale() { return isOnSale; }
    public void setIsOnSale(int isOnSale) { this.isOnSale = isOnSale; }
    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}