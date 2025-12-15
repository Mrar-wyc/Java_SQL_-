-- 查询「碳酸饮料」分类下的所有商品+供应商名称+仓库名称
SELECT p.product_name, c.category_name, s.supplier_name, w.warehouse_name, p.price, p.stock 
FROM product p
LEFT JOIN product_category c ON p.category_id = c.category_id
LEFT JOIN supplier s ON p.supplier_id = s.supplier_id
LEFT JOIN warehouse w ON p.warehouse_id = w.warehouse_id
WHERE c.category_name = '碳酸饮料';