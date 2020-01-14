package com.ideature.agiletestingspring.loanapp;

 import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
 public abstract class BaseDataSourceSpringContextIntegrationTest extends AbstractTransactionalDataSourceSpringContextTests {
 	private static final String[] configFiles = new String[]{"loanapp-applicationContext-jpa.xml"};

 	@Override
 	protected String[] getConfigLocations() {
 		return configFiles;
 	}
 } 