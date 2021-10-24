package com.binderror;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/spring")
public class AdminRouter {

	@Autowired
	private RestTemplate restTemplate;

	@Value(value = "${echo.endpoint.baseapipath}")
	private String baseApiPath;

	@RequestMapping(value = "/syncapi/{delaytime}", method = RequestMethod.GET)
	public String test(@PathVariable final String delaytime) {
		String data = restTemplate.getForObject(baseApiPath + "/echo/hello/after/" + delaytime, String.class);
		return data;
	}

}
