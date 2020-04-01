package com.silasclark.jsontojson.services;

import com.silasclark.jsontojson.processors.JsonFlattenProcessor;
import com.silasclark.jsontojson.utils.ServiceUtil;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class FlattenService {

    public LinkedHashMap<String, Object> flatten(Object resource){

        return JsonFlattenProcessor.flatten(resource);

    }


    public Object unFlatten(LinkedHashMap<String, Object> flatResource){

        return JsonFlattenProcessor.unFlatten(flatResource);

    }


    public LinkedHashMap<String, Object> flatten(String url){

        Object resource = ServiceUtil.getDataFromService(url);
        return flatten(resource);

    }

}
