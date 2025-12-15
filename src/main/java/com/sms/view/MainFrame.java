package com.sms.view;

import com.sms.dao.ProductDao;
import com.sms.model.Category;
import com.sms.model.Product;
import com.sms.model.Supplier;
import com.sms.model.Warehouse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {
    private JTextField searchField;
    private JButton searchButton, addButton, updateButton, deleteButton, filterButton, saleButton, exportButton, warnButton;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private ProductDao productDao;
    private String currentUser;

    // 下拉框数据源
    private List<Category> categoryList;
    private List<Supplier> supplierList;
    private List<Warehouse> warehouseList;

    public MainFrame(String username) {
        this.productDao = new ProductDao();
        this.currentUser = username;
        // 加载分类/供应商/仓库数据
        loadBaseData();
        initUI();
        setTitle("超市商品管理系统 - 欢迎：" + username);
        setSize(1400, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // 加载分类/供应商/仓库基础数据
    private void loadBaseData() {
        try {
            categoryList = productDao.getAllCategories();
            supplierList = productDao.getAllSuppliers();
            warehouseList = productDao.getAllWarehouses();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "加载基础数据失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initUI() {
        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());

        // 搜索区
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("搜索（名称/分类/条形码）："));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        searchButton = new JButton("搜索");
        searchPanel.add(searchButton);

        // 功能按钮区
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("新增商品");
        updateButton = new JButton("修改商品");
        deleteButton = new JButton("删除商品");
        filterButton = new JButton("多条件筛选");
        saleButton = new JButton("销售出库");
        exportButton = new JButton("导出商品");
        warnButton = new JButton("库存预警");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(saleButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(warnButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // 商品表格（适配新字段）
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{
                "商品ID", "商品名称", "分类ID", "供应商ID", "仓库ID", "原价(元)", "折扣价(元)",
                "库存", "条形码", "生产日期", "保质期(天)", "截止日期", "是否上架", "采购价(元)", "单位"
        });
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(productTable);

        // 组装界面
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 绑定事件
        searchButton.addActionListener(e -> searchProducts());
        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        filterButton.addActionListener(e -> filterProducts());
        saleButton.addActionListener(e -> saleProduct());
        exportButton.addActionListener(e -> exportProducts());
        warnButton.addActionListener(e -> stockWarn());

        // 初始化数据
        searchProducts();
    }

    // 搜索商品
    private void searchProducts() {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);

        try {
            List<Product> products = productDao.searchProducts(query);
            for (Product p : products) {
                tableModel.addRow(new Object[]{
                        p.getProductId(),
                        p.getProductName(),
                        p.getCategoryId(),
                        p.getSupplierId(),
                        p.getWarehouseId(),
                        p.getPrice(),
                        p.getDiscountPrice(),
                        p.getStock(),
                        p.getBarCode(),
                        p.getProductionDate(),
                        p.getShelfLifeDays(),
                        p.getExpiryDate(),
                        p.getIsOnSale(),
                        p.getPurchasePrice(),
                        p.getUnit()
                });
            }
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this, "未找到相关商品！");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "搜索失败：" + ex.getMessage());
        }
    }

    // 新增商品（下拉框选择分类/供应商/仓库）
    private void addProduct() {
        if (categoryList.isEmpty() || supplierList.isEmpty() || warehouseList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "基础数据加载失败，无法新增商品！");
            return;
        }

        // 构建弹窗面板
        JPanel panel = new JPanel(new GridLayout(14, 2, 5, 5));

        // 输入框/下拉框
        JTextField nameField = new JTextField();
        // 分类下拉框
        JComboBox<Category> categoryBox = new JComboBox<>(categoryList.toArray(new Category[0]));
        // 供应商下拉框
        JComboBox<Supplier> supplierBox = new JComboBox<>(supplierList.toArray(new Supplier[0]));
        // 仓库下拉框
        JComboBox<Warehouse> warehouseBox = new JComboBox<>(warehouseList.toArray(new Warehouse[0]));

        JTextField priceField = new JTextField();
        JTextField discountPriceField = new JTextField("0.00");
        JTextField stockField = new JTextField();
        JTextField barCodeField = new JTextField();
        JTextField prodDateField = new JTextField("格式：2025-01-01");
        JTextField shelfLifeField = new JTextField();
        JTextField expiryDateField = new JTextField("格式：2025-01-01");
        JTextField isOnSaleField = new JTextField("1=上架，0=下架");
        JTextField purchasePriceField = new JTextField();
        JTextField unitField = new JTextField("件");

        // 添加组件
        panel.add(new JLabel("商品名称："));
        panel.add(nameField);
        panel.add(new JLabel("商品分类："));
        panel.add(categoryBox);
        panel.add(new JLabel("供应商："));
        panel.add(supplierBox);
        panel.add(new JLabel("所属仓库："));
        panel.add(warehouseBox);
        panel.add(new JLabel("原价（元）："));
        panel.add(priceField);
        panel.add(new JLabel("折扣价（元）："));
        panel.add(discountPriceField);
        panel.add(new JLabel("库存数量："));
        panel.add(stockField);
        panel.add(new JLabel("条形码："));
        panel.add(barCodeField);
        panel.add(new JLabel("生产日期："));
        panel.add(prodDateField);
        panel.add(new JLabel("保质期（天）："));
        panel.add(shelfLifeField);
        panel.add(new JLabel("截止日期："));
        panel.add(expiryDateField);
        panel.add(new JLabel("是否上架（1/0）："));
        panel.add(isOnSaleField);
        panel.add(new JLabel("采购价（元）："));
        panel.add(purchasePriceField);
        panel.add(new JLabel("单位："));
        panel.add(unitField);

        int result = JOptionPane.showConfirmDialog(this, panel, "新增商品", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // 解析输入
                String productName = nameField.getText().trim();
                Category selectedCategory = (Category) categoryBox.getSelectedItem();
                Supplier selectedSupplier = (Supplier) supplierBox.getSelectedItem();
                Warehouse selectedWarehouse = (Warehouse) warehouseBox.getSelectedItem();

                double price = Double.parseDouble(priceField.getText().trim());
                double discountPrice = Double.parseDouble(discountPriceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                String barCode = barCodeField.getText().trim();
                Date productionDate = Date.valueOf(prodDateField.getText().trim());
                int shelfLifeDays = Integer.parseInt(shelfLifeField.getText().trim());
                Date expiryDate = Date.valueOf(expiryDateField.getText().trim());
                int isOnSale = Integer.parseInt(isOnSaleField.getText().trim());
                double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
                String unit = unitField.getText().trim();

                // 构建商品对象
                Product product = new Product(
                        productName,
                        selectedCategory.getCategoryId(),
                        selectedSupplier.getSupplierId(),
                        selectedWarehouse.getWarehouseId(),
                        price,
                        discountPrice,
                        stock,
                        barCode,
                        productionDate,
                        shelfLifeDays,
                        expiryDate,
                        isOnSale,
                        purchasePrice,
                        unit
                );

                if (productDao.addProduct(product)) {
                    JOptionPane.showMessageDialog(this, "新增商品成功！");
                    searchProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "新增商品失败！");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "输入错误：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 修改商品
    private void updateProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要修改的商品！");
            return;
        }

        // 获取原有数据
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String oldName = (String) tableModel.getValueAt(selectedRow, 1);
        int oldCategoryId = (int) tableModel.getValueAt(selectedRow, 2);
        int oldSupplierId = (int) tableModel.getValueAt(selectedRow, 3);
        int oldWarehouseId = (int) tableModel.getValueAt(selectedRow, 4);
        double oldPrice = (double) tableModel.getValueAt(selectedRow, 5);
        double oldDiscount = (double) tableModel.getValueAt(selectedRow, 6);
        int oldStock = (int) tableModel.getValueAt(selectedRow, 7);
        String oldBarCode = (String) tableModel.getValueAt(selectedRow, 8);
        Date oldProdDate = (Date) tableModel.getValueAt(selectedRow, 9);
        int oldShelfLife = (int) tableModel.getValueAt(selectedRow, 10);
        Date oldExpiry = (Date) tableModel.getValueAt(selectedRow, 11);
        int oldIsOnSale = (int) tableModel.getValueAt(selectedRow, 12);
        double oldPurchase = (double) tableModel.getValueAt(selectedRow, 13);
        String oldUnit = (String) tableModel.getValueAt(selectedRow, 14);

        // 弹窗面板
        JPanel panel = new JPanel(new GridLayout(14, 2, 5, 5));
        JTextField nameField = new JTextField(oldName);
        JComboBox<Category> categoryBox = new JComboBox<>(categoryList.toArray(new Category[0]));
        JComboBox<Supplier> supplierBox = new JComboBox<>(supplierList.toArray(new Supplier[0]));
        JComboBox<Warehouse> warehouseBox = new JComboBox<>(warehouseList.toArray(new Warehouse[0]));

        // 选中原有分类/供应商/仓库
        for (Category c : categoryList) {
            if (c.getCategoryId() == oldCategoryId) {
                categoryBox.setSelectedItem(c);
                break;
            }
        }
        for (Supplier s : supplierList) {
            if (s.getSupplierId() == oldSupplierId) {
                supplierBox.setSelectedItem(s);
                break;
            }
        }
        for (Warehouse w : warehouseList) {
            if (w.getWarehouseId() == oldWarehouseId) {
                warehouseBox.setSelectedItem(w);
                break;
            }
        }

        JTextField priceField = new JTextField(String.valueOf(oldPrice));
        JTextField discountPriceField = new JTextField(String.valueOf(oldDiscount));
        JTextField stockField = new JTextField(String.valueOf(oldStock));
        JTextField barCodeField = new JTextField(oldBarCode);
        JTextField prodDateField = new JTextField(oldProdDate.toString());
        JTextField shelfLifeField = new JTextField(String.valueOf(oldShelfLife));
        JTextField expiryDateField = new JTextField(oldExpiry.toString());
        JTextField isOnSaleField = new JTextField(String.valueOf(oldIsOnSale));
        JTextField purchasePriceField = new JTextField(String.valueOf(oldPurchase));
        JTextField unitField = new JTextField(oldUnit);

        // 添加组件
        panel.add(new JLabel("商品名称："));
        panel.add(nameField);
        panel.add(new JLabel("商品分类："));
        panel.add(categoryBox);
        panel.add(new JLabel("供应商："));
        panel.add(supplierBox);
        panel.add(new JLabel("所属仓库："));
        panel.add(warehouseBox);
        panel.add(new JLabel("原价（元）："));
        panel.add(priceField);
        panel.add(new JLabel("折扣价（元）："));
        panel.add(discountPriceField);
        panel.add(new JLabel("库存数量："));
        panel.add(stockField);
        panel.add(new JLabel("条形码："));
        panel.add(barCodeField);
        panel.add(new JLabel("生产日期："));
        panel.add(prodDateField);
        panel.add(new JLabel("保质期（天）："));
        panel.add(shelfLifeField);
        panel.add(new JLabel("截止日期："));
        panel.add(expiryDateField);
        panel.add(new JLabel("是否上架（1/0）："));
        panel.add(isOnSaleField);
        panel.add(new JLabel("采购价（元）："));
        panel.add(purchasePriceField);
        panel.add(new JLabel("单位："));
        panel.add(unitField);

        int result = JOptionPane.showConfirmDialog(this, panel, "修改商品（ID：" + productId + "）", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // 解析修改后的数据
                String productName = nameField.getText().trim();
                Category selectedCategory = (Category) categoryBox.getSelectedItem();
                Supplier selectedSupplier = (Supplier) supplierBox.getSelectedItem();
                Warehouse selectedWarehouse = (Warehouse) warehouseBox.getSelectedItem();

                double price = Double.parseDouble(priceField.getText().trim());
                double discountPrice = Double.parseDouble(discountPriceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                String barCode = barCodeField.getText().trim();
                Date productionDate = Date.valueOf(prodDateField.getText().trim());
                int shelfLifeDays = Integer.parseInt(shelfLifeField.getText().trim());
                Date expiryDate = Date.valueOf(expiryDateField.getText().trim());
                int isOnSale = Integer.parseInt(isOnSaleField.getText().trim());
                double purchasePrice = Double.parseDouble(purchasePriceField.getText().trim());
                String unit = unitField.getText().trim();

                // 构建商品对象
                Product product = new Product(
                        productId,
                        productName,
                        selectedCategory.getCategoryId(),
                        selectedSupplier.getSupplierId(),
                        selectedWarehouse.getWarehouseId(),
                        price,
                        discountPrice,
                        stock,
                        barCode,
                        productionDate,
                        shelfLifeDays,
                        expiryDate,
                        isOnSale,
                        purchasePrice,
                        unit
                );

                if (productDao.updateProduct(product)) {
                    JOptionPane.showMessageDialog(this, "修改商品成功！");
                    searchProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "修改商品失败！");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "输入错误：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 删除商品
    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的商品！");
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "确定删除商品【" + productName + "】吗？",
                "删除确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (productDao.deleteProduct(productId)) {
                    JOptionPane.showMessageDialog(this, "删除商品成功！");
                    searchProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "删除商品失败！");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "删除失败：" + e.getMessage());
            }
        }
    }

    // 多条件筛选
    private void filterProducts() {
        // ========== 1. 创建主面板（用BoxLayout垂直排列各模块） ==========
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 内边距

        // ========== 模块1：分类/供应商筛选（加标题边框） ==========
        JPanel categorySupplierPanel = new JPanel(new GridBagLayout());
        categorySupplierPanel.setBorder(BorderFactory.createTitledBorder("分类/供应商筛选"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 控件间距
        gbc.anchor = GridBagConstraints.EAST; // 标签右对齐

        // 商品分类下拉框
        gbc.gridx = 0;
        gbc.gridy = 0;
        categorySupplierPanel.add(new JLabel("商品分类："), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        JComboBox<Category> categoryBox = new JComboBox<>();
        categoryBox.addItem(new Category(-1, "不筛选"));
        for (Category c : categoryList) categoryBox.addItem(c);
        categoryBox.setPreferredSize(new Dimension(180, 25)); // 统一宽度
        categorySupplierPanel.add(categoryBox, gbc);

        // 供应商下拉框
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;
        categorySupplierPanel.add(new JLabel("供应商："), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 3;
        JComboBox<Supplier> supplierBox = new JComboBox<>();
        supplierBox.addItem(new Supplier(-1, "不筛选"));
        for (Supplier s : supplierList) supplierBox.addItem(s);
        supplierBox.setPreferredSize(new Dimension(180, 25));
        categorySupplierPanel.add(supplierBox, gbc);

        mainPanel.add(categorySupplierPanel);


        // ========== 模块2：价格区间筛选 ==========
        JPanel pricePanel = new JPanel(new GridBagLayout());
        pricePanel.setBorder(BorderFactory.createTitledBorder("价格筛选"));
        gbc.anchor = GridBagConstraints.EAST;

        // 最低单价
        gbc.gridx = 0;
        gbc.gridy = 0;
        pricePanel.add(new JLabel("最低单价（元）："), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        JTextField minPriceField = new JTextField("0");
        minPriceField.setPreferredSize(new Dimension(100, 25));
        pricePanel.add(minPriceField, gbc);

        // 最高单价
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;
        pricePanel.add(new JLabel("最高单价（元）："), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 3;
        JTextField maxPriceField = new JTextField("9999");
        maxPriceField.setPreferredSize(new Dimension(100, 25));
        pricePanel.add(maxPriceField, gbc);

        mainPanel.add(pricePanel);


        // ========== 模块3：库存区间筛选 ==========
        JPanel stockPanel = new JPanel(new GridBagLayout());
        stockPanel.setBorder(BorderFactory.createTitledBorder("库存筛选"));
        gbc.anchor = GridBagConstraints.EAST;

        // 最低库存
        gbc.gridx = 0;
        gbc.gridy = 0;
        stockPanel.add(new JLabel("最低库存："), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        JTextField minStockField = new JTextField("0");
        minStockField.setPreferredSize(new Dimension(100, 25));
        stockPanel.add(minStockField, gbc);

        // 最高库存
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 2;
        stockPanel.add(new JLabel("最高库存："), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 3;
        JTextField maxStockField = new JTextField("9999");
        maxStockField.setPreferredSize(new Dimension(100, 25));
        stockPanel.add(maxStockField, gbc);

        mainPanel.add(stockPanel);


        // ========== 模块4：上架状态筛选 ==========
        JPanel statusPanel = new JPanel(new GridBagLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("状态筛选"));
        gbc.anchor = GridBagConstraints.EAST;

        // 是否上架
        gbc.gridx = 0;
        gbc.gridy = 0;
        statusPanel.add(new JLabel("是否上架（1=是/0=否/-1=不筛选）："), gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        JTextField isOnSaleField = new JTextField("-1");
        isOnSaleField.setPreferredSize(new Dimension(100, 25));
        statusPanel.add(isOnSaleField, gbc);

        mainPanel.add(statusPanel);


        // ========== 2. 弹出筛选窗口（加确定/取消按钮） ==========
        int result = JOptionPane.showConfirmDialog(
                this,
                mainPanel,
                "多条件筛选",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE // 去掉默认问号图标
        );


        // ========== 3. 解析条件并执行筛选（逻辑和之前一致） ==========
        if (result == JOptionPane.OK_OPTION) {
            try {
                double minPrice = Double.parseDouble(minPriceField.getText().trim());
                double maxPrice = Double.parseDouble(maxPriceField.getText().trim());
                int minStock = Integer.parseInt(minStockField.getText().trim());
                int maxStock = Integer.parseInt(maxStockField.getText().trim());
                int isOnSale = Integer.parseInt(isOnSaleField.getText().trim());

                // 获取分类/供应商ID
                Category selectedCategory = (Category) categoryBox.getSelectedItem();
                int categoryId = selectedCategory.getCategoryId();
                Supplier selectedSupplier = (Supplier) supplierBox.getSelectedItem();
                int supplierId = selectedSupplier.getSupplierId();

                // 执行筛选
                List<Product> filteredProducts = productDao.filterProducts(
                        minPrice, maxPrice, minStock, maxStock, isOnSale, categoryId, supplierId
                );

                // 更新表格
                tableModel.setRowCount(0);
                for (Product p : filteredProducts) {
                    tableModel.addRow(new Object[]{
                            p.getProductId(), p.getProductName(), p.getCategoryId(), p.getSupplierId(),
                            p.getWarehouseId(), p.getPrice(), p.getDiscountPrice(), p.getStock(),
                            p.getBarCode(), p.getProductionDate(), p.getShelfLifeDays(), p.getExpiryDate(),
                            p.getIsOnSale(), p.getPurchasePrice(), p.getUnit()
                    });
                }

                JOptionPane.showMessageDialog(this, "筛选完成！共找到 " + filteredProducts.size() + " 件商品");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "输入错误：价格/库存/状态必须是数字！");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "筛选失败：" + e.getMessage());
            }
        }
    }

    // 销售出库
    private void saleProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选中要销售的商品！");
            return;
        }

        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);
        double price = (double) tableModel.getValueAt(selectedRow, 5);
        double discountPrice = (double) tableModel.getValueAt(selectedRow, 6);
        int stock = (int) tableModel.getValueAt(selectedRow, 7);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField quantityField = new JTextField();
        JTextField salePriceField = new JTextField(String.valueOf(discountPrice > 0 ? discountPrice : price));

        panel.add(new JLabel("商品名称："));
        panel.add(new JLabel(productName + "（库存：" + stock + "）"));
        panel.add(new JLabel("销售数量："));
        panel.add(quantityField);
        panel.add(new JLabel("销售单价（元）："));
        panel.add(salePriceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "销售出库", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int saleQuantity = Integer.parseInt(quantityField.getText().trim());
                double salePrice = Double.parseDouble(salePriceField.getText().trim());

                if (saleQuantity <= 0) {
                    JOptionPane.showMessageDialog(this, "销售数量必须大于0！");
                    return;
                }

                if (productDao.saleProduct(productId, saleQuantity, salePrice, currentUser)) {
                    JOptionPane.showMessageDialog(this, "销售成功！\n商品：" + productName + "\n数量：" + saleQuantity + "\n总价：" + (saleQuantity * salePrice));
                    searchProducts();
                } else {
                    JOptionPane.showMessageDialog(this, "销售失败！库存不足（当前库存：" + stock + "）");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "销售失败：" + e.getMessage());
            }
        }
    }

    // 导出商品
    private void exportProducts() {
        try {
            List<Product> products = productDao.getAllProducts();
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this, "暂无商品可导出！");
                return;
            }

            // ========== 修复：正确获取桌面路径 ==========
            javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView();
            File desktopDir = fsv.getHomeDirectory(); // 系统桌面目录
            File exportFile = new File(desktopDir, "product_export.txt");
            String path = exportFile.getAbsolutePath();

            // 写入文件
            FileWriter writer = new FileWriter(exportFile);
            writer.write("商品ID,商品名称,分类ID,供应商ID,仓库ID,原价,折扣价,库存,条形码,生产日期,保质期(天),截止日期,是否上架,采购价,单位\n");
            for (Product p : products) {
                writer.write(p.getProductId() + "," +
                        p.getProductName() + "," +
                        p.getCategoryId() + "," +
                        p.getSupplierId() + "," +
                        p.getWarehouseId() + "," +
                        p.getPrice() + "," +
                        p.getDiscountPrice() + "," +
                        p.getStock() + "," +
                        p.getBarCode() + "," +
                        p.getProductionDate() + "," +
                        p.getShelfLifeDays() + "," +
                        p.getExpiryDate() + "," +
                        p.getIsOnSale() + "," +
                        p.getPurchasePrice() + "," +
                        p.getUnit() + "\n");
            }
            writer.close();
            JOptionPane.showMessageDialog(this, "导出成功！文件路径：" + path);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "导出失败：数据库查询错误 - " + e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "导出失败：文件写入错误 - " + e.getMessage());
        }
    }

    // 库存预警
    private void stockWarn() {
        String thresholdStr = JOptionPane.showInputDialog(this, "请输入库存预警阈值（默认10）：", "库存预警", JOptionPane.QUESTION_MESSAGE);
        int threshold = 10;
        if (thresholdStr != null && !thresholdStr.isEmpty()) {
            try {
                threshold = Integer.parseInt(thresholdStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "输入错误，使用默认阈值10！");
            }
        }

        try {
            List<Product> warnProducts = productDao.getStockWarnProducts(threshold);
            if (warnProducts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "暂无库存低于" + threshold + "的商品！");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("库存预警（低于").append(threshold).append("）：\n");
            for (Product p : warnProducts) {
                sb.append("【").append(p.getProductName()).append("】库存：").append(p.getStock()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "库存预警提示", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "查询失败：" + e.getMessage());
        }
    }
}