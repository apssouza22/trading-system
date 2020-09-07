open module com.apssouza.mytrade.forex {
    requires com.apssouza.mytrade.feed;
    requires java.logging;
    requires java.desktop;
    requires java.sql;
    requires com.apssouza.mytrade.common;
    exports com.apssouza.mytrade.trading.forex.portfolio;
}