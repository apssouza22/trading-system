package com.apssouza.mytrade.feed.domain.signal;

import com.apssouza.mytrade.common.time.DateTimeConverter;
import com.apssouza.mytrade.feed.api.SignalDto;

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

        ResultSet resultSet;
        try (Statement sta = this.connection.createStatement()) {
            resultSet = sta.executeQuery(query);
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
    public List<SignalDto> getSignal(String sourceName, LocalDateTime currentTime) {
        String query = String.format("select * from signal where source_name = '%s' and created_at = '%s'",
                sourceName,
                DateTimeConverter.getDatabaseFormat(currentTime)
        );
        return getList(query);
    }

}
