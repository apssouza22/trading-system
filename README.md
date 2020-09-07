# FXyou   [![Build Status](https://travis-ci.org/apssouza22/trading-system.svg?branch=master)](https://travis-ci.org/apssouza22/trading-system) [![Code Climate](https://codeclimate.com/github/apssouza22/trading-system.png)](https://codeclimate.com/github/apssouza22/trading-system) [![Test Coverage](https://api.codeclimate.com/v1/badges/d999a5f1311bd806b345/test_coverage)](https://codeclimate.com/github/apssouza22/trading-system/test_coverage) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=apssouza22_trading-system&metric=alert_status)](https://sonarcloud.io/dashboard?id=apssouza22_trading-system)
Simple High frequency trading with backtesting simulation and live trading engine written in Java.

It has been developed for both retail traders and institutional hedge funds to aid strategy development and deployment.

## Features
* Based on Java 8: For botting on any operating system - Windows, macOS and Linux
* Event driven architecture
* Persistence: Persistence is achieved through the H2
* Backtesting: Run a simulation of your buy/sell strategy.
* Stop order/ Limit order management
* Broker reconciliation
* Multi position for currency pair
* Daily summary of profit/loss: Provide a daily summary of your profit/loss.
* Performance status report: Provide a performance status of your current trades.

## How to run
```
mvn package && \
java --enable-preview -jar forex/target/forex-1.0-SNAPSHOT-fat.jar 
``` 
## That's all. Leave a star if it helped you!
