package cn.syj.emusearch.swing;

import cn.syj.emusearch.model.EMU;
import cn.syj.emusearch.url.URLFactory;
import cn.syj.emusearch.util.HTMLUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @program: emusearch
 * @ClassName SearchSwing
 * @description:
 * @author: syj
 * @create: 2021-02-23 17:19
 **/
public class SearchSwing {

    public static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(false);

        JFrame frame = new JFrame("EMU-SEARCH");
        frame.setLayout(new BorderLayout());
        frame.setSize(700, 700);    //设置窗口显示尺寸
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //置窗口是否可以关闭

        Container container = frame.getContentPane();    //获取当前窗口的内容窗格
        JPanel condition = new JPanel(new FlowLayout());
        String[] typeList = new String[]{"CRH380AL", "CR400AF", "CR400AF-A", "CR400AF-B", "CR400BF", "CR400BF-A", "CR400BF-B"};
        String[] railwayBureauList = new String[]{"北京铁路局", "上海铁路局", "广州铁路集团"};
        String[] manufacturerList = new String[]{"BST", "南车青岛四方", "唐山轨道客车", "长春轨道客车"};
        final JComboBox<String> modelComboBox = new JComboBox<>(typeList);
        final JComboBox<String> bureauComboBox = new JComboBox<>(railwayBureauList);
        final JComboBox<String> plantComboBox = new JComboBox<>(manufacturerList);
        final JTextField departmentTextField = new JTextField(10);
        JPanel modelPanel = createConditionPanel("EMU model", modelComboBox);
        JPanel bureauPanel = createConditionPanel("Bureau", bureauComboBox);
        JPanel plantPanel = createConditionPanel("Plant", plantComboBox);
        JPanel departmentPanel = createConditionPanel("Department", departmentTextField);
        JButton searchButton = new JButton("查询");
        searchButton.addActionListener((e) -> {
            String model = (String) modelComboBox.getSelectedItem();
            String bureau = (String) bureauComboBox.getSelectedItem();
            String plant = (String) plantComboBox.getSelectedItem();
            String department = departmentTextField.getText();
            java.util.List<EMU> emuList = HTMLUtil.getEMUListFromInternet(URLFactory.searchByModel(model));
            emuList.removeIf(r -> !model.equals(r.getModel()) || !bureau.equals(r.getBureau()) || !plant.equals(r.getPlant()) || !department.equals(r.getDepartment()));
        });
        condition.add(modelPanel);
        condition.add(bureauPanel);
        condition.add(plantPanel);
        condition.add(departmentPanel);
        condition.add(searchButton);
        container.add(condition);
        frame.setVisible(true);
    }

    public static <C extends Component> JPanel createConditionPanel(String labelText, C component) {
        GridBagLayout gridBag = new GridBagLayout();
        JPanel jPanel = new JPanel(gridBag);
        JLabel jLabel = new JLabel(labelText);
        gridBag.addLayoutComponent(jLabel, new GridBagConstraints());
        gridBag.addLayoutComponent(component, new GridBagConstraints());
        jPanel.add(jLabel);
        jPanel.add(component);
        return jPanel;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(SearchSwing::createAndShowGUI);
    }
}
