package com.ideature.agiletestingspring.loanapp.service;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ideature.agiletestingspring.loanapp.LoanAppConstants;
import com.ideature.agiletestingspring.loanapp.LoanAppException;
import com.ideature.agiletestingspring.loanapp.domain.BorrowerDetails;
import com.ideature.agiletestingspring.loanapp.domain.FundingDetails;
import com.ideature.agiletestingspring.loanapp.domain.LoanDetails;
import com.ideature.agiletestingspring.loanapp.dto.FundingDTO;
import com.ideature.agiletestingspring.loanapp.repository.BorrowerDetailsRepository;
import com.ideature.agiletestingspring.loanapp.repository.FundingDetailsRepository;
import com.ideature.agiletestingspring.loanapp.repository.LoanDetailsRepository;
import com.ideature.agiletestingspring.loanapp.repository.RepositoryException;

public class FundingServiceImplIntegrationTest {
 	private static final Log log = LogFactory.getLog(FundingServiceImplIntegrationTest.class);

 	private static final String[] configFiles = new String[] {
 		"loanapp-applicationContext-jpa.xml"};

 	private ApplicationContext ctx = null;

 	private LoanDetailsRepository loanDetailsRepository = null;
 	private BorrowerDetailsRepository borrowerDetailsRepository = null;
 	private FundingDetailsRepository fundingDetailsRepository = null;
 	private FundingService fundingService;

 	@Before
 	public void setUp() {
 		ctx = new ClassPathXmlApplicationContext(configFiles);
 		log.debug("ctx: "+ctx);
 		loanDetailsRepository = (LoanDetailsRepository)ctx.getBean("loanDetailsRepository");
 		borrowerDetailsRepository = (BorrowerDetailsRepository)ctx.getBean("borrowerDetailsRepository");
 		fundingDetailsRepository = (FundingDetailsRepository)ctx.getBean("fundingDetailsRepository");
 		log.debug("loanDetailsRepository: "+loanDetailsRepository);

 		fundingService = (FundingService)ctx.getBean("fundingService");
 		log.debug("fundingService: " + fundingService);
 	}

 	@After
 	public void tearDown() {
 		fundingService = null;
 		loanDetailsRepository = null;
 		borrowerDetailsRepository = null;
 		fundingDetailsRepository = null;
 		ctx = null;
 		log.debug("ctx set null.");
 	}

 	@Test
 	public void testLoanFunding() {

 		// -------------------------------------------
 		// Set LOAN details
 		// -------------------------------------------
 		long loanId = 100;
 		LoanDetails loanDetails = new LoanDetails();
 		loanDetails.setLoanId(loanId);
 		loanDetails.setLoanAmount(450000);
 		loanDetails.setLoanStatus("REQUESTED");
 		loanDetails.setProductGroup("FIXED");
 		loanDetails.setProductId(1234);
 		loanDetails.setPurchasePrice(500000);

 		// -------------------------------------------
 		// Set BORROWER details
 		// -------------------------------------------
 		BorrowerDetails borrowerDetails = new BorrowerDetails();
 		long borrowerId = 131;
 		borrowerDetails.setBorrowerId(borrowerId);
 		borrowerDetails.setFirstName("BOB");
 		borrowerDetails.setLastName("SMITH");
 		borrowerDetails.setPhoneNumber("123-456-7890");
 		borrowerDetails.setEmailAddress("test.borr@abc.com");
 		borrowerDetails.setLoanId(loanId);

 		// -------------------------------------------
 		// Set FUNDING details
 		// -------------------------------------------
 		long fundingTxnId = 300;
 		FundingDetails fundingDetails = new FundingDetails();
 		fundingDetails.setFundingTxnId(fundingTxnId);
 		fundingDetails.setLoanId(loanId);
 		fundingDetails.setFirstPaymentDate(new Date());

		fundingDetails.setFundType(LoanAppConstants.FUND_TYPE_WIRE);
 		fundingDetails.setLoanAmount(450000);
 		fundingDetails.setMonthlyPayment(2500);
 		fundingDetails.setTermInMonths(360);

 		// Populate the DTO object
 		FundingDTO fundingDTO = new FundingDTO();
 		fundingDTO.setLoanDetails(loanDetails);
 		fundingDTO.setBorrowerDetails(borrowerDetails);
 		fundingDTO.setFundingDetails(fundingDetails);

 		try {
 			Collection loans = loanDetailsRepository.getLoans();
 			log.debug("loans: " + loans.size());
 			// At this time, there shouldn't be any loan records
 			assertEquals(0, loans.size());

 			Collection borrowers = borrowerDetailsRepository.getBorrowers();
 			log.debug("borrowers: " + borrowers.size());
 			// There shouldn't be any borrower records either
 			assertEquals(0, borrowers.size());

 			Collection fundingDetailsList = fundingDetailsRepository.getFundingDetails();
 			log.debug("FundingDetails: " + fundingDetailsList.size());
 			// There shouldn't be any fundingDetails records
 			assertEquals(0, fundingDetailsList.size());

 			// Call service method now
 			fundingService.processLoanFunding(fundingDTO);

 			// Assert that the new record has been saved to the DB.
 			loans = loanDetailsRepository.getLoans();
 			log.debug("After adding a new record - loans 2: " + loans.size());
 			// Now, there should be one loan record
 			assertEquals(1, loans.size());

 			borrowers = borrowerDetailsRepository.getBorrowers();
 			log.debug("After adding a new record - borrowers2: " + borrowers.size());
 			// Same with borrower record
 			assertEquals(1, borrowers.size());

 			fundingDetailsList = fundingDetailsRepository.getFundingDetails();
 			log.debug("After adding a new record - # of records: " + fundingDetailsList.size());
 			// Same with funding details record
 			assertEquals(1, fundingDetailsList.size());

 			// Now, delete the newly added records

 			// Delete the funding details record
 			fundingDetailsRepository.deleteFundingDetails(fundingTxnId);

 			// Delete the borrower details record
 			borrowerDetailsRepository.deleteBorrower(borrowerId);

 			// Delete loan details record last
 			loanDetailsRepository.deleteLoanDetails(loanId);

 		} catch (RepositoryException re) {
 			log.error("RepositoryException in testLoanFunding() method.", re);
 		} catch (LoanAppException lae) {
 			log.error("LoanAppException in testLoanFunding() method.", lae);
 		}
 	}
 } 