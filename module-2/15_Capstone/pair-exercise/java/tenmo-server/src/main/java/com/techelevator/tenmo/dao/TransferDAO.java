package com.techelevator.tenmo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;

import com.techelevator.tenmo.model.Transfer;

public class TransferDAO
{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public Transfer get(int id)
	{
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
			transfer = MapRowToTransfer(row);
		}
		return transfer;
	}
	
	private Transfer mapRowToTransfer(SqlRowSet row)
	{
		Transfer transfer = new Transfer();
		
		transfer.setId(row.getInt("transfer_id"));
		transfer.setTransferType(row.getString("type"));
		transfer.setTransferStatus(row.getString("status"));
		transfer.setAmount(row.getBigDecimal("amount"));
	}
}
