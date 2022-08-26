package cn.syj.emusearch.swing;

import cn.syj.emusearch.constant.Constants;
import cn.syj.emusearch.entity.EmuTrain;
import cn.syj.emusearch.service.EmuService;
import cn.syj.emusearch.service.impl.RemoteEmuServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

/**
 * @author syj
 * 2021-02-23 17:19
 **/
public class MainApp {

    private JFrame frame;

    private MainApp() {
        this.initialize();
    }

    private void initialize() {

        // 设置系统默认样式
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        JFrame.setDefaultLookAndFeelDecorated(false);
        frame = new JFrame();
        frame.setTitle("动车组配属查询");
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.RED);
        // frame.setSize(700, 700);    //设置窗口显示尺寸
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //置窗口是否可以关闭
        Container container = frame.getContentPane();    //获取当前窗口的内容窗格
        GridBagLayout cGridBagLayout = new GridBagLayout();
        //设置了总共有一列
        cGridBagLayout.columnWidths = new int[]{0};
        //设置了总共有2行
        cGridBagLayout.rowHeights = new int[]{0, 0};
        //设置了列的宽度为容器宽度
        cGridBagLayout.columnWeights = new double[]{1.0};
        //第一行的高度占了容器的2份，第二行的高度占了容器的8份
        cGridBagLayout.rowWeights = new double[]{0.15, 0.85};
        container.setLayout(cGridBagLayout);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("imgs/cr400_z.jpg")));
        Border greyLineBorder = BorderFactory.createLineBorder(Color.GRAY, 2);

        /*------------条件panel------------*/
        JPanel cdPanel = new JPanel(new FlowLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                //super.printComponent(g);
                g.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);
            }
        };
        //cdPanel.setUI();
        cdPanel.setMaximumSize(new DimensionUIResource(frame.getWidth(), 200));
        cdPanel.setBorder(BorderFactory.createTitledBorder(greyLineBorder, "查询条件"));
        GridBagConstraints cdGbc = new GridBagConstraints();
        cdGbc.insets = new Insets(0, 0, 5, 0);
        cdGbc.fill = GridBagConstraints.BOTH;
        cdGbc.gridx = 0;
        cdGbc.gridy = 0;
        container.add(cdPanel, cdGbc);

        /*------------结果panel------------*/
        JPanel rsPanel = new JPanel(new BorderLayout());
        TitledBorder rsTb = BorderFactory.createTitledBorder(greyLineBorder, "查询结果");
        rsPanel.setBorder(rsTb);
        GridBagConstraints rsGbc = new GridBagConstraints();
        rsGbc.fill = GridBagConstraints.BOTH;
        rsGbc.gridx = 0;
        rsGbc.gridy = 1;
        container.add(rsPanel, rsGbc);

        String[] typeList = new String[]{
                "全部",
                "CRH380A",
                "CRH380AL",
                "CRH380AN",
                "CRH380AM",
                "CRH380AJ",
                "CRH380B",
                "CRH380BL",
                "CRH380BG",
                "CRH380BJ",
                "CRH380BJ-A",
                "CRH380CL",
                "CRH380D",
                "CR400AF",
                "CR400AF-A",
                "CR400AF-B",
                "CR400AF-C",
                "CR400AF-G",
                "CR400AF-Z",
                "CR400AF-BZ",
                "CR400BF",
                "CR400BF-A",
                "CR400BF-B",
                "CR400BF-C",
                "CR400BF-G",
                "CR400BF-Z",
                "CR400BF-AZ",
                "CR400BF-BZ",
                "CR400BF-GZ",
                "CR400BF-J"
        };
        String[] railwayBureauList = new String[]{"全部", "北京铁路局", "上海铁路局", "广州铁路集团"};
        String[] manufacturerList = new String[]{"全部", "BST", "南车青岛四方", "唐山轨道客车", "长春轨道客车"};

        //型号选择框
        final JComboBox<String> modelComboBox = new JComboBox<>(typeList);
        //路局选择框
        final JComboBox<String> bureauComboBox = new JComboBox<>(railwayBureauList);
        //主机厂选择框
        final JComboBox<String> plantComboBox = new JComboBox<>(manufacturerList);
        //动车所选择框
        final JTextField departmentTextField = new JTextField(10);
        JPanel modelPanel = createConditionPanel("型号", modelComboBox);
        cdPanel.add(modelPanel);
        JPanel bureauPanel = createConditionPanel("路局", bureauComboBox);
        cdPanel.add(bureauPanel);
        JPanel plantPanel = createConditionPanel("主机厂", plantComboBox);
        cdPanel.add(plantPanel);
        JPanel departmentPanel = createConditionPanel("动车所", departmentTextField);
        cdPanel.add(departmentPanel);
        JButton searchButton = new JButton("查询");
        cdPanel.add(searchButton);
        EmuService emuService = new RemoteEmuServiceImpl(Constants.PASS_SEARCH_URL, 20000);

        Vector<String> col = new Vector<>(6);
        col.add("型号");
        col.add("编号");
        col.add("路局");
        col.add("主机厂");
        col.add("动车所");
        col.add("备注");
        DefaultTableModel dtm = new DefaultTableModel(col, 0);

        //用于展示查询结果的表格
        JTable rsTable = new JTable(dtm) {

            //设置单元格不可编辑
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Font f = new Font("Microsoft YaHei", Font.PLAIN, 12);
        rsTable.setFont(f);
        rsPanel.add(new JScrollPane(rsTable));

        //设置居中于屏幕
        frame.pack();
        frame.setLocationRelativeTo(null);

        /*---------事件---------*/
        searchButton.addActionListener((e) -> {
            Map<String, Object> cm = new HashMap<>();
            cm.put(Constants.MODEL, modelComboBox.getSelectedItem());
            cm.put(Constants.BUREAU, bureauComboBox.getSelectedItem());
            cm.put(Constants.PLANT, plantComboBox.getSelectedItem());
            cm.put(Constants.DEPARTMENT, departmentTextField.getText());
            long start = System.currentTimeMillis();
            rsTable.setModel(emuService.searchTableModel(cm));
            System.out.println((System.currentTimeMillis() - start) + "ms");
            rsTb.setTitle("查询结果共" + rsTable.getRowCount() + "条");
            rsPanel.updateUI();
        });
    }

    private void setVisible() {
        this.frame.setVisible(true);
    }

    private <C extends Component> JPanel createConditionPanel(String labelText, C component) {
        GridBagLayout gridBag = new GridBagLayout();
        JPanel jPanel = new JPanel(gridBag);
        jPanel.setOpaque(false);
        //jPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        JLabel jLabel = new JLabel(labelText);
        jLabel.setForeground(Color.WHITE);
        Font font = jLabel.getFont();
        jLabel.setFont(font.deriveFont(Font.BOLD));
        jLabel.setOpaque(false);
        gridBag.addLayoutComponent(jLabel, new GridBagConstraints());
        gridBag.addLayoutComponent(component, new GridBagConstraints());
        jPanel.add(jLabel);
        jPanel.add(component);
        return jPanel;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MainApp mainApp = new MainApp();
            mainApp.setVisible();
        });
    }
}
