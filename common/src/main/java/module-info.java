module com.apssouza.mytrade.common {
    requires java.logging;
    requires java.sql;
    requires cfg4j.core;
    requires org.slf4j;
    exports com.apssouza.mytrade.common.time;
    exports com.apssouza.mytrade.common.file;
}