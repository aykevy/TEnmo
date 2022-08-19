package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    //New JDBC to talk to the database and get requested information via SQL
    private JdbcTemplate jdbcTemplate;

    //constructor for JDBC
    public JdbcTransferDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }



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

    //helper method to create the Account Object from rowset data.
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
