package com.ideature.agiletestingspring.loanapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ideature.agiletestingspring.loanapp.service.FundingServiceImplIntegrationTest;
import com.ideature.agiletestingspring.loanapp.service.FundingServiceImplSpringDITest;
import com.ideature.agiletestingspring.loanapp.service.FundingServiceImplSpringJpaTest;
import com.ideature.agiletestingspring.loanapp.service.FundingServiceImplTest;

@RunWith(Suite.class)
@SuiteClasses(value = {
 		FundingServiceImplTest.class,
 		FundingServiceImplIntegrationTest.class,
 		FundingServiceImplSpringDITest.class,
 		FundingServiceImplSpringJpaTest.class
})
public class AllIntegrationTests {
} 