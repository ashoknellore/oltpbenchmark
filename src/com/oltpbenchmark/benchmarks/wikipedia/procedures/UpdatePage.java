/*******************************************************************************
 * oltpbenchmark.com
 *  
 *  Project Info:  http://oltpbenchmark.com
 *  Project Members:    Carlo Curino <carlo.curino@gmail.com>
 *              Evan Jones <ej@evanjones.ca>
 *              DIFALLAH Djellel Eddine <djelleleddine.difallah@unifr.ch>
 *              Andy Pavlo <pavlo@cs.brown.edu>
 *              CUDRE-MAUROUX Philippe <philippe.cudre-mauroux@unifr.ch>  
 *                  Yang Zhang <yaaang@gmail.com> 
 * 
 *  This library is free software; you can redistribute it and/or modify it under the terms
 *  of the GNU General Public License as published by the Free Software Foundation;
 *  either version 3.0 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 ******************************************************************************/
package com.oltpbenchmark.benchmarks.wikipedia.procedures;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.oltpbenchmark.api.Procedure;
import com.oltpbenchmark.api.SQLStmt;
import com.oltpbenchmark.benchmarks.wikipedia.WikipediaConstants;
import com.oltpbenchmark.util.TimeUtil;

public class UpdatePage extends Procedure {
	
    // -----------------------------------------------------------------
    // STATEMENTS
    // -----------------------------------------------------------------
    
	public SQLStmt insertText = new SQLStmt(
        "INSERT INTO " + WikipediaConstants.TABLENAME_USER + " (" +
        "old_page,old_text,old_flags" + 
        ") VALUES (" +
        "?,?,'utf-8'" +
        ")"
    ); 
	public SQLStmt insertRevision = new SQLStmt(
        "INSERT INTO " + WikipediaConstants.TABLENAME_REVISION + " (" +
		"rev_page,rev_text_id,rev_comment,rev_minor_edit,rev_user," +
        "rev_user_text,rev_timestamp,rev_deleted,rev_len,rev_parent_id" +
		") VALUES (" +
        "?, ?, ? ,'0',?, ?, ? ,'0',?,?" +
		")"
	);
	public SQLStmt updatePage = new SQLStmt(
        "UPDATE " + WikipediaConstants.TABLENAME_PAGE +
        "   SET page_latest = ?, page_touched = ?, page_is_new = 0, page_is_redirect = 0, page_len = ? " +
        " WHERE page_id = ?"
    );
	public SQLStmt insertRecentChanges = new SQLStmt(
        "INSERT INTO " + WikipediaConstants.TABLENAME_RECENTCHANGES + " (" + 
	    "rc_timestamp, rc_cur_time, rc_namespace, rc_title, rc_type, " +
        "rc_minor, rc_cur_id, rc_user, rc_user_text, rc_comment, rc_this_oldid, " +
	    "rc_last_oldid, rc_bot, rc_moved_to_ns, rc_moved_to_title, rc_ip,rc_patrolled, " +
        "rc_new, rc_old_len, rc_new_len,rc_deleted, rc_logid," + 
        "rc_log_type,rc_log_action,rc_params" +
        ") VALUES (" +
        "?, ?, ? , ? ,'0','0', ? , ? , ? ,'', ? , ? ,'0','0','',?,'1','0', ? , ? ,'0','0',NULL,'',''" +
        ")"
    );
	public SQLStmt selectWatchList = new SQLStmt(
        "SELECT wl_user FROM " + WikipediaConstants.TABLENAME_WATCHLIST +
        " WHERE wl_title = ?" +
        "   AND wl_namespace = ?" +
		"   AND wl_user != ?" +
		"   AND wl_notificationtimestamp IS NULL"
    );
	public SQLStmt updateWatchList = new SQLStmt(
        "UPDATE " + WikipediaConstants.TABLENAME_WATCHLIST +
        "   SET wl_notificationtimestamp = ? " +
	    " WHERE wl_title = ?" +
	    "   AND wl_namespace = ?" +
	    "   AND wl_user = ?"
    );
	public SQLStmt selectUser = new SQLStmt(
        "SELECT * FROM " + WikipediaConstants.TABLENAME_USER + " WHERE user_id = ?"
    );
	public SQLStmt insertLogging = new SQLStmt(
        "INSERT INTO " + WikipediaConstants.TABLENAME_LOGGING + " (" +
		"log_type, log_action, log_timestamp, log_user, log_user_text, " +
        "log_namespace, log_title, log_page, log_comment, log_params" +
        ") VALUES (" +
        "'patrol','patrol',?,?,?,?,?,?,'',?" +
        ")"
    );
	public SQLStmt updateUserEdit = new SQLStmt(
        "UPDATE " + WikipediaConstants.TABLENAME_USER +
        "   SET user_editcount=user_editcount+1" +
        " WHERE user_id = ?"
    );
	public SQLStmt updateUserTouched = new SQLStmt(
        "UPDATE " + WikipediaConstants.TABLENAME_USER + 
        "   SET user_touched = ?" +
        " WHERE user_id = ?"
    );
	
    // -----------------------------------------------------------------
    // RUN
    // -----------------------------------------------------------------
	
