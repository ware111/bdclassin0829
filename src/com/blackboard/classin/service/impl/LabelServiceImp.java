package com.blackboard.classin.service.impl;

import com.blackboard.classin.mapper.LabelMapper;
import com.blackboard.classin.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LabelServiceImp implements LabelService {

    @Autowired
    private LabelMapper labelMapper;

    @Override
    public Map<String,String> getLabel(String value) {
        Map<String, String> valueMap = labelMapper.getLabelByValue(value);
        return valueMap;
    }
}
