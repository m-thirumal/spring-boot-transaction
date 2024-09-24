/**
 * 
 */
package com.thirumal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirumal.dao.TransactionADao;
import com.thirumal.dao.TransactionBDao;
import com.thirumal.model.TransactionA;
import com.thirumal.model.TransactionB;

/**
 * @author Thirumal
 */
@Service
public class TransactionService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private TransactionADao transactionADao;
	private TransactionBDao transactionBDao;
	
	public TransactionService(TransactionADao transactionADao, TransactionBDao transactionBDao) {
		super();
		this.transactionADao = transactionADao;
		this.transactionBDao = transactionBDao;
	}

	@Transactional
	public String defaultRequiredOutside() {
		logger.debug("Inside Default @Transactional(Propagation.REQUIRED)");
		StringBuilder stringBuilder = new StringBuilder();
		transactionADao.insert(TransactionA.builder().data("@Transactional(Propagation.REQUIRED)").build());
		return stringBuilder.toString();
	}
	
	@Transactional
	public String defaultRequiredInner(StringBuilder stringBuilder) {
		logger.debug("Inside Default @Transactional(Propagation.REQUIRED) at 2nd method");
		transactionBDao.insert(TransactionB.builder().data("@Transactional(Propagation.REQUIRED)").build());
		return stringBuilder.toString();
	}
	
	
	
	
}
