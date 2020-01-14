package com.ideature.agiletestingspring.loanapp.service;

import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.ExpectedException;

import com.ideature.agiletestingspring.loanapp.BaseJpaIntegrationTest;
import com.ideature.agiletestingspring.loanapp.LoanAppConstants;
import com.ideature.agiletestingspring.loanapp.domain.BorrowerDetails;
import com.ideature.agiletestingspring.loanapp.domain.FundingDetails;
import com.ideature.agiletestingspring.loanapp.domain.LoanDetails;
import com.ideature.agiletestingspring.loanapp.repository.BorrowerDetailsRepository;
import com.ideature.agiletestingspring.loanapp.repository.FundingDetailsRepository;
import com.ideature.agiletestingspring.loanapp.repository.LoanDetailsRepository;
import com.ideature.agiletestingspring.loanapp.repository.RepositoryException;

@RunWith(TestClassRunner.class)
public class FundingServiceImplSpringJpaTest extends BaseJpaIntegrationTest {

 	private static final Log log = LogFactory.getLog(FundingServiceImplSpringDITest.class);

 	private LoanDetailsRepository loanDetailsRepository = null;
 	private BorrowerDetailsRepository borrowerDetailsRepository = null;
 	private FundingDetailsRepository fundingDetailsRepository = null;

 	public void setLoanDetailsRepository(LoanDetailsRepository loanDetailsRepository) {
 		this.loanDetailsRepository = loanDetailsRepository;
 	}

  	public void setBorrowerDetailsRepository(BorrowerDetailsRepository borrowerDetailsRepository) {
 		this.borrowerDetailsRepository = borrowerDetailsRepository;
 	}

 	public void setFundingDetailsRepository(FundingDetailsRepository fundingDetailsRepository) {
 		this.fundingDetailsRepository = fundingDetailsRepository;
 	}

     @Before
     public void initialize() throws Exception {
         super.setUp();
     }

     @After
     public void cleanup() throws Exception {
         super.tearDown();
     }

     @Test
 	@ExpectedException(IllegalArgumentException.class)
 	public void testInvalidQuery() {
 		sharedEntityManager.createQuery("select test FROM TestTable test").executeUpdate();
 	}

     @Test
 	public void testApplicationManaged() {
 		EntityManager entityManager = entityManagerFactory.createEntityManager();
 		entityManager.joinTransaction();
 	}

     @Test
 	public void testJdbcQueryUsingSimpleJdbcTemplate() {
 		// Use simpleJdbcTemplate to get the loan count
 		int rowCount = simpleJdbcTemplate.queryForInt("SELECT COUNT(*) from LoanDetails");
 		assertEquals(rowCount,0);
 	}

     @Test
     public void testInsertLoanDetails() throws RepositoryException {
 		int loanCount = 0;
 		Collection loans = loanDetailsRepository.getLoans();
 		loanCount = loans.size();
 		assertTrue(loanCount==0);

 		long loanId = 200;
 		LoanDetails loanDetails = loanDetailsRepository.loadLoanDetails(loanId);
 		assertNull(loanDetails);

 		double loanAmount = 600000.0;
 		String loanStatus = LoanAppConstants.STATUS_IN_REVIEW;
 		String productGroup = "ARM";
 		long productId = 2345;
 		double purchasePrice = 700000.0;

 		// Add a new record
 		LoanDetails newLoan = new LoanDetails();
 		newLoan.setLoanId(loanId);
 		newLoan.setLoanAmount(loanAmount);
 		newLoan.setLoanStatus(loanStatus);
 		newLoan.setProductGroup(productGroup);
 		newLoan.setProductId(productId);
 		newLoan.setPurchasePrice(purchasePrice);

 		loanDetailsRepository.insertLoanDetails(newLoan);

 		loans = loanDetailsRepository.getLoans();
 		assertEquals(loanCount + 1, loans.size());
 	}

     @Test
     public void testLoanFunding() throws RepositoryException {

 		long loanId = 100;
 		// -------------------------------------------
 		// Insert LOAN details
 		// -------------------------------------------
 		Collection loans = loanDetailsRepository.getLoans();
 		log.debug("loans: " + loans.size());

 		// Add a new record
 		LoanDetails newLoan = new LoanDetails();
 		newLoan.setLoanId(loanId);
 		newLoan.setLoanAmount(450000);
 		newLoan.setLoanStatus("REQUESTED");
 		newLoan.setProductGroup("FIXED");
 		newLoan.setProductId(1234);
 		newLoan.setPurchasePrice(500000);

 		loanDetailsRepository.insertLoanDetails(newLoan);

 		loans = loanDetailsRepository.getLoans();
 		log.debug("After adding a new record - loans 2: " + loans.size());

 		// -------------------------------------------
 		// Insert BORROWER details
 		// -------------------------------------------
 		long borrowerId = 131;
  		Collection borrowers = borrowerDetailsRepository.getBorrowers();
 		log.debug("borrowers: " + borrowers.size());

 		// Add a new Borrower
 		BorrowerDetails newBorr = new BorrowerDetails();
 		newBorr.setBorrowerId(borrowerId);
 		newBorr.setFirstName("BOB");
 		newBorr.setLastName("SMITH");
 		newBorr.setPhoneNumber("123-456-7890");
 		newBorr.setEmailAddress("test.borr@abc.com");
 		newBorr.setLoanId(loanId);

 		borrowerDetailsRepository.insertBorrower(newBorr);

 		borrowers = borrowerDetailsRepository.getBorrowers();
 		log.debug("After adding a new record - borrowers2: " + borrowers.size());

 		// -------------------------------------------
 		// Insert FUNDING details
 		// -------------------------------------------
 		long fundingTxnId = 300;

 		Collection fundingDetailsList = fundingDetailsRepository.getFundingDetails();
 		log.debug("FundingDetails: " + fundingDetailsList.size());

 		// Add a new record
 		FundingDetails newFundingDetails = new FundingDetails();
 		newFundingDetails.setFundingTxnId(fundingTxnId);
 		newFundingDetails.setLoanId(loanId);
 		newFundingDetails.setFirstPaymentDate(new Date());
		newFundingDetails.setFundType(LoanAppConstants.FUND_TYPE_WIRE);
 		newFundingDetails.setLoanAmount(450000);
 		newFundingDetails.setMonthlyPayment(2500);
 		newFundingDetails.setTermInMonths(360);

		fundingDetailsRepository.insertFundingDetails(newFundingDetails);

 		fundingDetailsList = fundingDetailsRepository.getFundingDetails();
 		log.debug("After adding a new record - # of records: " + fundingDetailsList.size());

 		// Delete the borrower details record
 		borrowerDetailsRepository.deleteBorrower(borrowerId);

 		// Delete the funding details record
		fundingDetailsRepository.deleteFundingDetails(fundingTxnId);

 		// Delete loan details record last
 		loanDetailsRepository.deleteLoanDetails(loanId); 	}
  } 