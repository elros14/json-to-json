package com.silasclark.jsontojson.controllers;

import com.silasclark.jsontojson.services.FlattenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;

@RestController
public class FlattenController {
	
	
	@Autowired
	FlattenService service;
	
	
	@PostMapping("/flatten")
	public LinkedHashMap<String, Object> flatten(
		@RequestBody Object resource
	){
		
		return service.flatten(resource);
		
	}
	
	
	@GetMapping("/flattenService")
	public LinkedHashMap<String, Object> flatten(
		@RequestParam String url
	){
		
		return service.flatten(url);
		
	}
	
	
	@PostMapping("/reconstruct")
	public Object unFlatten(
		@RequestBody LinkedHashMap<String, Object> flatResource
	){
		
		return service.unFlatten(flatResource);
		
	}

}
