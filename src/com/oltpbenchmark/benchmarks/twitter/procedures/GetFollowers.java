package com.oltpbenchmark.benchmarks.twitter.procedures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.oltpbenchmark.api.Procedure;
import com.oltpbenchmark.api.SQLStmt;
import com.oltpbenchmark.benchmarks.twitter.TwitterConstants;

public class GetFollowers extends Procedure {
	
    public final SQLStmt getFollowers = new SQLStmt(
        "SELECT f2 FROM " + TwitterConstants.TABLENAME_FOLLOWERS +
		" WHERE f1 = ? LIMIT " + TwitterConstants.LIMIT_FOLLOWERS
    );
    
    /** NOTE: The ?? is substituted into a string of repeated ?'s */
    public final SQLStmt getFollowerNames = new SQLStmt(
        "SELECT uid, name FROM " + TwitterConstants.TABLENAME_USER + 
        " WHERE uid IN (??)", TwitterConstants.LIMIT_FOLLOWERS
    );
    
    public ResultSet run(Connection conn, long uid) throws SQLException {
        PreparedStatement stmt = this.getPreparedStatement(conn, getFollowers);
        stmt.setLong(1, uid);
        ResultSet rs = stmt.executeQuery();
        
        stmt = this.getPreparedStatement(conn, getFollowerNames);
        int ctr = 0;
        long last = -1;
        while (rs.next() && ctr++ < TwitterConstants.LIMIT_FOLLOWERS) {
            last = rs.getLong(1);
            stmt.setLong(ctr, last);
        } // WHILE
        if (ctr > 0) {
            while (ctr++ < TwitterConstants.LIMIT_FOLLOWERS) {
                stmt.setLong(ctr, last);
            } // WHILE
            return stmt.executeQuery();
        }
        // LOG.warn("No followers for user : "+uid); //... so what ? 
        return (null);
    }

}
