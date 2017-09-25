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
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.sql.Timestamp;

import de.dfki.irl.darby.prediction.accumulation.results.AccumulationResult;
import de.dfki.irl.darby.prediction.helper.SAPTimestamp;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;
import de.dfki.irl.darby.prediction.helper.cloud.Cloud;
import de.dfki.irl.darby.prediction.matching.bonmatching.BonInfo;
import de.dfki.irl.darby.prediction.matching.bonmatching.EnhancedBonList;
import de.dfki.irl.darby.prediction.matching.bonmatching.TraceInfo;
import de.dfki.irl.darby.prediction.matching.bonmatching.UPurchase;
import de.dfki.irl.darby.prediction.products.Product;

public class MySQLDatabase implements Database {

	private final Connection con;
	private final String rawTracesTable = "locations";
	private final String targetTracesTable = "traces";
	private final String tracesAccumTable = "traceInfo";
	private final ArrayList<ResultCallback> callbacks = new ArrayList<ResultCallback>();

	public MySQLDatabase() throws SQLException {
		// Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost/quuppa?rewriteBatchedStatements=true";
		String user = "quuppa";
		String password = "quuppa";

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
		ArrayList<Integer> ret = new ArrayList<Integer>();
		ResultSet rs = executeQuery("SELECT DISTINCT traceID FROM `traces`");

		try {
			while (rs.next()) {
				ret.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	@Override
	public Trace getTraceByTraceId(int id) {
		ResultSet rs = executeQuery("SELECT * from traces where traceID=" + id);
		return buildOneTrace(rs, id);
	}

	private Trace buildOneTrace(ResultSet rs, int id) {
		Trace t = new Trace();
		t.setId(id);

		try {
			while (rs.next()) {
				if (rs.getInt(7) != id) {
					// next trace-> abort
					rs.previous();
					break;
				}
				TracePoint tp = new TracePoint(rs.getDouble(1),
						rs.getDouble(2), rs.getLong(6));
				tp.setAccuracy(rs.getDouble(3));
				tp.setId(rs.getString(4));
				tp.setPositionTimeStampText(rs.getString(5));
				t.add(tp);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public void registerCallback(ResultCallback callback) {
		callbacks.add(callback);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * trajectories.database.Database#saveTargetTrajectory(trajectories.container
	 * .Trajectory)
	 */
	@Override
	public void saveTrace(Trace t) {
		try {
			String columns = "POSITIONX, POSITIONY, POSITIONACCURACY, ID, TIMESTAMPTEXT, TIMESTAMP, TRACEID";
			String query = String.format(
					"INSERT INTO %s (%s) VALUES (?,?,?,?,?,?,?)",
					targetTracesTable, columns);

			PreparedStatement stmt = con.prepareStatement(query);

			for (TracePoint tp : t.getPoints()) {
				stmt.setDouble(1, tp.getX());
				stmt.setDouble(2, tp.getY());
				stmt.setDouble(3, tp.getAccuracy());
				stmt.setString(4, "synth");
				stmt.setString(5, tp.getPositionTimeStampText());
				stmt.setDouble(6, tp.getPositionTimeStamp());
				stmt.setInt(7, t.getId());

				stmt.addBatch();
			}

			stmt.executeBatch();
			con.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void getAllTraces() {
		ResultSet rs = executeQuery("SELECT * from traces order by traceID, `timeStamp`");
		try {
			while (rs.next()) {
				int nextId = rs.getInt(7);
				rs.previous();
				Trace t = buildOneTrace(rs, nextId);
				notifyCallbacks(t);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		notifyCallbacksFinished();
	}

	private void notifyCallbacks(Trace t) {
		for (ResultCallback call : callbacks) {
			call.receiveTrace(t);
		}

	}

	private void notifyCallbacksFinished() {
		for (ResultCallback call : callbacks) {
			call.transactionFinished();
		}

	}

	@Override
	public void unregisterCallback(ResultCallback callback) {
		// TODO Auto-generated method stub
		callbacks.remove(callback);
	}

	@Override
	public void storeAccumulationresults(ArrayList<AccumulationResult> res) {
		try {
			String columns = "traceId, time, distance, cashDesk, payTime, startTime, endTime, startTimeText, endTimeText, biggestJump, averageJump";
			String query = String.format(
					"INSERT INTO %s (%s) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
					tracesAccumTable, columns);

			PreparedStatement stmt = con.prepareStatement(query);

			for (AccumulationResult result : res) {
				stmt.setInt(1, result.getTraceID());
				stmt.setDouble(2, result.getShoppingTime());
				stmt.setDouble(3, result.getShoppingDistance());
				int usedCashDesk = 0;
				if (result.getUsedCashDesk() != null) {
					usedCashDesk = result.getUsedCashDesk().getDeskNumber();
				}
				stmt.setInt(4, usedCashDesk);
				stmt.setLong(5, result.getStartPayTime());
				stmt.setLong(6, result.getStartTime());
				stmt.setLong(7, result.getEndTime());
				stmt.setString(8, result.getStartTimeText());
				Date datum = new Date(result.getEndTime());
				stmt.setString(9, datum.toString());
				stmt.setDouble(10, result.getBiggestJump());
				stmt.setDouble(11, result.getAvgJump());
				stmt.addBatch();

			}
			stmt.executeBatch();
			con.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public ArrayList<BareCloud> getCloudsByTrace(int traceID, double maxSpeed) {
		ResultSet rs = executeQuery("SELECT * FROM `clouds` where traceId="
				+ traceID);

		ArrayList<BareCloud> clouds = new ArrayList<BareCloud>();
		try {
			while (rs.next()) {
				BareCloud createCloud = createCloud(rs);
				if (createCloud.getTraceId() == traceID
						&& createCloud.getScore() <= maxSpeed) {
					clouds.add(createCloud);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return clouds;
	}

	private BareCloud createCloud(ResultSet rs) {

		try {
			BareCloud cloud = new BareCloud(rs.getInt(1), rs.getInt(2),
					rs.getInt(5), rs.getDouble(3), rs.getDouble(4),
					new TracePoint(rs.getDouble(6), rs.getDouble(7), 0));
			return cloud;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void getAllClouds() {
		throw new IllegalAccessError("Not implemented yet: use getCloudsForTrace");

	}

	@Override
	public void storeClouds(Collection<Cloud> clouds) {
		try {
			String columns = "traceId, cloudId, score, diameter, numPoints, centerX, centerY";
			String query = String.format(
					"INSERT INTO %s (%s) VALUES (?,?,?,?,?,?,?)", "clouds",
					columns);

			PreparedStatement stmt = con.prepareStatement(query);
			int i = 0;

			for (Cloud cloud : clouds) {
				stmt.setInt(1, cloud.getTraceId());
				stmt.setInt(2, i++);
				stmt.setDouble(3, cloud.getCloudSpeed());
				stmt.setDouble(4, cloud.getDiameter());
				stmt.setInt(5, cloud.getNumberOfPoints());
				stmt.setDouble(6, cloud.getCenter().getX());
				stmt.setDouble(7, cloud.getCenter().getY());
				stmt.addBatch();
			}
			stmt.executeBatch();
			con.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void getAllProducts() {
		ResultSet rs = executeQuery("SELECT * from products");
		try {
			while (rs.next()) {

				Product p = buildProduct(rs);
				notifyCallbacks(p);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		notifyCallbacksFinished();

	}

	private Product buildProduct(ResultSet rs) {

		try {
			int sapmat = rs.getInt(1);
			String sapWar = rs.getString(2);
			String bez = rs.getString(4);
			long ean = rs.getLong(5);
			int lieferantId = rs.getInt(6);
			String lieferant = rs.getString(7);
			int regalPos = rs.getInt(9);
			int regalnr = rs.getInt(10);

			return new Product(sapmat, sapWar, bez, ean, lieferantId,
					lieferant, regalPos, regalnr);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private void notifyCallbacks(Product p) {
		for (ResultCallback call : callbacks) {
			call.receiveProduct(p);
		}

	}

	@Override
	public ArrayList<TraceInfo> getTraceInfos(long startTimestamp,
			long endTimestamp) {
		return getTraceInfosStatement("SELECT * from traceInfo where payTime>"
				+ startTimestamp + " and payTime <" + endTimestamp);
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
	public HashMap<Long, ArrayList<BonInfo>> getRawBons(Long startTime,Long endTime) {
		throw new UnsupportedOperationException(
				"Only available for HANA database");

	}

	@Override
	public void writeBonInfos(ArrayList<BonInfo> infos) {
		try {
			String columns = "traceId, transnumber, timestamp, workstationID, matchingtype, ean, quantity";
			String query = String.format(
					"INSERT INTO %s (%s) VALUES (?,?,?,?,?,?,?)", "BonInfos",
					columns);

			PreparedStatement stmt = con.prepareStatement(query);

			for (BonInfo info : infos) {

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
			stmt.executeBatch();
			con.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public ArrayList<TraceInfo> getTraceInfosByPaytime(long startTime,
			long endTime) {
		return getTraceInfosStatement("SELECT * from traceInfo where payTime>"
				+ startTime + " and payTime <" + endTime + " order by payTime");
	}

	@Override
	public Iterable<BonInfo> getRawBons(Long start, Long end,Iterable<Long> transnumbers) {
		throw new UnsupportedOperationException(
				"Only available for HANA database");
	}

	@Override
	public EnhancedBonList getEnhancedBons() {
		
		EnhancedBonList ret=new EnhancedBonList();
		
		ResultSet rs=executeQuery("select * from BonInfos order by traceID");
		
		ArrayList<BonInfo> infos=new ArrayList<BonInfo>();
		
		ArrayList<Long> eans=new ArrayList<Long>();
		ArrayList<Double> quantities=new ArrayList<Double>();
		
		
		try {
			
			long traceID = 0;
			long transnumber=-1;
			long workstationID=-1;
			long timestamp = 0;
			String matchingtype = null;
			
			while (rs.next()){
				

				
				if (transnumber!=-1 && (rs.getLong(1)!=transnumber||rs.getLong(4)!=workstationID)){
					//got new transID or workstationid-> all records from the current traceid are extracted
					infos.add(new BonInfo(traceID, transnumber, timestamp, workstationID, matchingtype, eans, quantities));
					eans=new ArrayList<Long>();
					quantities=new ArrayList<Double>();
				}
				traceID=rs.getLong(1);
				transnumber=rs.getLong(2);
				timestamp=rs.getLong(3);
				workstationID=rs.getLong(4);
				matchingtype=rs.getString(5);
				long ean=rs.getLong(6);
				double quantity=rs.getDouble(7);
				eans.add(ean);
				quantities.add(quantity);
				
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (BonInfo info:infos){
			ret.addBon(info.getTraceId(), info);
		}
		return ret;
	}

	private BonInfo buildBonInfo(ResultSet rs) {
		
		return null;
	}

	@Override
	public void writeBonMatchings(HashMap<Long, String> matchings) {
		
		try {
			String columns = "traceID, matchingType";
			String query = String.format(
					"INSERT INTO %s (%s) VALUES (?,?)", "BonMatchings",
					columns);

			PreparedStatement stmt = con.prepareStatement(query);

			for (Entry<Long,String> entry:matchings.entrySet()) {
				stmt.setLong(1, entry.getKey());
				stmt.setString(2, entry.getValue());
				stmt.addBatch();
			}

			stmt.executeBatch();
			con.commit();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	
	@Override
	public void writeBonMatchingsDistance(HashMap<Long, Double> matchingsDistance) {
			
		}
	
	@Override
	public BonInfo getBonForTrace(long traceId) {
		ResultSet rs=executeQuery("select * from BonInfos,BonMatchings where BonInfos.traceID=BonMatchings.traceID and BonInfos.matchingType=BonMatchings.matchingType and BonInfos.traceID=" + traceId);
		
		try {
			long traceID = 0;
			long transnumber=-1;
			long workstationID=-1;
			long timestamp = 0;
			String matchingtype = null;
			ArrayList<Long> eans=new ArrayList<Long>();
			ArrayList<Double> quantities=new ArrayList<Double>();
			
			while(rs.next()){
				traceID=rs.getLong(1);
				transnumber=rs.getLong(2);
				timestamp=rs.getLong(3);
				workstationID=rs.getLong(4);
				matchingtype=rs.getString(5);
				long ean=rs.getLong(6);
				double quantity=rs.getDouble(7);
				eans.add(ean);
				quantities.add(quantity);
			}
			
			return new BonInfo(traceID, transnumber, timestamp, workstationID, matchingtype, eans, quantities);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("not matching found for trace " + traceId);
		return null;
	}

	@Override
	public ArrayList<UPurchase> getUPurForTrace(long traceId) {
ResultSet rs=executeQuery("select * from upurchases where traceId=" + traceId);
		ArrayList<UPurchase> ret=new ArrayList<UPurchase>();
		
		try {
			long traceID = 0;
			long prodId=-1;
			int centerX=0, centerY=0;
						
			while(rs.next()){
				traceID=rs.getLong(1);
				prodId=rs.getLong(2);
				centerX=rs.getInt(3);
				centerY=rs.getInt(4);
				ret.add(new UPurchase(traceID, prodId, centerX, centerY));				
			}
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return ret;
	}

}
