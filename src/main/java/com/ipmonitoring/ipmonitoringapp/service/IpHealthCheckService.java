package com.ipmonitoring.ipmonitoringapp.service;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ipmonitoring.ipmonitoringapp.model.IpAddress;
import com.ipmonitoring.ipmonitoringapp.repository.IpAddressRepository;

@Service
public class IpHealthCheckService {
    private final IpAddressRepository repository;

    public IpHealthCheckService(IpAddressRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedDelay = 10000) // every 30 seconds
    public void healthCheckAllIps() {
        List<IpAddress> ips = repository.findAll();
        for (IpAddress ipAddress : ips) {
            try {
                InetAddress inet = InetAddress.getByName(ipAddress.getIp());
                boolean reachable = inet.isReachable(2000); // 2s timeout
                String status = reachable ? "Online" : "Down";
                ipAddress.setStatus(status);
            } catch (Exception e) {
                ipAddress.setStatus("Error");
            }
            ipAddress.setLastChecked(LocalDateTime.now());
            repository.save(ipAddress);
        }
    }
}
