package cn.syj.emusearch;

import cn.syj.emusearch.ui.panel.EMUSearchPanel;
import cn.syj.emusearch.util.Utils;

import javax.swing.*;
import java.awt.*;

/**
 * @author syj
 **/
public class MainApp {

    private JFrame frame;

    private MainApp() {
        this.initialize();
    }

    public EMUSearchPanel emuSearchPanel;

    private void initialize() {
        // 设置系统默认样式
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame() {

        };
        frame.setTitle("动车组配属查询");
        frame.setLayout(new BorderLayout());
        //cr400bf-j.png
        frame.setIconImage(Utils.loadImageIcon("imgs/cr400bf-j.png").getImage());
        frame.getContentPane().setBackground(Color.RED);
        // frame.setSize(700, 700);    //设置窗口显示尺寸
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //置窗口是否可以关闭
        emuSearchPanel = new EMUSearchPanel();
        frame.add(emuSearchPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MainApp mainApp = new MainApp();
            mainApp.frame.setVisible(true);
        });
    }

}
