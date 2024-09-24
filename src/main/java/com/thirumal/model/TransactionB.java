package com.thirumal.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * Generated using erm-postgresql-spring-boot project
 * @see <a href="https://github.com/M-Thirumal">erm-postgresql-spring-boot</a>
 * @author erm-postgresql-spring-boot
 * @since 2024-09-23
 * @version 1.0
 */
@Getter@Setter
@NoArgsConstructor@AllArgsConstructor
@ToString@Builder
public class TransactionB implements java.io.Serializable  {

	private static final long serialVersionUID = 2456448254252967587L;
	//Declaring fields
	private Integer transactionBId;
	private String data;
	@EqualsAndHashCode.Exclude
	private OffsetDateTime rowCreatedOn;

}
