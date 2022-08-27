package cn.syj.emusearch.swing;

import cn.syj.emusearch.constant.Constants;
import cn.syj.emusearch.service.EmuService;
import cn.syj.emusearch.service.impl.RemoteEmuServiceImpl;
import com.google.common.collect.Lists;
import org.jsoup.internal.StringUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

/**
 * @author syj
 * 2021-02-23 17:19
 **/
@Deprecated
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
        cGridBagLayout.rowWeights = new double[]{0.2, 0.85};
        container.setLayout(cGridBagLayout);

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(this.getClass().getClassLoader().getResource("imgs/cr400_z.jpg")));
        Border greyLineBorder = BorderFactory.createLineBorder(Color.GRAY, 2);

        /*------------条件panel------------*/
        JPanel cdPanel = new JPanel(new FlowLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Image image = icon.getImage();
                g.drawImage(image, 0, 0, getSize().width, getSize().height, this);
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
        String[] manufacturerList = new String[]{"全部", "BST", "南车青岛四方", "唐山轨道客车", "长春轨道客车"};

        Font f = new Font("Microsoft YaHei", Font.PLAIN, 12);

        //型号选择框
        final JComboBox<String> modelComboBox = new JComboBox<>(new Vector<>(Lists.asList("全部", loadCfg("conf/emu-model.cfg", "\r\n"))));
        modelComboBox.setFont(f);
        //车组号输入框
        final JTextField numberTextField = new JTextField(4);
        numberTextField.setFont(f);
        numberTextField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                //只能输入4个数字
                if (StringUtil.isNumeric(str) && this.getLength() < 4) {
                    super.insertString(offs, str, a);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        //路局选择框
        final JComboBox<String> bureauComboBox = new JComboBox<>(new Vector<>(Lists.asList("全部", loadCfg("conf/cr-bureau.cfg", "\\|"))));
        bureauComboBox.setFont(f);
        //主机厂选择框
        final JComboBox<String> plantComboBox = new JComboBox<>(manufacturerList);
        plantComboBox.setFont(f);
        //动车所选择框
        final JTextField departmentTextField = new JTextField(10);
        departmentTextField.setFont(f);
        JPanel modelPanel = createConditionPanel("型号", modelComboBox);
        cdPanel.add(modelPanel);
        JPanel numberPanel = createConditionPanel("编号", numberTextField);
        cdPanel.add(numberPanel);
        JPanel bureauPanel = createConditionPanel("路局", bureauComboBox);
        cdPanel.add(bureauPanel);
        JPanel plantPanel = createConditionPanel("主机厂", plantComboBox);
        cdPanel.add(plantPanel);
        JPanel departmentPanel = createConditionPanel("动车所", departmentTextField);
        cdPanel.add(departmentPanel);
        JButton searchButton = new JButton("查询");
        searchButton.setFont(f);
        cdPanel.add(searchButton);
        EmuService emuService = new RemoteEmuServiceImpl(Constants.PASS_SEARCH_URL, 20000);

        //用于展示查询结果的表格
        JTable rsTable = new JTable(
                //初始表格
                new DefaultTableModel(Constants.RESULT_TABLE_COLUMN_NAME, 0)
        );

        rsTable.setFont(f);
        rsPanel.add(new JScrollPane(rsTable));

        //设置居中于屏幕
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        /*---------事件---------*/
        searchButton.addActionListener((e) -> {
            //check
            String number = numberTextField.getText();
            if (number.length() != 0 && number.length() != 4) {
                JOptionPane.showMessageDialog(this.frame, "车组号必须输入4位数字！", "警告", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Map<String, Object> cm = new HashMap<>();
            cm.put(Constants.NUMBER, number);
            cm.put(Constants.MODEL, modelComboBox.getSelectedItem());
            cm.put(Constants.BUREAU, bureauComboBox.getSelectedItem());
            cm.put(Constants.PLANT, plantComboBox.getSelectedItem());
            cm.put(Constants.DEPARTMENT, departmentTextField.getText());
            long start = System.currentTimeMillis();
            rsTable.setModel(emuService.searchTableModel(cm));
            System.out.println((System.currentTimeMillis() - start) + "ms");
            rsTb.setTitle("查询结果共" + rsTable.getRowCount() + "条");
            rsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
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

    private String[] loadCfg(String name, String regex) {
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(name)));
            int a = inputStream.available();
            byte[] buff = new byte[a];
            a = inputStream.read(buff);
            System.out.println("read " + a + " bytes...");
            String s = new String(buff);
            return s.split(regex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MainApp mainApp = new MainApp();
            mainApp.setVisible();
        });
    }
}