	public void run(Connection conn, int textId, int pageId,
	                                 String pageTitle, String pageText, int pageNamespace,
	                                 int userId, String userIp, String userText,
	                                 int revisionId, String revComment) throws SQLException {

	    boolean adv;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    int param;
	    final String timestamp = TimeUtil.getCurrentTimeString14();
	    
	    // INSERT NEW TEXT
		ps = this.getPreparedStatementReturnKeys(conn, insertText, new int[]{1});
		param = 1;
		ps.setInt(param++, pageId);
		ps.setString(param++, pageText);
		ps.execute();

		rs = ps.getGeneratedKeys();
		adv = rs.next();
		assert(adv) : "Problem inserting new tuples in table text";
		int nextTextId = rs.getInt(1);
		rs.close();
		assert(nextTextId >= 0) : "Invalid nextTextId (" + nextTextId + ")";

		// INSERT NEW REVISION
		ps = this.getPreparedStatementReturnKeys(conn, insertRevision, new int[]{1});
		param = 1;
		ps.setInt(param++, pageId);
		ps.setInt(param++, nextTextId);
		ps.setString(param++, revComment);
		ps.setInt(param++, userId);
		ps.setString(param++, userText);
		ps.setString(param++, timestamp);
		ps.setInt(param++, pageText.length());
		ps.setInt(param++, revisionId);
		ps.executeUpdate();
		
		rs = ps.getGeneratedKeys();
		adv = rs.next();
		int nextRevId = rs.getInt(1);
		System.err.println(this.getDatabaseType() + " NEXT: " + nextRevId);
		rs.close();
		assert(nextRevId >= 0) : "Invalid nextRevID (" + nextRevId + ")";

		// I'm removing AND page_latest = "+a.revisionId+" from the query, since
		// it creates sometimes problem with the data, and page_id is a PK
		// anyway
		ps = this.getPreparedStatement(conn, updatePage);
		param = 1;
		ps.setInt(param++, nextRevId);
		ps.setString(param++, timestamp);
		ps.setInt(param++, pageText.length());
		ps.setInt(param++, pageId);
		int numUpdatePages = ps.executeUpdate();
		assert(numUpdatePages == 1) : "WE ARE NOT UPDATING the page table!";

		// REMOVED
		// sql="DELETE FROM `redirect` WHERE rd_from = '"+a.pageId+"';";
		// st.addBatch(sql);

		ps = this.getPreparedStatement(conn, insertRecentChanges);
		ps.setString(param++, timestamp);
		ps.setString(param++, timestamp);
		ps.setInt(param++, pageNamespace);
		ps.setString(param++, pageTitle);
		ps.setInt(param++, pageId);
		ps.setInt(param++, userId);
		ps.setString(param++, userText);
		ps.setInt(param++, nextTextId);
		ps.setInt(param++, textId);
		ps.setString(param++, userIp);
		ps.setInt(param++, pageText.length());
		ps.setInt(param++, pageText.length());
		int count = ps.executeUpdate();
		assert(count == 1);

		// REMOVED
		// sql="INSERT INTO `cu_changes` () VALUES ();";
		// st.addBatch(sql);

		// SELECT WATCHING USERS
		ps = this.getPreparedStatement(conn, selectWatchList);
		param = 1;
		ps.setString(param++, pageTitle);
		ps.setInt(param++, pageNamespace);
		ps.setInt(param++, userId);
		rs = ps.executeQuery();

		ArrayList<String> wlUser = new ArrayList<String>();
		while (rs.next()) {
			wlUser.add(rs.getString(1));
		}
		rs.close();

		// =====================================================================
		// UPDATING WATCHLIST: txn3 (not always, only if someone is watching the
		// page, might be part of txn2)
		// =====================================================================
		if (!wlUser.isEmpty()) {

			// NOTE: this commit is skipped if none is watching the page, and
			// the transaction merge with the following one
			conn.commit();

			ps = this.getPreparedStatement(conn, updateWatchList);
			ps.setString(1, timestamp);
			ps.setInt(3, pageNamespace);
			for (String t : wlUser) {
				ps.setString(2, pageTitle);
				ps.setString(4, t);
				ps.executeUpdate();
			}

			// NOTE: this commit is skipped if none is watching the page, and
			// the transaction merge with the following one
			conn.commit();

			// ===================================================================== 
			// UPDATING USER AND LOGGING STUFF: txn4 (might still be part of
			// txn2)
			// =====================================================================

			// This seems to be executed only if the page is watched, and once
			// for each "watcher"
			for (String t : wlUser) {
				ps = this.getPreparedStatement(conn, selectUser);
				param = 1;
				ps.setString(param++,t);
				rs = ps.executeQuery();
				rs.next();
				rs.close();
			}
		}

		// This is always executed, sometimes as a separate transaction,
		// sometimes together with the previous one
		
		ps = this.getPreparedStatement(conn, insertLogging);
		param = 1;
		ps.setString(param++, timestamp);
		ps.setInt(param++, userId);
		ps.setString(param++, pageTitle);
		ps.setInt(param++, pageNamespace);
		ps.setString(param++, userText);
		ps.setInt(param++, pageId);
		ps.setString(param++, String.format("%d\n%d\n%d", nextRevId, revisionId, 1));
		ps.executeUpdate();

		ps = this.getPreparedStatement(conn, updateUserEdit);
		param = 1;
		ps.setInt(param++, userId);
		ps.executeUpdate();
		
		ps = this.getPreparedStatement(conn, updateUserTouched);
		param = 1;
		ps.setString(param++, timestamp);
		ps.setInt(param++, userId);
		ps.executeUpdate();
	}
}
