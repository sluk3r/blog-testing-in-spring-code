package com.ideature.agiletestingspring.loanapp.service;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.RunWith;

import com.ideature.agiletestingspring.loanapp.BaseDataSourceSpringContextIntegrationTest;
import com.ideature.agiletestingspring.loanapp.LoanAppConstants;
import com.ideature.agiletestingspring.loanapp.domain.LoanDetails;
import com.ideature.agiletestingspring.loanapp.repository.LoanDetailsRepository;
import com.ideature.agiletestingspring.loanapp.repository.RepositoryException;

@RunWith(TestClassRunner.class)
public class FundingServiceImplSpringDITest extends BaseDataSourceSpringContextIntegrationTest {

  	private static final Log log = LogFactory.getLog(FundingServiceImplSpringDITest.class);

 	private LoanDetailsRepository loanDetailsRepository = null;

 	public void setLoanDetailsRepository(LoanDetailsRepository loanDetailsRepository) {
 		this.loanDetailsRepository = loanDetailsRepository;
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
     public void testFindLoans() throws RepositoryException {
 		// First delete all the records from LoanDetails table
 		// by calling deleteFromTables() helper method.
 		deleteFromTables(new String[]{"LoanDetails"});
 		Collection loans = loanDetailsRepository.getLoans();
 		assertEquals(0, loans.size());
 	}

 	@Test
 	public void testJdbcQueryUsingJdbcTemplate() {
 		// Use jdbcTemplate to get the loan count
 		int rowCount = jdbcTemplate.queryForInt("SELECT COUNT(0) from LoanDetails");
 		assertEquals(rowCount,0);
 	}

  	@Test
 	public void testLoadLoanDetails() throws RepositoryException {
 		int rowCount = countRowsInTable("LOANDETAILS");
 		log.info("rowCount: " + rowCount);

 		long loanId = 100;
 		double loanAmount = 450000.0;
 		String loanStatus = LoanAppConstants.STATUS_REQUESTED;
 		String productGroup = "FIXED";
 		long productId = 1234;
 		double purchasePrice = 500000.0;

 		// Add a new record
 		LoanDetails newLoan = new LoanDetails();
 		newLoan.setLoanId(loanId);
 		newLoan.setLoanAmount(loanAmount);
 		newLoan.setLoanStatus(loanStatus);
 		newLoan.setProductGroup(productGroup);
 		newLoan.setProductId(productId);
 		newLoan.setPurchasePrice(purchasePrice);

 		// Insert a new record using jdbcTemplate helper attribute
 		jdbcTemplate.update("insert into LoanDetails (LoanId,ProductGroup,ProductId,LoanAmount,PurchasePrice," +
 				"PropertyAddress,LoanStatus) values (?,?,?,?,?,?,?)",
 				new Object[] { new Long(newLoan.getLoanId()),newLoan.getProductGroup(),new Long(newLoan.getProductId()),
 				new Double(newLoan.getLoanAmount()), new Double(newLoan.getPurchasePrice()),"123 MAIN STREET","IN REVIEW" });

 		// Explicitly end the transaction so the new record will be
 		// saved in the database table.
 		endTransaction();

 		// Start a new transaction to get a different unit of work (UOW)
 		startNewTransaction();

 		rowCount = countRowsInTable("LOANDETAILS");
 		log.info("rowCount: " + rowCount);

 		LoanDetails loanDetails1 = loanDetailsRepository.loadLoanDetails(loanId);
 		// We should get a null as the return value.
 		assertNull(loanDetails1);
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
 		log.info("loans.size(): " + loans.size());
 		System.out.println("loans.size(): " + loans.size());
 		assertEquals(loanCount + 1, loans.size());
 	}

 	@Test
 	public void testUpdateLoanDetails() throws Exception {
 		// First, insert a new record
 		long loanId = 100;
 		double loanAmount = 450000.0;
 		String oldStatus = LoanAppConstants.STATUS_FUNDING_COMPLETE;
 		String productGroup = "FIXED";
 		long productId = 1234;
 		double purchasePrice = 500000.0;
 		String propertyAddress = "123 MAIN STREET";

 		// Add a new record
 		LoanDetails newLoan = new LoanDetails();
 		newLoan.setLoanId(loanId);
 		newLoan.setLoanAmount(loanAmount);
 		newLoan.setLoanStatus(oldStatus);
 		newLoan.setProductGroup(productGroup); 		newLoan.setProductId(productId);
 		newLoan.setPurchasePrice(purchasePrice);
 		newLoan.setPropertyAddress(propertyAddress);

 		// Insert a new record using jdbcTemplate helper attribute
 		jdbcTemplate.update("insert into LoanDetails (LoanId,ProductGroup,ProductId,LoanAmount,PurchasePrice," +
 				"PropertyAddress,LoanStatus) values (?,?,?,?,?,?,?)",
 				new Object[] { new Long(newLoan.getLoanId()),newLoan.getProductGroup(),new Long(newLoan.getProductId()),
 				new Double(newLoan.getLoanAmount()), new Double(newLoan.getPurchasePrice()),newLoan.getPropertyAddress(),
 				newLoan.getLoanStatus() });

 		LoanDetails loanDetails1 = loanDetailsRepository.loadLoanDetails(loanId);
 		String status = loanDetails1.getLoanStatus();
 		assertEquals(status, oldStatus);

 		String newStatus = LoanAppConstants.STATUS_FUNDING_DENIED;

 		// Update status field
 		loanDetails1.setLoanStatus(newStatus);
 		loanDetailsRepository.updateLoanDetails(loanDetails1);
 		status = loanDetails1.getLoanStatus();
 		assertEquals(status, newStatus);
 	}
 } 