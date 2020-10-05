open module com.apssouza.mytrade.forex {
    requires java.logging;
    requires java.sql;
    // transitive is to say that modules that depend on forex also depend on feed
    requires transitive com.apssouza.mytrade.feed;
    exports com.apssouza.mytrade.trading.forex.portfolio;
    exports com.apssouza.mytrade.trading.misc.helper;
    exports com.apssouza.mytrade.trading.forex.feed.price;
    exports com.apssouza.mytrade.trading.forex.feed.signal;
    exports com.apssouza.mytrade.trading.forex.session;
}