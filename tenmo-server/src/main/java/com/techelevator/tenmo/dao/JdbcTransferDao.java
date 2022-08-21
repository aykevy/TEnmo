package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException
            {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, transfer.getTypeId());
                ps.setInt(2, transfer.getStatusId());
                ps.setInt(3, transfer.getAccountFrom());
                ps.setInt(4, transfer.getAccountTo());
                ps.setBigDecimal(5, transfer.getAmount());
                return ps;
            }
        }, holder);
        Map<String, Object> transfer_id = holder.getKeys();
        transfer.setId((Integer)transfer_id.get("transfer_id"));
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
