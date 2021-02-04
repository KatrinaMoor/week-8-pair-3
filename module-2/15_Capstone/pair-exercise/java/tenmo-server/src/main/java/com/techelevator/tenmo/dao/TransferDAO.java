package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

@Component
public class TransferDAO
{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private UserDAO userDao;
	
	public Transfer get(int id)
	{
		Transfer transfer = null;
		int accountFrom;
		int accountTo;
		User userFrom = null;
		User userTo = null;
		
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
			
			userFrom = userDao.getUserByAccount(accountFrom);
			userTo = userDao.getUserByAccount(accountTo);
			
			transfer = mapRowToTransfer(row, userFrom.getUsername(), userTo.getUsername());
		}
		return transfer;
	}
	
	public Transfer create(Transfer transfer)
	{
		int transferId = getNextId();
		int userFrom = userDao.findIdByUsername(transfer.getUserFrom());
		int userTo = userDao.findIdByUsername(transfer.getUserTo());
		int accountFrom = userDao.getAccountByUserId(userFrom);
		int accountTo = userDao.getAccountByUserId(userTo);
		int transferType = getStatusOrTypeId("type", transfer.getTransferType());
		int transferStatus = getStatusOrTypeId("status", transfer.getTransferStatus());
		BigDecimal amount = transfer.getAmount();
		
		String sql = "INSERT INTO transfers "
					+ "(transfer_id"
					+ ", transfer_type_id"
					+ ", transfer_status_id"
					+ ", account_from"
					+ ", account_to"
					+ ", amount) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?, ?);";
		
		jdbcTemplate.update(sql, transferId, transferType, transferStatus,
								accountFrom, accountTo, amount);
		
		Transfer newTransfer = get(transferId);
		
		return newTransfer;
	}
	
	private int getStatusOrTypeId(String statusOrType, String description)
	{
		int id = -1;
		
		String sql = "SELECT transfer_" + statusOrType + "_id "
					+ "FROM transfer_" + statusOrType + " "
					+ "WHERE transfer_" + statusOrType + "_desc = ?;";
		
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, description);
		
		if(row.next())
		{
			id = row.getInt("transfer_" + statusOrType + "_id");
		}
		
		return id;
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
	
	private int getNextId() {
		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('seq_transfer_id AS nextId')");
		if(nextIdResult.next()) {
			return nextIdResult.getInt("nextId");
		} else {
			throw new RuntimeException("Something went wrong while getting an id for the new transfer");
		}
	}
}
