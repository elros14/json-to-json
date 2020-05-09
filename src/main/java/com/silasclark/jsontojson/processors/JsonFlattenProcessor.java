package com.silasclark.jsontojson.processors;

import java.util.*;

public class JsonFlattenProcessor {

    public static final String DEFAULT_DELIMITER = ".";
    public static final String DEFAULT_ROOT_PATH = "$";

    public static LinkedHashMap<String, Object> flatten(Object resource){

        return flatten(resource, new LinkedHashMap<String, Object>(), DEFAULT_ROOT_PATH, DEFAULT_DELIMITER);

    }


    public static LinkedHashMap<String, Object> flatten(
            Object                        resource,
            LinkedHashMap<String, Object> flatResource,
            String                        jsonPath,
            String                        pathDelimiter
    ){


        if (resource instanceof Map){

            @SuppressWarnings("unchecked")
            HashMap<String, Object> resourceAsMap = (HashMap<String, Object>) resource;

            resourceAsMap.forEach((String key, Object value) -> {

                if ((value instanceof List) || (value instanceof Map)){
                    flatten(value, flatResource, jsonPath + pathDelimiter + key, pathDelimiter);
                } else {
                    flatResource.put(jsonPath + pathDelimiter + key, value);
                }

            });

        } else if (resource instanceof List){

            @SuppressWarnings("unchecked")
            ArrayList<Object> resourceAsList = (ArrayList<Object>) resource;

            int i = 0;

            for (Object value : resourceAsList){

                if ((value instanceof List) || (value instanceof Map)){
                    flatten(value, flatResource, jsonPath + "[" + i + "]", pathDelimiter);
                }else{
                    flatResource.put(jsonPath + "[" + i + "]", value);
                }

                i++;
            }

        }

        return flatResource;
    }


    public static Object unFlatten(
            LinkedHashMap<String, Object> flatResource
    ){

        LinkedHashMap<String, Object> unFlattenedResource = new LinkedHashMap<String, Object>();

        flatResource.forEach((String jsonPath, Object value) -> {

            LinkedList<String> jsonPathAsList = new LinkedList<>(Arrays.asList(jsonPath.split("\\.")));
            traverseAndBuildStructuredResource(jsonPathAsList, jsonPathAsList, unFlattenedResource, value);

        });

        return unFlattenedResource.get(DEFAULT_ROOT_PATH);
    }



    private static void traverseAndBuildStructuredResource(
            List<String>        origJsonPathAsList,
            List<String>        jsonPathAsList,
            Map<String, Object> resourceAsMap,
            Object              value
    ){

        if (jsonPathAsList.size() == 0) return;
        if (jsonPathAsList.size() == 1){
            addValueToCollection(resourceAsMap, jsonPathAsList.get(0), value);
            return;
        }

        String key = jsonPathAsList.get(0);
        if (isKeyArray(key)){
            int index = getIndexFromKeyArray(key);
            key = getKeyFromKeyArray(key);
            if (!resourceAsMap.containsKey(key)){
                resourceAsMap.put(key, new ArrayListAnySize<LinkedHashMap>());
            }
            ArrayListAnySize<LinkedHashMap> resourceAsList = (ArrayListAnySize<LinkedHashMap>) resourceAsMap.get(key);
            if (resourceAsList.size() <= index){
                resourceAsList.add(index, new LinkedHashMap<String, Object>());
            }
            jsonPathAsList.remove(0);
            traverseAndBuildStructuredResource(origJsonPathAsList, jsonPathAsList, resourceAsList.get(index), value);
        }else{

            if (!resourceAsMap.containsKey(key)){
                resourceAsMap.put(key, new LinkedHashMap<>());
            }
            jsonPathAsList.remove(0);
            traverseAndBuildStructuredResource(origJsonPathAsList, jsonPathAsList, (Map)resourceAsMap.get(key), value);

        }

    }


    private static void addValueToCollection(
            Map<String, Object> resourceAsMap,
            String key,
            Object value
    ){

        if (isKeyArray(key)) {

            int index = getIndexFromKeyArray(key);
            key = getKeyFromKeyArray(key);

            if (!resourceAsMap.containsKey(key)) {
                resourceAsMap.put(key, new ArrayListAnySize<>());
            }
            ArrayListAnySize<Object> list = (ArrayListAnySize) resourceAsMap.get(key);
            list.add(index, value);

        } else{
            resourceAsMap.put(key, value);
        }
    }


    private static boolean isKeyArray(String key){

        if (key.contains("[") && key.contains("]")){
            return true;
        }else{
            return false;
        }

    }


    private static String getKeyFromKeyArray(String key){
        return key.substring(0, key.indexOf("["));
    }


    private static int getIndexFromKeyArray(String key){

        String indexStr = key.substring(key.indexOf("[")+1, key.indexOf("]"));
        return Integer.parseInt(indexStr);

    }


    public static class ArrayListAnySize<E> extends ArrayList<E>{

        private static final long serialVersionUID = -5136114147878757495L;

        @Override
        public void add(int index, E element){

            int insertNulls = index - size();

            for (int i = 0; i <= insertNulls; i++){
                super.add(null);
            }
            super.set(index, element);

        }
    }

}
