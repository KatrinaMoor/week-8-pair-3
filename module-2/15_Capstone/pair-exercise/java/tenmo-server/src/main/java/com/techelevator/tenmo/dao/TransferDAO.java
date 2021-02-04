package com.techelevator.tenmo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Transfer;

@Component
public class TransferDAO
{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public Transfer get(int id)
	{
		Transfer transfer = null;
		int accountFrom;
		int accountTo;
		String userFrom;
		String userTo;
		
		String sql = "SELECT t.transfer_id "
					+ "		, t.account_from "
					+ "		, t.account_to "
					+ "		, tt.transfer_type_desc AS type "
					+ "		, ts.transfer_status_desc AS status "
					+ "		, t.amount "
					+ "FROM transfers AS t "
					+ "JOIN transfer_types AS tt "
					+ "		ON t.transfer_type_id = tt.transfer_type_id "
					+ "JOIN transfer_statuses AS ts "
					+ "		ON t.transfer_status_id = ts.transfer_status_id "
					+ "WHERE t.transfer_id = ?; ";
		
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, id);
		
		if (row.next())
		{
			accountFrom = row.getInt("account_from");
			accountTo = row.getInt("account_to");
			
			String sqlUser = "SELECT u.username " + 
							"FROM users AS u " + 
							"JOIN accounts AS a " + 
							"    ON u.user_id = a.user_id " + 
							"WHERE a.account_id = ?;";
			
			SqlRowSet rowUserFrom = jdbcTemplate.queryForRowSet(sqlUser, accountFrom);
			userFrom = rowUserFrom.getString("username");
			
			SqlRowSet rowUserTo = jdbcTemplate.queryForRowSet(sqlUser, accountTo);
			userTo = rowUserTo.getString("username");
			
			transfer = mapRowToTransfer(row, userFrom, userTo);
		}
		return transfer;
	}
	
	private Transfer mapRowToTransfer(SqlRowSet row, String userFrom, String userTo)
	{
		Transfer transfer = new Transfer();
		
		transfer.setId(row.getInt("transfer_id"));
		transfer.setUserFrom(userFrom);
		transfer.setUserTo(userTo);
		transfer.setTransferType(row.getString("type"));
		transfer.setTransferStatus(row.getString("status"));
		transfer.setAmount(row.getBigDecimal("amount"));
		
		return transfer;
	}
}
