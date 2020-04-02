package com.silasclark.jsontojson.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({"jsonSchemaMappings", "resource"})
public class JsonToJsonDTO {

    private Object resource;
    private List<JsonSchemaMapping> jsonSchemaMappings;


    @Data
    @JsonPropertyOrder({"sourceJsonPath", "targetJsonPath"})
    public static class JsonSchemaMapping{

        private String sourceJsonPath;
        private String targetJsonPath;

    }

}
