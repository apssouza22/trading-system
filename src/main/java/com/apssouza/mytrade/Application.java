package com.apssouza.mytrade;

import com.apssouza.mytrade.trading.forex.session.ExecutionType;
import com.apssouza.mytrade.trading.forex.session.SessionType;
import com.apssouza.mytrade.trading.forex.session.TradingSession;
import com.apssouza.mytrade.trading.misc.helper.config.Properties;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {

    private static Logger log = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection conn = getConnection();
        loadMockedData(conn);

        LocalDate date = LocalDate.of(2018, 9, 10);
        Properties.tradingStartDay = LocalDateTime.of(date, LocalTime.MIN);
        Properties.tradingEndDay = LocalDateTime.of(date.plusDays(30), LocalTime.MIN);
        Properties.tradingStartTime = LocalTime.MIN;
        Properties.tradingEndTime = LocalTime.MAX;

        TradingSession tradingSession = new TradingSession(
                BigDecimal.valueOf(100000l),
                Properties.tradingStartDay,
                Properties.tradingEndDay,
                conn,
                SessionType.BACK_TEST,
                "signal_test",
                ExecutionType.SIMULATED
        );
        tradingSession.start();
    }

    private static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver"); // (1)
        String url = "jdbc:h2:mem:";
        Connection con = DriverManager.getConnection(url);
        return con;
    }

    private static void loadMockedData(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        st.executeUpdate("create table price(Id int IDENTITY(1,1) NOT NULL," +
                "TimeStamp datetime NOT NULL," +
                "OpenPrice float NOT NULL," +
                "HighPrice float NOT NULL," +
                "LowPrice float NOT NULL," +
                "ClosePrice float NOT NULL," +
                "Volume float NOT NULL," +
                "Symbol nvarchar(50) NOT NULL," +
                ")");

        st.executeUpdate("create table signal(Id int IDENTITY(1,1) NOT NULL," +
                "created_at datetime NOT NULL," +
                "action nvarchar(6) NOT NULL," +
                "symbol nvarchar(50) NOT NULL," +
                "source_name nvarchar(100) NOT NULL," +
                ")");

        for (int i = 1; i < 20; i = i + 2) {
            st.executeUpdate("" +
                    "INSERT INTO price(TimeStamp,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,Symbol)" +
                    "     VALUES('2018-09-"+i+" 01:01:00.000',0.73562,0.73563,0.73562,0.73562,0,'AUDUSD')"
            );
        }
        for (int i = 1; i < 6; i = i + 2) {
            st.executeUpdate("" +
                    "INSERT INTO signal(created_at,action, symbol, source_name)" +
                    "     VALUES('2018-09-1" + i + " 01:01:00.000','BUY', 'AUDUSD', 'signal_test')"
            );
        }

        for (int i = 1; i < 10; i = i + 3) {
            st.executeUpdate("" +
                    "INSERT INTO signal(created_at,action, symbol, source_name)" +
                    "     VALUES('2018-09-1" + i + " 01:01:00.000','SELL', 'AUDUSD', 'signal_test')"
            );
        }
    }

}
