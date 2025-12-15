package com.sms.model;

public class Category {
    private int categoryId; // 分类ID
    private String categoryName; // 分类名称
    private int parentId; // 父分类ID
    private int sort; // 排序

    // 构造函数
    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getter/Setter
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
    public int getSort() { return sort; }
    public void setSort(int sort) { this.sort = sort; }

    // 下拉框显示分类名称
    @Override
    public String toString() {
        return categoryName;
    }
}