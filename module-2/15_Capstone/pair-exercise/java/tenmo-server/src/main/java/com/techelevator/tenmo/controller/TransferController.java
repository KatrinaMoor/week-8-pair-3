package com.techelevator.tenmo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.dao.UserSqlDAO;
import com.techelevator.tenmo.model.Transfer;

@RestController
@RequestMapping(path = "/transfers")
public class TransferController
{
	@Autowired
	UserSqlDAO userDao;
	@Autowired
	TransferDAO transferDao;

	@GetMapping("/{id}")
	public Transfer getById(@PathVariable int id)
	{
		return transferDao.get(id);
	}
	
	@PostMapping()
	public Transfer createTransfer(@RequestBody Transfer transfer)
	{
		return transferDao.create(transfer);
	}
	
}
