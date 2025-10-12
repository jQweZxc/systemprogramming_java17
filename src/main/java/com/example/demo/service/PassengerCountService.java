package com.example.demo.service;

import com.example.demo.model.PassengerCount;
import com.example.demo.repository.PassengerCountRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PassengerCountService {
    
    private final PassengerCountRepository passengerCountRepository;

    public PassengerCountService(PassengerCountRepository passengerCountRepository) {
        this.passengerCountRepository = passengerCountRepository;
    }
    
    public List<PassengerCount> getAll() {
        return passengerCountRepository.findAll();
    }
    
    public PassengerCount getById(Long id) {
        return passengerCountRepository.findById(id).orElse(null);
    }
    
    public PassengerCount create(PassengerCount passengerCount) {
        return passengerCountRepository.save(passengerCount);
    }
    
    public PassengerCount updateById(Long id, PassengerCount updatedPassengerCount) {
        return passengerCountRepository.findById(id)
                .map(passengerCount -> {
                    passengerCount.setBus(updatedPassengerCount.getBus());
                    passengerCount.setStop(updatedPassengerCount.getStop());
                    passengerCount.setEntered(updatedPassengerCount.getEntered());
                    passengerCount.setExited(updatedPassengerCount.getExited());
                    passengerCount.setTimestamp(updatedPassengerCount.getTimestamp());
                    return passengerCountRepository.save(passengerCount);
                })
                .orElse(null);
    }
    
    public boolean deleteById(Long id) {
        if (passengerCountRepository.existsById(id)) {
            passengerCountRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<PassengerCount> getByStopId(Long stopId) {
        return passengerCountRepository.findByStopId(stopId);
    }
    
    public List<PassengerCount> getByBusId(Long busId) {
        return passengerCountRepository.findByBusId(busId);
    }
    
    public List<PassengerCount> getByRouteId(Long routeId) {
        return passengerCountRepository.findByRouteId(routeId);
    }
}