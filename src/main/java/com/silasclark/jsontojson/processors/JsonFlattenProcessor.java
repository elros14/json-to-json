package com.silasclark.jsontojson.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonFlattenProcessor {

    public static final String DEFAULT_DELIMITER = ".";
    public static final String DEFAULT_ROOT_XPATH = "$";

    public static LinkedHashMap<String, Object> flatten(Object resource){

        return flatten(resource, new LinkedHashMap<String, Object>(), DEFAULT_ROOT_XPATH, DEFAULT_DELIMITER);

    }


    public static LinkedHashMap<String, Object> flatten(
            Object                        resource,
            LinkedHashMap<String, Object> flatResource,
            String                        xPath,
            String                        xPathDelimiter
    ){


        if (resource instanceof Map){

            @SuppressWarnings("unchecked")
            HashMap<String, Object> resourceAsMap = (HashMap<String, Object>) resource;

            resourceAsMap.forEach((String key, Object value) -> {

                if ((value instanceof List) || (value instanceof Map)){
                    flatten(value, flatResource, xPath + xPathDelimiter + key, xPathDelimiter);
                } else {
                    flatResource.put(xPath + xPathDelimiter + key, value);
                }

            });

        } else if (resource instanceof List){

            @SuppressWarnings("unchecked")
            ArrayList<Object> resourceAsList = (ArrayList<Object>) resource;

            int i = 0;

            for (Object value : resourceAsList){

                if ((value instanceof List) || (value instanceof Map)){
                    flatten(value, flatResource, xPath + xPathDelimiter + "array[" + i + "]", xPathDelimiter);
                }else{
                    flatResource.put(xPath + xPathDelimiter + "array[" + i + "]", value);
                }

                i++;
            }

        }

        return flatResource;
    }


    public static Object unFlatten(
            LinkedHashMap<String, Object> flatResource
    ){

        return unFlatten(flatResource, DEFAULT_DELIMITER);

    }


    public static Object unFlatten(
            LinkedHashMap<String, Object> flatResource,
            String                        xPathDelimiter
    ){

        LinkedHashMap<String, Object> unFlattenedResource = new LinkedHashMap<String, Object>();

        flatResource.forEach((String xPath, Object value) -> {

            ArrayList<String> xPathAsList = new ArrayList<>(Arrays.asList(xPath.split("\\" + xPathDelimiter)));
            traverseAndBuildStructuredResource(xPathAsList, unFlattenedResource, value);

        });

        return unFlattenedResource.get(DEFAULT_ROOT_XPATH);
    }



    private static void traverseAndBuildStructuredResource(
            ArrayList<String> xPathAsList,
            Object            resourceAsObj,
            Object            value
    ){

        Object localResourceAsObj = resourceAsObj;

        for (int i = 0; i < xPathAsList.size(); i++){

            String key = xPathAsList.get(i);
            String nextKey = new String();

            if (i == (xPathAsList.size() - 1)){
                addValueToCollection(localResourceAsObj, key, value);
                break;
            }else{
                nextKey = xPathAsList.get(i+1);
            }

            if (localResourceAsObj instanceof List){
                @SuppressWarnings("unchecked")
                ArrayListAnySize<Object> localResourceAsList = (ArrayListAnySize<Object>) localResourceAsObj;

                if (isKeyArray(key)){
                    int index = getIndexFromKeyArray(key);

                    if ((index < localResourceAsList.size()) && (localResourceAsList.get(index) != null)){
                        localResourceAsObj = localResourceAsList.get(index);
                    }else{
                        if (isKeyArray(nextKey)){
                            localResourceAsList.add(index, new ArrayListAnySize<Object>());
                        }else{
                            localResourceAsList.add(index, new LinkedHashMap<String, Object>());
                        }
                        localResourceAsObj = localResourceAsList.get(index);
                    }
                }
            } else if (localResourceAsObj instanceof Map){
                @SuppressWarnings("unchecked")
                HashMap<String, Object> localResourceAsMap = (HashMap<String, Object>) localResourceAsObj;

                if (!localResourceAsMap.containsKey(key)){
                    if (isKeyArray(nextKey)){
                        localResourceAsMap.put(key, new ArrayListAnySize<Object>());
                    }else{
                        localResourceAsMap.put(key, new LinkedHashMap<String, Object>());
                    }
                }

                localResourceAsObj = localResourceAsMap.get(key);
            }
        }

    }


    private static void addValueToCollection(Object collection, String key, Object value){

        if (collection instanceof List){
            @SuppressWarnings("unchecked")
            ArrayListAnySize<Object> collectionAsList = (ArrayListAnySize<Object>) collection;
            collectionAsList.add(getIndexFromKeyArray(key), value);
        }else if (collection instanceof Map){
            @SuppressWarnings("unchecked")
            HashMap<String, Object> collectionAsMap = (HashMap<String, Object>) collection;
            collectionAsMap.put(key, value);
        }
    }


    private static boolean isKeyArray(String key){

        if (key.length() < 6) return false;

        if ((key.substring(0, 6).equals("array[")) && (key.substring((key.length() - 1), key.length()).equals("]"))){
            return true;
        }else{
            return false;
        }

    }


    private static int getIndexFromKeyArray(String key){

        String indexStr = key.substring(key.indexOf("array[")+6, key.indexOf("]"));
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
