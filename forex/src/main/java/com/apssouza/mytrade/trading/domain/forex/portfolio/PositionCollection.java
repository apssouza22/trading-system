package com.apssouza.mytrade.trading.domain.forex.portfolio;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

class PositionCollection {

    private Map<String, PositionDto> positions = new ConcurrentHashMap<>();

    public void add(PositionDto position) {
        positions.put(position.identifier(), position);
    }

    public void update(PositionDto position) {
        positions.put(position.identifier(), position);
    }

    public void updateItems(Function<PositionDto, PositionDto> mapper) {
        this.positions
                .entrySet()
                .stream()
                .forEach(item -> item.setValue(mapper.apply(item.getValue())));
    }

    public void remove(String identifier) {
        positions.remove(identifier);
    }

    public List<PositionDto> getPositions() {
        return positions
                .entrySet()
                .stream()
                .map(Map.Entry::getValue).
                collect(Collectors.toList());
    }

    public PositionDto get(String identifier) {
        return positions.get(identifier);
    }

    public Map<String, PositionDto> getOpenPositions() {
        return positions.entrySet()
                .stream()
                .filter(entry -> entry.getValue().isPositionAlive())
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()));
    }

    public boolean contains(String identifier) {
        return positions.containsKey(identifier);
    }

    public boolean isEmpty() {
        return positions.isEmpty();
    }

    public int size() {
        return positions.size();
    }
}
