//open indicates that all packages are accessible to code in other modules at runtime only.
//Also, all the types in the specified package (and all of the types’ members) are accessible via reflection.
// It is required to work with Cucumber
open module com.apssouza.mytrade.feed {
    exports com.apssouza.mytrade.feed.api;
    requires java.sql;
    // transitive is to say that modules that depend on feed also depend on common
    requires transitive com.apssouza.mytrade.common;
}