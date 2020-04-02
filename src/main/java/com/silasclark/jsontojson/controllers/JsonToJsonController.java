package com.silasclark.jsontojson.controllers;

import com.silasclark.jsontojson.dtos.JsonToJsonDTO;
import com.silasclark.jsontojson.services.JsonToJsonService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/json-to-json")
public class JsonToJsonController {

    private JsonToJsonService service;

    public JsonToJsonController(JsonToJsonService service){
        this.service = service;
    }


    @PostMapping("/mapJsonToJson")
    public Object mapJsonToJson(
            @RequestBody
            JsonToJsonDTO dto
    ) throws IOException {
        return this.service.mapJsonToJson(dto);
    }

}
