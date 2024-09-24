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

import com.thirumal.model.TransactionB;

/**
 * Generated using erm-postgresql-spring-boot project
 * @see <a href="https://github.com/M-Thirumal/">erm-postgresql-spring-boot</a>
 * @author erm-postgresql-spring-boot
 * @since 2024-09-23
 * @version 1.0
 */
@Repository
public class TransactionBDao {

	private static final String PK        = "transaction_b_id";

	
	private JdbcTemplate jdbcTemplate;
	
	public TransactionBDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public Long insert(TransactionB transactionb) {
		KeyHolder holder = new GeneratedKeyHolder();
		jdbcTemplate.update(con -> setPreparedStatement(transactionb, con.prepareStatement("INSERT INTO public.transaction_b(data) VALUES (?)",
					new String[] { PK })), holder);
		return holder.getKey().longValue();
	}

	public PreparedStatement setPreparedStatement(TransactionB transactionb, PreparedStatement ps) throws SQLException {
		if(transactionb.getData() == null) {
			ps.setObject(1, null);
		} else { 
			ps.setString(1, transactionb.getData());
		}
		return ps;
	}



	public Optional<TransactionB> get(Integer id) {
		try {
			return Optional.of(jdbcTemplate.queryForObject("SELECT * FROM public.transaction_a WHERE transaction_a_id = ?", transactionbRowMapper, 
				id
			));
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	public Optional<TransactionB> get(Integer id, String whereClause) {
		try {
			return Optional.of(jdbcTemplate.queryForObject("SELECT * FROM public.transaction_a WHERE transaction_a_id = ?", transactionbRowMapper, 
				id
			));
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	public List<TransactionB> list(Integer id) {
			return jdbcTemplate.query("SELECT * FROM public.transaction_a WHERE transaction_a_id = ?", transactionbRowMapper, 
				id
			);
	}

	public List<TransactionB> list(Integer id, String whereClause) {
			return jdbcTemplate.query("SELECT * FROM public.transaction_a WHERE transaction_a_id = ?", transactionbRowMapper, 
				id
			);
	}

	RowMapper<TransactionB> transactionbRowMapper = (rs, rowNum) -> {

		TransactionB transactionb = new TransactionB();

		transactionb.setTransactionBId(rs.getObject(PK) != null ? rs.getInt(PK) : null);

		transactionb.setData(rs.getObject("data") != null ? rs.getString("data") : null);

		transactionb.setRowCreatedOn(rs.getObject("row_created_on") != null ? rs.getObject("row_created_on", OffsetDateTime.class) : null);

		return transactionb;
	};

}


