package com.example.fairsharebackend.factory;

import com.example.fairsharebackend.strategy.SplitStrategy;
import com.example.fairsharebackend.strategy.impl.EqualSplitStrategy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SplitStrategyFactory {
    
    private final Map<String, SplitStrategy> strategies = new HashMap<>();
    
    public SplitStrategyFactory(EqualSplitStrategy equalSplitStrategy) {
        strategies.put("EQUAL", equalSplitStrategy);
        // To add more strategies here in later sprints
    }
    
    public SplitStrategy getStrategy(String strategyName) {
        SplitStrategy strategy = strategies.get(strategyName.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported split strategy: " + strategyName);
        }
        return strategy;
    }
}