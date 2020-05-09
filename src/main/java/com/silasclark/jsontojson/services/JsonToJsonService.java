package com.silasclark.jsontojson.services;

import com.silasclark.jsontojson.dtos.JsonToJsonDTO;
import com.silasclark.jsontojson.dtos.JsonToJsonDTO.JsonSchemaMapping;
import com.silasclark.jsontojson.processors.JsonFlattenProcessor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JsonToJsonService {


    public Object mapJsonToJson(JsonToJsonDTO dto) throws IOException {

        LinkedHashMap<String, Object> sourceFlattenedResource = JsonFlattenProcessor.flatten(dto.getResource());
        LinkedHashMap<String, Object> targetFlattenedResource = new LinkedHashMap<>();

        Map<String, String> jsonMap =  convertListMappingToMap(dto.getJsonSchemaMappings());

        for (Map.Entry<String, Object> sourceEntry: sourceFlattenedResource.entrySet()){

            String sourceJsonPath = sourceEntry.getKey().substring(sourceEntry.getKey().indexOf(JsonFlattenProcessor.DEFAULT_DELIMITER)+1);
            if (sourceJsonPath.contains("array[")){
                String abstractJsonPath = sourceJsonPath.replaceAll("array\\[\\d+\\]", "array[*]");
                if (jsonMap.containsKey(abstractJsonPath)){
                    String enrichedTargetJsonPath = getEnrichedTargetJsonPath(sourceJsonPath, jsonMap.get(abstractJsonPath));
                    targetFlattenedResource.put("$." + enrichedTargetJsonPath, sourceEntry.getValue());
                }
            }else if(jsonMap.containsKey(sourceJsonPath)){
                targetFlattenedResource.put("$." + jsonMap.get(sourceJsonPath), sourceEntry.getValue());
            }

        }

        Object targetResource = JsonFlattenProcessor.unFlatten(targetFlattenedResource);

        return targetResource;

    }


    private Map<String, String> convertListMappingToMap(List<JsonSchemaMapping> mappings){

        Map<String, String> map = new LinkedHashMap<>();
        for (JsonSchemaMapping mapping : mappings){
            map.put(mapping.getSourceJsonPath(), mapping.getTargetJsonPath());
        }

        return map;
    }


    private String getEnrichedTargetJsonPath(
            String sourceJsonPath,
            String abstractTargetJsonPath
    ) throws IOException {

        List<String> allMatches = new LinkedList<String>();
        Matcher m = Pattern.compile("array\\[\\d+\\]") .matcher(sourceJsonPath);
        while (m.find()) {
            allMatches.add(m.group());
        }

        for(String match : allMatches){
            if (!abstractTargetJsonPath.contains("array[*]")){
                throw new IOException("Configured arrays do not match");
            }else{
                abstractTargetJsonPath = abstractTargetJsonPath.replaceFirst("array\\[\\*\\]", match);
            }
        }

        return abstractTargetJsonPath;

    }
}
