package com.clement.magichome.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clement.magichome.service.TelevisionSimulator;

@RestController
public class TVSimulatorController {

	@Resource
	TelevisionSimulator televisionSimulator;

	@RequestMapping("/pressonoff")
	public void pressOnOff() throws Exception {
		televisionSimulator.pressOnButton();
	}

}