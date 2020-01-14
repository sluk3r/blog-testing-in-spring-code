package com.ideature.agiletestingspring.loanapp;

import org.springframework.test.jpa.AbstractJpaTests;

public class BaseJpaIntegrationTest extends AbstractJpaTests {
 	private static final String[] configFiles = new String[]{"loanapp-applicationContext-jpa.xml"};

 	@Override
 	protected String[] getConfigLocations() {
 		return configFiles;
 	}
 } 