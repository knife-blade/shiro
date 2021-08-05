package com.touchealth.platform.processengine.service.impl.datastatistics;

import com.touchealth.platform.processengine.dao.datastatistics.ModuleBoardDao;
import com.touchealth.platform.processengine.service.datastatistics.ModuleBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Service("moduleBoardService")
public class ModuleBoardServiceImpl implements ModuleBoardService {

    @Autowired
    private ModuleBoardDao moduleBoardDao;

}
