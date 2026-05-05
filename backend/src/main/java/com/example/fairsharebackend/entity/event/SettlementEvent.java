package com.example.fairsharebackend.entity.event;

import com.example.fairsharebackend.entity.Settlement;

public class SettlementEvent {
    private Settlement settlement;

    public SettlementEvent() {
    }

    public SettlementEvent(Settlement settlement) {
        this.settlement = settlement;
    }

    public Settlement getSettlement() {
        return settlement;
    }

    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
    }
}
