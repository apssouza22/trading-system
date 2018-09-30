package com.apssouza.mytrade.feed.signal;

import com.apssouza.mytrade.trading.misc.helper.time.DateTimeConverter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlSignalDao implements SignalDao {

    private Connection connection;

    public SqlSignalDao(Connection connection) {
        this.connection = connection;
    }

    private List<SignalDto> getList(String query) {
        ArrayList<SignalDto> signals = new ArrayList<>();
        try {
            Statement sta = this.connection.createStatement();
            ResultSet resultSet = sta.executeQuery(query);
            while (resultSet.next()) {
                signals.add(bindResultToDto(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return signals;
    }

    private SignalDto bindResultToDto(ResultSet resultSet) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(2);
        return new SignalDto(
                timestamp.toLocalDateTime(),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getString(5)
        );
    }

    @Override
    public void loadData(LocalDateTime start, LocalDateTime end) {

    }

    @Override
    public List<SignalDto> getBySecondAndSource(String sourceName, LocalDateTime currentTime) {
        String query = String.format("select * from signal where source_name = '%s' and created_at = '%s'",
                sourceName,
                DateTimeConverter.getDatabaseFormat(currentTime)
        );
        return getList(query);
    }

}
