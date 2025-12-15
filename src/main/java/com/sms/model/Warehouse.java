package com.sms.model;

public class Warehouse {
    private int warehouseId; // 仓库ID
    private String warehouseName; // 仓库名称
    private String address; // 地址

    // 构造函数
    public Warehouse(int warehouseId, String warehouseName) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
    }

    // Getter/Setter
    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // 下拉框显示仓库名称
    @Override
    public String toString() {
        return warehouseName;
    }
}