package com.silasclark.jsontojson.utils;

import org.springframework.web.client.RestTemplate;

public class ServiceUtil {
	
	
	public static Object getDataFromService(String url){
		
		RestTemplate restTemplate = new RestTemplate();
		//restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

		return restTemplate.getForObject(url, Object.class);
		
	}

}
