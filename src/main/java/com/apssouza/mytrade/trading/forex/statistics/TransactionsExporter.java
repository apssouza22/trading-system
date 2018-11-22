package com.apssouza.mytrade.trading.forex.statistics;

import com.apssouza.mytrade.trading.forex.session.CycleHistory;
import com.apssouza.mytrade.trading.forex.session.TransactionDto;
import com.apssouza.mytrade.trading.misc.helper.file.WriteFileHelper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TransactionsExporter {

    public void exportCsv(List<CycleHistory> transactions, String filepath) {
        WriteFileHelper.write(filepath,"");
        for (CycleHistory item : transactions) {
            for (Map.Entry<String, TransactionDto> trans : item.getTransactions().entrySet()) {
                List<String> line = Arrays.asList(
                        trans.getValue().getIdentifier(),
                        trans.getValue().getPosition() != null ? trans.getValue().getPosition().getInitPrice().toString() : "",
                        trans.getValue().getPosition() != null ? trans.getValue().getPosition().getQuantity().toString() : "",
                        trans.getValue().getPosition() != null ? trans.getValue().getPosition().getInitPrice().multiply(BigDecimal.valueOf(trans.getValue().getPosition().getQuantity())).toString() : "",
                        trans.getValue().getOrder() != null ? trans.getValue().getOrder().getAction().toString() : "",
                        trans.getValue().getPosition() != null ? trans.getValue().getPosition().getTimestamp().toString() : "",
                        //trans.getValue().getPosition() != null ? trans.getValue().getPosition().getPlacedStopLoss().getPrice().toString(): "",
                        //trans.getValue().getPosition() != null ? trans.getValue().getPosition().getTakeProfitOrder().getPrice().toString(): "",
                        trans.getValue().getPosition() != null ? trans.getValue().getPosition().getExitReason().toString() : "",
                        trans.getValue().getState() != null ? trans.getValue().getState().toString() : "",
                        ""
                );
                WriteFileHelper.append(filepath, String.join(",", line) + "\n");
            }
        }
    }
}
