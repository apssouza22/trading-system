package com.apssouza.mytrade.trading.forex.session.event;

import com.apssouza.mytrade.trading.forex.session.TradingSessionEventDriven;
import com.apssouza.mytrade.trading.misc.loop.LoopEvent;

import java.util.concurrent.BlockingQueue;

public class EventProcessor extends Thread {
    private final BlockingQueue<LoopEvent> eventQueue;
    private final TradingSessionEventDriven tradingSession;

    public EventProcessor(BlockingQueue<LoopEvent> eventQueue, TradingSessionEventDriven tradingSession) {
        super();
        this.eventQueue = eventQueue;
        this.tradingSession = tradingSession;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                LoopEvent loopEvent = eventQueue.take();
                tradingSession.processNext(loopEvent);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
