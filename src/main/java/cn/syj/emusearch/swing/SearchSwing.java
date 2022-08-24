package cn.syj.emusearch.swing;

import cn.syj.emusearch.Entity.EmuTrain;
import cn.syj.emusearch.constant.Constants;
import cn.syj.emusearch.service.EmuService;
import cn.syj.emusearch.service.impl.RemoteEmuServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author syj
 * 2021-02-23 17:19
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
        EmuService emuService = new RemoteEmuServiceImpl(Constants.PASS_SEARCH_URL, 20000);
        searchButton.addActionListener((e) -> {
            Map<String,Object> cm = new HashMap<>();
            cm.put(Constants.MODEL, modelComboBox.getSelectedItem());
            cm.put(Constants.BUREAU, bureauComboBox.getSelectedItem());
            cm.put(Constants.PLANT, plantComboBox.getSelectedItem());
            cm.put(Constants.DEPARTMENT, departmentTextField.getText());
            List<EmuTrain> emus = emuService.searchEmuList(cm);
            emus.forEach(System.out::println);
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
