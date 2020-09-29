package com.apssouza.mytrade.runner;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("/api/test")
	public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name) {
		return "greeting alex!!";
	}
}
