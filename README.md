# FXyou   [![Build Status](https://travis-ci.org/apssouza22/trading-system.svg?branch=master)](https://travis-ci.org/apssouza22/trading-system) [![Code Climate](https://codeclimate.com/github/apssouza22/trading-system.png)](https://codeclimate.com/github/apssouza22/trading-system) [![Test Coverage](https://api.codeclimate.com/v1/badges/d999a5f1311bd806b345/test_coverage)](https://codeclimate.com/github/apssouza22/trading-system/test_coverage) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=apssouza22_trading-system&metric=alert_status)](https://sonarcloud.io/dashboard?id=apssouza22_trading-system)
Simple High frequency trading with backtesting simulation and live trading engine written in Java.

It has been developed for both retail traders and institutional hedge funds to aid strategy development and deployment.

## Features
* Persistence: Persistence is achieved through the H2
* Backtesting: Run a simulation of your buy/sell strategy.
* Stop order/ Limit order management
* Broker reconciliation
* Multi position for currency pair
* Daily summary of profit/loss: Provide a daily summary of your profit/loss.
* Performance status report: Provide a performance status of your current trades.

# Tech details
* Java 14
* Event driven architecture
* Maven multi-modules
* Java modularization
* Packaging by domain
* Domain driven design (DDD)
* BDD with cucumber
* Multiple design patterns (Strategy, factory, builder, Observer...)
* Cross-cutting concern

![Alt text](assets/trading-system.png?raw=true "Trading system")

## How to run
```
mvn package && \
java --enable-preview -jar runner/target/runner-1.0-SNAPSHOT.jar
``` 

### Using Docker

```
docker build . -t trading-engine
docker run --rm -p 8080:8080 trading-engine
```

