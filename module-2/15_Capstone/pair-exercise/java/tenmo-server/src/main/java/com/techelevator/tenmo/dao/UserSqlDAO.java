package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserSqlDAO implements UserDAO {

    private static final double STARTING_BALANCE = 1000;
    private JdbcTemplate jdbcTemplate;
    private TransferDAO transferDao;
    
    public UserSqlDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        return jdbcTemplate.queryForObject("select user_id from users where username = ?", int.class, username);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.user_id "
    				+ ", u.username "
    				+ ", u.password_hash "
    				+ ", a.balance "
        			+ "FROM users AS u "
        			+ "JOIN accounts AS a "
        			+ "	ON u.user_id = a.user_id;";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }

        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        for (User user : this.findAll()) {
            if( user.getUsername().toLowerCase().equals(username.toLowerCase())) {
                return user;
            }
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public User findByUserId(int id) throws UsernameNotFoundException {
        for (User user : this.findAll()) {
            if( user.getId() == id) {
                return user;
            }
        }
        throw new UsernameNotFoundException("User ID " + id + " was not found.");
    }

    @Override
    public User getUserByAccount(int id)
    {
    	User user = null;
    	
    	String sql = "SELECT u.user_id "
    			+ "FROM users AS u " 
    			+ "JOIN accounts AS a " 
    			+ "    ON u.user_id = a.user_id " 
    			+ "WHERE a.account_id = ?;";
    	
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, id);

		if(row.next())
		{
			int userId = row.getInt("user_id");
			user = findByUserId(userId);
		}
		
		return user;
    }

    @Override
    public boolean create(String username, String password) {
        boolean userCreated = false;
        boolean accountCreated = false;

        // create user
        String insertUser = "insert into users (username,password_hash) values(?,?)";
        String password_hash = new BCryptPasswordEncoder().encode(password);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String id_column = "user_id";
        userCreated = jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(insertUser, new String[]{id_column});
                    ps.setString(1, username);
                    ps.setString(2,password_hash);
                    return ps;
                }
                , keyHolder) == 1;
        int newUserId = (int) keyHolder.getKeys().get(id_column);

        // create account
        String insertAccount = "insert into accounts (user_id,balance) values(?,?)";
        accountCreated = jdbcTemplate.update(insertAccount,newUserId,STARTING_BALANCE) == 1;

        return userCreated && accountCreated;
    }

    public List<Transfer> getTransfersByUser(int userId)
    {
    	List<Transfer> transfers = new ArrayList<Transfer>();
    	int accountId = getAccountByUserId(userId);
    	
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
				+ "WHERE t.account_from = ? "
				+ "	OR t.account_to = ?; ";

    	SqlRowSet row = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
    	
        while(row.next()) {
            Transfer transfer = transferDao.get(row.getInt("transfer_id"));
            transfers.add(transfer);
        }
    	
    	return transfers;
    }
    
    @Override
    public int getAccountByUserId(int id)
    {
    	int accountId = -1;
    	
    	String sql = "SELECT a.account_id "
    			+ "FROM accounts AS a " 
    			+ "JOIN users AS u " 
    			+ "    ON a.user_id = u.user_id " 
    			+ "WHERE u.user_id = ?;";
    	
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, id);

		if(row.next())
		{
			accountId = row.getInt("account_id");
		}
		
		return accountId;
    }
    
    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("ROLE_USER");
        user.setBalance(rs.getBigDecimal("balance"));
        return user;
    }
}
