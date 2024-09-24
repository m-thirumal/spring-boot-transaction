package com.thirumal.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.thirumal.model.TransactionA;

/**
 * Generated using erm-postgresql-spring-boot project
 * @see <a href="https://github.com/M-Thirumal/">erm-postgresql-spring-boot</a>
 * @author erm-postgresql-spring-boot
 * @since 2024-09-23
 * @version 1.0
 */
@Repository
public class TransactionADao {

	private static final String PK        = "transaction_a_id";

	private JdbcTemplate jdbcTemplate;
	
	public TransactionADao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public Long insert(TransactionA transactiona) {
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(con -> setPreparedStatement(transactiona, con.prepareStatement("INSERT INTO public.transaction_a(data) VALUES (?)",
					new String[] { PK })), holder);
		return holder.getKey().longValue();
	}

	public PreparedStatement setPreparedStatement(TransactionA transactiona, PreparedStatement ps) throws SQLException {
		if(transactiona.getData() == null) {
			ps.setObject(1, null);
		} else { 
			ps.setString(1, transactiona.getData());
		}
		return ps;
	}

	public Optional<TransactionA> get(Integer id) {
		try {
			return Optional.of(jdbcTemplate.queryForObject("SELECT * FROM public.transaction_a WHERE transaction_a_id = ?", transactionaRowMapper, 
				id
			));
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}


	public List<TransactionA> list(Integer id) {
			return jdbcTemplate.query("SELECT * FROM public.transaction_a WHERE transaction_a_id = ?", transactionaRowMapper, 
				id
			);
	}

	public List<TransactionA> list(Integer id, String whereClause) {
		return jdbcTemplate.query("SELECT * FROM public.transaction_a WHERE transaction_a_id = ?", transactionaRowMapper, 
				id
			);
	}


	RowMapper<TransactionA> transactionaRowMapper = (rs, rowNum) -> {

		TransactionA transactiona = new TransactionA();

		transactiona.setTransactionAId(rs.getObject(PK) != null ? rs.getInt(PK) : null);

		transactiona.setData(rs.getObject("data") != null ? rs.getString("data") : null);

		transactiona.setRowCreatedOn(rs.getObject("row_created_on") != null ? rs.getObject("row_created_on", OffsetDateTime.class) : null);

		return transactiona;
	};

}


