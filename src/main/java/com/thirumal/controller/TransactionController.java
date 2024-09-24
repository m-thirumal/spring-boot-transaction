/**
 * 
 */
package com.thirumal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thirumal.service.TransactionService;

/**
 * @author Thirumal
 */
@RestController
@RequestMapping("/transaction")
public class TransactionController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	private TransactionService transactionService;
	/**
	 * 
	 */
	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	@GetMapping("/default-required")
	public String defaultRequired(@RequestHeader(value = "User-Accept-Language", defaultValue = "en-IN") String locale) {
		logger.debug("Initating Default Transaction(Required)");
		return transactionService.defaultRequiredOutside();
	}
	

}
