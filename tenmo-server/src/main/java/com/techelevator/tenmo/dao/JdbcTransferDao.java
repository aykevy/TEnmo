package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    //New JDBC to talk to the database and get requested information via SQL
    private JdbcTemplate jdbcTemplate;

    //Constructor for JDBC
    public JdbcTransferDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    //Natalie's Edits For Transfer
    @Override
    public List<Transfer> list(int id) {
        List<Transfer> tranfersList = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM transfer " +
                "WHERE accountFrom = ? || accountTo == ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);
        //As long as there were results, add to list to be returned.
        if(results != null) {
            while (results.next()) {
                tranfersList.add(mapTransferRowSet(results));
            }
        return tranfersList;
    }
        return null;
    }

    //Kevin's Edits During Tech Session
    @Override
    public void withdraw(int id, int accountId, BigDecimal withdrawAmount)
    {
        String sql = "UPDATE account SET balance = balance - ? WHERE user_id = ? AND account_id = ?";
        jdbcTemplate.update(sql, withdrawAmount, id, accountId);
    }

    //Kevin's Edits During Tech Session
    @Override
    public void deposit(int id, int accountId, BigDecimal depositAmount)
    {
        String sql = "UPDATE account SET balance = balance + ? WHERE user_id = ? AND account_id = ?";
        jdbcTemplate.update(sql, depositAmount, id, accountId);
    }

    @Override
    public Transfer add(Transfer transfer)
    {
        String sql = "INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES(?, ?, ?, ?, ?) RETURNING transfer_id";
        int id = jdbcTemplate.update(sql, Integer.class, transfer.getTypeId(), transfer.getStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        transfer.setId(id);
        return transfer;
    }



    //Helper method to create the account object using row set data.
    public Transfer mapTransferRowSet(SqlRowSet rs){
        Transfer transfer = new Transfer();
        transfer.setId(rs.getInt("id"));
        transfer.setTypeId(rs.getInt("type"));
        transfer.setStatusId(rs.getInt("status"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        return transfer;
    }
}
