package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcAccountDao implements AccountDao{
    //New JDBC to talk to the database and get requested information via SQL
    private JdbcTemplate jdbcTemplate;

    //constructor for JDBC
    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }


    //Jacob's edit during our first session
    @Override
    //Overide the interface and request data via SQL
    public List<Account> list(int id) {
        List<Account> accountsList = new ArrayList<>();
        String sql = "SELECT a.account_id,a.user_id,a.balance " +
                "FROM account AS a " +
                "JOIN tenmo_user AS t " +
                "USING (user_id) " +
                "WHERE t.user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        //As long as there were results, add to list to be returned.
        while (results.next()) {
            accountsList.add(mapAccountRowSet(results));
        }
        return accountsList;
    }


    //helper method to create the Account Object from rowset data.
    public Account mapAccountRowSet(SqlRowSet rs){
        Account account = new Account();
        account.setBalance(rs.getBigDecimal("balance"));
        account.setUserId(rs.getInt("user_id"));
        account.setAccountId(rs.getInt("account_id"));
        return account;
    }
}
