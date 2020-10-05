module com.apssouza.mytrade.feed {
    exports com.apssouza.mytrade.feed.price;
    exports com.apssouza.mytrade.feed.signal;
    requires java.sql;
    // transitive is to say that modules that depend on feed also depend on common
    requires transitive com.apssouza.mytrade.common;
}