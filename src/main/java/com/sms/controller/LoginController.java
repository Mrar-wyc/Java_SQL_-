package com.sms.controller;

import com.sms.dao.ProductDao;
import com.sms.view.LoginFrame;
import com.sms.view.MainFrame;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class LoginController {
    private LoginFrame loginFrame;
    private ProductDao productDao;

    public LoginController(LoginFrame frame) {
        this.loginFrame = frame;
        this.productDao = new ProductDao();
        frame.getLoginButton().addActionListener(new LoginListener());
    }

    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginFrame.getUsername();
            String password = loginFrame.getPassword();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "用户名和密码不能为空！");
                return;
            }

            try {
                if (productDao.verifyAdmin(username, password)) {
                    JOptionPane.showMessageDialog(loginFrame, "登录成功！");
                    loginFrame.dispose();
                    new MainFrame(username).setVisible(true); // 跳转到超市主界面
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "用户名或密码错误！");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(loginFrame, "数据库连接失败：" + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}