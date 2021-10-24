package com.binderror.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class AdminRouter {

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String test() {
		String data = restTemplate.getForObject("http://172.21.0.7:9595/echoafter/500", String.class);
		return data;
	}

}
