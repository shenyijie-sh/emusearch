package cn.syj.emusearch.service;

import cn.syj.emusearch.entity.EmuTrain;

import javax.swing.table.TableModel;
import java.util.List;
import java.util.Map;

/**
 * @author syj
 **/
public interface EmuService {

    /**
     * 查询并返回一个动车组列表
     *
     * @param conditionMap 条件Map
     * @return 列表
     */
    List<EmuTrain> searchList(Map<String, Object> conditionMap);

    /**
     * 查询并返回一个包含动车组数据的{@link TableModel}
     *
     * @param conditionMap 条件Map
     * @return {@link TableModel}
     */
    TableModel searchTableModel(Map<String, Object> conditionMap);

}
