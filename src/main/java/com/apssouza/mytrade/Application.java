package com.apssouza.mytrade;

import com.apssouza.mytrade.trading.forex.session.ExecutionType;
import com.apssouza.mytrade.trading.forex.session.SessionType;
import com.apssouza.mytrade.trading.forex.session.TradingSession;

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

        LocalDate date = LocalDate.of(2018, 9, 1);

        TradingSession tradingSession = new TradingSession(
                BigDecimal.valueOf(100000l),
                LocalDateTime.of(date, LocalTime.MIN),
                LocalDateTime.of(date.plusDays(30), LocalTime.MIN),
                conn,
                SessionType.LIVE,
                "test",
                ExecutionType.SIMULATED
        );
//        tradingSession.start();
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
        st.executeUpdate("" +
                "INSERT INTO price(TimeStamp,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,Symbol)" +
                "     VALUES('2018-01-10 00:00:00.000',0.73562,0.73563,0.73562,0.73562,0,'AUDUSD')"
        );
        st.executeUpdate("" +
                "INSERT INTO price(TimeStamp,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,Symbol)" +
                "     VALUES('2018-01-11 00:00:00.000',0.73562,0.73563,0.73562,0.73562,0,'AUDUSD')"
        );
        st.executeUpdate("" +
                "INSERT INTO price(TimeStamp,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,Symbol)" +
                "     VALUES('2018-01-12 00:00:00.000',0.73562,0.73563,0.73562,0.73562,0,'AUDUSD')"
        );
        st.executeUpdate("" +
                "INSERT INTO price(TimeStamp,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,Symbol)" +
                "     VALUES('2018-01-13 00:00:00.000',0.73562,0.73563,0.73562,0.73562,0,'AUDUSD')"
        );
        st.executeUpdate("" +
                "INSERT INTO price(TimeStamp,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,Symbol)" +
                "     VALUES('2018-01-14 00:00:00.000',0.73562,0.73563,0.73562,0.73562,0,'AUDUSD')"
        );
        st.executeUpdate("" +
                "INSERT INTO price(TimeStamp,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,Symbol)" +
                "     VALUES('2018-01-15 00:00:00.000',0.73562,0.73563,0.73562,0.73562,0,'AUDUSD')"
        );
        st.executeUpdate("" +
                "INSERT INTO price(TimeStamp,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,Symbol)" +
                "     VALUES('2018-01-16 00:00:00.000',0.73562,0.73563,0.73562,0.73562,0,'AUDUSD')"
        );
        st.executeUpdate("" +
                "INSERT INTO price(TimeStamp,OpenPrice,HighPrice,LowPrice,ClosePrice,Volume,Symbol)" +
                "     VALUES('2018-01-17 00:00:00.000',0.73562,0.73563,0.73562,0.73562,0,'AUDUSD')"
        );
    }

}
