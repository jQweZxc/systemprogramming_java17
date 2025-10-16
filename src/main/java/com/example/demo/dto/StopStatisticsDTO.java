package com.example.demo.dto;

/**
 * DTO для статистики по остановке
 */
public class StopStatisticsDTO {
    private Long stopId;
    private int totalEntered;
    private int totalExited;
    private int netPassengers;

    public StopStatisticsDTO() {}

    public StopStatisticsDTO(Long stopId, int totalEntered, int totalExited, int netPassengers) {
        this.stopId = stopId;
        this.totalEntered = totalEntered;
        this.totalExited = totalExited;
        this.netPassengers = netPassengers;
    }

    // Геттеры и сеттеры
    public Long getStopId() { return stopId; }
    public void setStopId(Long stopId) { this.stopId = stopId; }

    public int getTotalEntered() { return totalEntered; }
    public void setTotalEntered(int totalEntered) { this.totalEntered = totalEntered; }

    public int getTotalExited() { return totalExited; }
    public void setTotalExited(int totalExited) { this.totalExited = totalExited; }

    public int getNetPassengers() { return netPassengers; }
    public void setNetPassengers(int netPassengers) { this.netPassengers = netPassengers; }
}