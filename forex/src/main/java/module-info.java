open module com.apssouza.mytrade.forex {
    requires java.logging;
    requires java.desktop;
    requires java.sql;
    requires com.apssouza.mytrade.common;
    requires com.apssouza.mytrade.feed;
    exports com.apssouza.mytrade.trading.forex.portfolio;
    exports com.apssouza.mytrade.trading.misc.helper;
    exports com.apssouza.mytrade.trading.forex.feed.price;
    exports com.apssouza.mytrade.trading.forex.feed.signal;
    exports com.apssouza.mytrade.trading.forex.session;
}