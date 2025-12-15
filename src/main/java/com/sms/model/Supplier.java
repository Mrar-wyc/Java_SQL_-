package com.sms.model;

public class Supplier {
    private int supplierId; // 供应商ID
    private String supplierName; // 供应商名称
    private String contactPerson; // 联系人
    private String phone; // 电话

    // 构造函数
    public Supplier(int supplierId, String supplierName) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }

    // Getter/Setter
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // 下拉框显示供应商名称
    @Override
    public String toString() {
        return supplierName;
    }
}