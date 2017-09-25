/*
 * Copyright (c) 2014 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement.
 *  
 */

package de.dfki.irl.darby.prediction.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.sql.Timestamp;

import de.dfki.irl.darby.prediction.accumulation.results.AccumulationResult;
import de.dfki.irl.darby.prediction.helper.SAPTimestamp;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;
import de.dfki.irl.darby.prediction.helper.cloud.Cloud;
import de.dfki.irl.darby.prediction.matching.bonmatching.BonInfo;
import de.dfki.irl.darby.prediction.matching.bonmatching.EnhancedBonList;
import de.dfki.irl.darby.prediction.matching.bonmatching.TraceInfo;
import de.dfki.irl.darby.prediction.matching.bonmatching.UPurchase;

public class HanaDatabase implements Database {

	private final Connection con;
	private final String rawTracesTable = "SAPCAR.Z_GL_TRACKING";
	private final String targetTracesTable = "SAPCAR.Z_GL_TRACES";

	public HanaDatabase() throws SQLException {
		// Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:sap://hpl-19-06:34215/";
		String user = "system";
		String password = "Sapcoe06";

		con = DriverManager.getConnection(url, user, password);
		con.setAutoCommit(false);
	}

	public ResultSet executeQuery(String query) {
		ResultSet rs = null;
		try {
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return rs;
	}

	@Override
	public ArrayList<Integer> getTraceIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Trace getTraceByTraceId(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerCallback(ResultCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAllTraces() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterCallback(ResultCallback callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeAccumulationresults(ArrayList<AccumulationResult> res) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<BareCloud> getCloudsByTrace(int traceID, double minScore) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getAllClouds() {
		// TODO Auto-generated method stub

	}

	@Override
	public void storeClouds(Collection<Cloud> clouds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAllProducts() {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveTrace(Trace t) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<TraceInfo> getTraceInfos(long startTimestamp,
			long endTimestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<Long, ArrayList<BonInfo>> getRawBons(Long startTime,Long endTime) {
		String query = "select distinct transnumber,begintimestamp,workstationid from sapcar.\"/POSDW/TLOGF\" where retailstoreid = '0000291006' and \"BEGINTIMESTAMP\">"
				+ startTime.toString()
				+ " and \"BEGINTIMESTAMP\"<"
				+ endTime.toString() + " order by begintimestamp";
		ResultSet rs = executeQuery(query);

		System.out.println(query);
		HashMap<Long, ArrayList<BonInfo>> ret = new HashMap<Long, ArrayList<BonInfo>>();
		try {
			while (rs.next()) {
				BonInfo bonInfo = createBonInfo(rs);
				addToList(ret, bonInfo.getWorkstationID(), bonInfo);
			}

			return ret;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void addToList(HashMap<Long, ArrayList<BonInfo>> ret,
			long workstationID, BonInfo bonInfo) {

		ArrayList<BonInfo> list = ret.get(workstationID);

		if (list == null) {
			list = new ArrayList<BonInfo>();
			ret.put(workstationID, list);
		}

		list.add(bonInfo);
	}

	private BonInfo createBonInfo(ResultSet rs) {
		// TODO Auto-generated method stub
		long bonId;
		try {
			bonId = rs.getLong(1);
			long beginTime = rs.getLong(2);
			long workstation = rs.getLong(3);

			return new BonInfo(-1, bonId, beginTime, workstation, null, null,
					null);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	@Override
	public void writeBonInfos(ArrayList<BonInfo> infos) {
		try {
			String columns = "\"traceID\", \"transnumber\", \"timestamp\", \"workstationID\", \"matchingtype\", \"ean\", \"quantity\"";
			String query = String.format(
					"INSERT INTO %s (%s) VALUES (?,?,?,?,?,?,?)",
					"sapcar.\"Z_GL_BonInfos\"", columns);

			PreparedStatement stmt = con.prepareStatement(query);

			for (BonInfo info : infos) {

				if (info.getMaterials() == null) {
					// raw bon without ean and stuff

					SAPTimestamp stamp = SAPTimestamp.fromLong(info
							.getTimestamp());

					stmt.setLong(1, info.getTraceId());
					stmt.setLong(2, info.getBonID());
					stmt.setLong(3, stamp.toEpoch());
					stmt.setLong(4, info.getWorkstationID());
					stmt.setString(5, info.getMatchingType());
					stmt.setLong(6, 666l);
					stmt.setDouble(7, -1);
					stmt.addBatch();
				} else {

					for (int i = 0; i < info.getMaterials().size(); i++) {
						stmt.setLong(1, info.getTraceId());
						stmt.setLong(2, info.getBonID());
						stmt.setLong(3, info.getTimestamp());
						stmt.setLong(4, info.getWorkstationID());
						stmt.setString(5, info.getMatchingType());
						stmt.setLong(6, info.getMaterials().get(i));
						stmt.setDouble(7, info.getQuantities().get(i));
						stmt.addBatch();
					}
				}
			}
			stmt.executeBatch();
			con.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public ArrayList<TraceInfo> getTraceInfosByPaytime(long startTime,
			long endTime) {
		// TODO Auto-generated method stub
		return getTraceInfosStatement("SELECT * from sapcar.\"Z_GL_TRACEINFO\" where \"payTime\" > "
				+ startTime
				+ " and \"payTime\" <"
				+ endTime
				+ " order by \"payTime\"");
	}

	private ArrayList<TraceInfo> getTraceInfosStatement(String statement) {
		ResultSet rs = executeQuery(statement);
		ArrayList<TraceInfo> infos = new ArrayList<TraceInfo>();
		try {

			while (rs.next()) {

				TraceInfo t = buildTraceInfo(rs);
				infos.add(t);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return infos;
	}

	private TraceInfo buildTraceInfo(ResultSet rs) {
		try {
			int traceId = rs.getInt(1);
			double time = rs.getDouble(2);
			double distance = rs.getDouble(3);
			int desk = rs.getInt(4);
			long payTime = rs.getLong(5);
			long startTime = rs.getLong(6);
			long endTime = rs.getLong(7);
			double biggestJump = rs.getDouble(10);
			double averageJump = rs.getDouble(11);
			return new TraceInfo(traceId, desk, payTime, startTime, endTime,
					payTime, distance, biggestJump, averageJump);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Iterable<BonInfo> getRawBons(Long start, Long end,Iterable<Long> transnumbers) {

		String str_transnumbers = null;

		for (Long number : transnumbers) {
			if (str_transnumbers == null) {
				str_transnumbers = number + "";
			} else {
				str_transnumbers += "," + number;
			}
		}

		String queryString = "select \"TRANSNUMBER\",\"BEGINTIMESTAMP\",\"WORKSTATIONID\",\"ITEMID\",\"RETAILQUANTITY\" from sapcar.\"/POSDW/TLOGF\" where \"BEGINTIMESTAMP\">"
				+ start
				+ " and \"BEGINTIMESTAMP\"<"
				+ end
				+ " and \"RETAILSTOREID\" = '0000291006'  and \"ITEMID\" <>'' and \"TRANSNUMBER\" in ("
				+ str_transnumbers + ") order by \"TRANSNUMBER\"";

		System.out.println(queryString);
		ResultSet rs = executeQuery(queryString);

		ArrayList<BonInfo> ret = new ArrayList<BonInfo>();
		try {
			while (rs.next()) {
				BonInfo bonInfo = createBonInfo(rs);

				// Hack: list contains only a single ean/quantity always

				ArrayList<Long> eans = new ArrayList<Long>();
				eans.add(rs.getLong(4));
				bonInfo.setMaterials(eans);
				// Hack: list contains only a single ean/quantity always

				ArrayList<Double> quant = new ArrayList<Double>();

				quant.add(rs.getDouble(5));
				bonInfo.setQuantities(quant);

				ret.add(bonInfo);
			}

			return ret;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public EnhancedBonList getEnhancedBons() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeBonMatchings(HashMap<Long, String> matchings) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void writeBonMatchingsDistance(HashMap<Long, Double> matchingsDistance) {
		
		}
	

	@Override
	public BonInfo getBonForTrace(long traceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<UPurchase> getUPurForTrace(long traceId) {
		// TODO Auto-generated method stub
		return null;
	}

}
