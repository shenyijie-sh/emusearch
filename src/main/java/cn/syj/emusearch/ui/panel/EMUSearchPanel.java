package cn.syj.emusearch.ui.panel;

import cn.syj.emusearch.constant.Constants;
import cn.syj.emusearch.service.EmuService;
import cn.syj.emusearch.service.impl.RemoteEmuServiceImpl;
import cn.syj.emusearch.ui.layout.GBC;
import cn.syj.emusearch.util.Utils;
import com.google.common.collect.Lists;
import org.jsoup.internal.StringUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * 动车组查询面板
 *
 * @author syj
 **/
public class EMUSearchPanel extends JPanel {

    private static final long serialVersionUID = -1L;

    private JTable resultTable;

    private final EmuService emuService = new RemoteEmuServiceImpl(Constants.PASS_SEARCH_URL);

    private String[] tmp = new String[6];

    public EMUSearchPanel() {
        initialize();
    }

    private void initialize() {
        GridBagLayout layout = new GridBagLayout();
        layout.rowWeights = new double[]{0.2, 0.8};
        layout.columnWeights = new double[]{1};
        this.setLayout(layout);
        this.add(getTopPanel(), new GBC(0, 0).setFill(GridBagConstraints.BOTH));
        this.add(getBottomPanel(), new GBC(0, 1).setFill(GridBagConstraints.BOTH).setInsets(5, 0, 0, 0));
    }

    private JPanel getTopPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                //Image image = icon.getImage();
                //g.drawImage(image, 0, 0, getSize().width, getSize().height, this);
                super.paintComponent(g);
            }
        };
        panel.setPreferredSize(new Dimension(625, 130));
        panel.setBorder(BorderFactory.createTitledBorder("查询条件"));
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        layout.columnWeights = new double[]{0.1, 0.3, 0.1, 0.3, 0.2};
        Font font = new Font("Microsoft YaHei", Font.PLAIN, 12);

        //型号选择框
        final JComboBox<String> modelComboBox = new JComboBox<>(new Vector<>(Lists.asList("全部", Utils.loadCfg("conf/emu-model.cfg", "\r\n"))));
        modelComboBox.setFont(font);
        //车组号输入框
        final JTextField numberTextField = new JTextField(4);
        numberTextField.setFont(font);
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
        final JComboBox<String> bureauComboBox = new JComboBox<>(new Vector<>(Lists.asList("全部", Utils.loadCfg("conf/cr-bureau.cfg", "\\|"))));
        bureauComboBox.setFont(font);
        //主机厂选择框
        final JComboBox<String> plantComboBox = new JComboBox<>(new Vector<>(Lists.asList("全部", Utils.loadCfg("conf/emu-plant.cfg", ","))));
        plantComboBox.setFont(font);
        //动车所选择框
        final JTextField departmentTextField = new JTextField(10);
        departmentTextField.setFont(font);
        JButton searchButton = new JButton("查询");
        searchButton.setFont( new Font("黑体",Font.BOLD,25));
        Insets insets = new Insets(3, 3, 3, 3);
        JButton clearCacheButton = new JButton("清理缓存");
        clearCacheButton.setFont(font);
        //把查询条件放入面板布局中
        JLabel modelLabel = new JLabel("型号");
        modelLabel.setFont(font);
        panel.add(modelLabel, new GBC(0, 0).setAnchor(GridBagConstraints.EAST).setInsets(insets));
        panel.add(modelComboBox, new GBC(1, 0).setFill(GridBagConstraints.BOTH).setInsets(insets));
        JLabel numberLabel = new JLabel("车组号");
        numberLabel.setFont(font);
        panel.add(numberLabel, new GBC(2, 0).setAnchor(GridBagConstraints.EAST).setInsets(insets));
        panel.add(numberTextField, new GBC(3, 0).setAnchor(GridBagConstraints.WEST).setInsets(insets));
        JLabel bureauLabel = new JLabel("路局");
        bureauLabel.setFont(font);
        panel.add(bureauLabel, new GBC(0, 1).setAnchor(GridBagConstraints.EAST).setInsets(insets));
        panel.add(bureauComboBox, new GBC(1, 1).setFill(GridBagConstraints.BOTH).setInsets(insets));
        JLabel plantLabel = new JLabel("主机厂");
        plantLabel.setFont(font);
        panel.add(plantLabel, new GBC(2, 1).setAnchor(GridBagConstraints.EAST).setInsets(insets));
        panel.add(plantComboBox, new GBC(3, 1).setFill(GridBagConstraints.BOTH).setInsets(insets));
        JLabel departmentLabel = new JLabel("动车所");
        departmentLabel.setFont(font);
        panel.add(departmentLabel, new GBC(0, 2).setAnchor(GridBagConstraints.EAST).setInsets(insets));
        panel.add(departmentTextField, new GBC(1, 2).setFill(GridBagConstraints.BOTH).setInsets(insets));
        panel.add(searchButton, new GBC(4, 0, 1, 2).setInsets(0, 10, 0, 0).setFill(GridBagConstraints.BOTH));
        panel.add(clearCacheButton,new GBC(4,2).setInsets(0, 10, 0, 0).setFill(GridBagConstraints.BOTH));
        searchButton.addActionListener(e -> {
            //check
            clearCacheButton.setEnabled(false);
            try {
                String number = numberTextField.getText();
                if (number.length() != 0 && number.length() != 4) {
                    JOptionPane.showMessageDialog(this, "车组号必须输入4位数字！", "警告", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Map<String, Object> cm = new HashMap<>();
                cm.put(Constants.NUMBER, number);
                cm.put(Constants.MODEL, modelComboBox.getSelectedItem());
                cm.put(Constants.BUREAU, bureauComboBox.getSelectedItem());
                cm.put(Constants.PLANT, plantComboBox.getSelectedItem());
                cm.put(Constants.DEPARTMENT, departmentTextField.getText());
                long start = System.currentTimeMillis();
                resultTable.setModel(emuService.searchTableModel(cm));
                System.out.println((System.currentTimeMillis() - start) + "ms");
                ((TitledBorder) ((JPanel) this.getComponent(1)).getBorder()).setTitle("查询结果共" + resultTable.getRowCount() + "条");
                resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                this.updateUI();
            }finally {
                clearCacheButton.setEnabled(true);
            }
        });

        clearCacheButton.addActionListener(e->{
            int confirm = JOptionPane.showConfirmDialog(this, "确认要清理缓存吗？", "警告", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.OK_OPTION) {
                this.emuService.clearCache();
                JOptionPane.showMessageDialog(this, "缓存清理完毕", "通知", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel getBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("查询结果"));

        //用于展示查询结果的表格
        resultTable = new JTable(
                //初始表格
                new DefaultTableModel(Constants.RESULT_TABLE_COLUMN_NAME, 0)
        );
        Font font = new Font("Microsoft YaHei", Font.PLAIN, 12);
        resultTable.setFont(font);
        JTableHeader tableHeader = resultTable.getTableHeader();
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        TableHeaderUI ui = tableHeader.getUI();
        panel.add(new JScrollPane(resultTable));

        return panel;
    }
}
