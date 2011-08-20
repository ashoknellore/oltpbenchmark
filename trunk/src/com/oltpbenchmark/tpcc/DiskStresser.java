/*******************************************************************************
 * oltpbenchmark.com
 *  
 *  Project Info:  http://oltpbenchmark.com
 *  Project Members:  	Carlo Curino <carlo.curino@gmail.com>
 * 				Evan Jones <ej@evanjones.ca>
 * 				DIFALLAH Djellel Eddine <djelleleddine.difallah@unifr.ch>
 * 				Andy Pavlo <pavlo@cs.brown.edu>
 * 				CUDRE-MAUROUX Philippe <philippe.cudre-mauroux@unifr.ch>  
 *  				Yang Zhang <yaaang@gmail.com> 
 * 
 *  This library is free software; you can redistribute it and/or modify it under the terms
 *  of the GNU General Public License as published by the Free Software Foundation;
 *  either version 3.0 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 ******************************************************************************/
package com.oltpbenchmark.tpcc;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class DiskStresser {

	Statement stmt;
	Random r;
	long low, high;
	String table;
	BufferedWriter out3;

	public DiskStresser(int table, long low, long high, BufferedWriter out3)
			throws SQLException {

		this.out3 = out3;
		this.table = "tb" + table;
		this.low = low;
		this.high = high;

		Properties ini = new Properties();
		try {
			ini.load(new FileInputStream(System.getProperty("prop")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Register jdbcDriver
		try {
			Class.forName(ini.getProperty("driver"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection conn = DriverManager.getConnection(ini.getProperty("conn"),
				ini.getProperty("user"), ini.getProperty("password"));
		conn.setAutoCommit(false);

		stmt = conn.createStatement();
		r = new Random();

	}

	public void close() throws SQLException {

		stmt.getConnection().close();

	}

	public static void main(String[] args) throws Exception {

		Properties ini = new Properties();
		try {
			ini.load(new FileInputStream(System.getProperty("prop")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Register jdbcDriver
		try {
			Class.forName(ini.getProperty("driver"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection conn = DriverManager.getConnection(ini.getProperty("conn"),
				ini.getProperty("user"), ini.getProperty("password"));
		conn.setAutoCommit(false);

		Statement stmt = conn.createStatement();

		// MEASURE INITIAL STATISTICS
		int initMeasureTime = 100; // seconds
		long oldPR = getStats(stmt, "Innodb_buffer_pool_reads");
		// long oldLR = getStats(stmt,"Innodb_buffer_pool_read_requests");
		Thread.sleep(initMeasureTime * 1000);
		long curPR = getStats(stmt, "Innodb_buffer_pool_reads");
		// long curLR = getStats(stmt,"Innodb_buffer_pool_read_requests");

		double avgPR = (double) (curPR - oldPR) / (double) initMeasureTime;
		// long avgLR = (curLR-oldLR)/initMeasureTime;

		System.out.println("#REFERENCE LOGICAL PR: " + avgPR); // +" "+avgLR +
																// " ratio: "
																// +(100*(double)avgPR)/(double)avgLR+"%");

		// PREPARE FOR BALOONING
		stmt.execute("CREATE DATABASE IF NOT EXISTS baloon");
		stmt.execute("use baloon");
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS baloon  ("
				+ "id int,"
				+ "a1 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a2 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a3 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a4 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a5 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a6 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a7 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a8 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a9 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a10 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a11 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a12 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a13 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a14 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a15 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a16 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a17 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a18 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a19 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a20 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a21 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a22 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a23 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a24 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a25 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a26 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a27 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a28 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a29 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a30 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a31 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\","
				+ "a32 char(250) default \"1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890\""
				+ ");");

		Random r = new Random();

		int maxchunks = 64; // total 1GB

		// GROW INCREASINGLY 1MB at a time up to maxchunks
		System.out.println("#Baloon-level (MB), Physical Reads (#)");
		System.out.println("0\t" + avgPR);

		long tts = 0;
		long tte;

		for (int i = 1; i <= maxchunks; i++) {

			// INSERT 16 MB WORTH OF DATA
			String sql = "INSERT INTO baloon (id) VALUES";
			for (int u = ((i - 1) * 2048); u < (((i - 1) * 2048) + 2047); u++) {
				sql += "(" + u + "),";
			}
			sql += "(" + (i * 2048) + ")";
			stmt.execute(sql);

			long tstart = System.currentTimeMillis();
			// EVERY 120 SECONDS
			for (int j = 0; j < 120; j++) {
				// EXECUTING SCANS OF OUR TABLES EVERY SECOND (TO KEEP THEM HOT)
				long wait = (1000 - (System.currentTimeMillis() - tstart));
				if (wait > 0)
					Thread.sleep(wait);
				else
					System.err.println("For j=" + j
							+ " took more than a second to loop!");

				// this returns exactly how many pages have been touched
				sql = "SELECT count(*)/2 FROM (SELECT id FROM baloon WHERE a"
						+ (1 + r.nextInt(32)) + "!=\"" + r.nextInt(1000)
						+ "\" LIMIT " + (i * 2048) + ") as tab;";

				// AND MEASURING RESPONSE TIME (IF LOW THEY ARE IN MEMORY)
				tstart = System.currentTimeMillis();
				ResultSet res2 = stmt.executeQuery(sql);
				long tend = System.currentTimeMillis();
				res2.next();
				int pages = res2.getInt(1);

				if (j == 20) {
					tts = System.currentTimeMillis();
					oldPR = getStats(stmt, "Innodb_buffer_pool_reads");
					// oldLR =
					// getStats(stmt,"Innodb_buffer_pool_read_requests");
				}
			}

			tte = System.currentTimeMillis();
			curPR = getStats(stmt, "Innodb_buffer_pool_reads");
			// curLR = getStats(stmt,"Innodb_buffer_pool_read_requests");

			double avgPRpsec = (double) (curPR - oldPR) / (double) (tte - tts);

			System.out.println(i * 16 + "\t" + avgPRpsec);

		}

	}

	public static long getStats(Statement stmt, String t) throws SQLException {
		ResultSet res = stmt
				.executeQuery("SHOW GLOBAL STATUS WHERE Variable_name=\"" + t
						+ "\" ");
		res.next();
		return res.getLong(2);
	}

	public long executeTransaction(int type) throws SQLException {
		String sql = null;

		if (type == 1) {
			long ra = (low + r.nextInt((int) (high - low)));
			sql = "SELECT distinct(concat(sha1(a1),id%3)) from " + table
					+ " where id >= " + ra + " and id < " + (ra + 100) + ";";
		} else
			sql = "UPDATE " + table + " SET cc=cc+1 WHERE id = "
					+ (low + r.nextInt((int) (high - low))) + " ;";

		// AND MEASURING RESPONSE TIME (IF LOW THEY ARE IN MEMORY)
		long tstart = System.currentTimeMillis();
		stmt.execute(sql);
		long tend = System.currentTimeMillis();
		return tend - tstart;
	}

	public static List<DiskStresser> getTerminals(int size, int numTerminals,
			BufferedWriter out3) throws SQLException {

		ArrayList<DiskStresser> s = new ArrayList<DiskStresser>();

		for (int j = 1; j <= size; j++) {
			for (int i = 0; i < numTerminals / size; i++) {

				int low = 1000000 / (numTerminals / size) * i;
				int high = 1000000 / (numTerminals / size) * (i + 1);

				s.add(new DiskStresser(j - 1, low, high, out3));
			}
		}
		return s;
	}

}
