package cn.syj.emusearch.service;

import cn.syj.emusearch.entity.EmuTrain;

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
    List<EmuTrain> searchEmuList(Map<String, Object> conditionMap);


}
