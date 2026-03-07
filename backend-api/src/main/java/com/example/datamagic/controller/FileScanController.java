package com.example.datamagic.controller;

import com.example.datamagic.service.FileScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class FileScanController {

    private final FileScanService fileScanService;

    @PostMapping("/trigger")
    public ResponseEntity<Map<String, String>> triggerScan() {
        fileScanService.triggerScan();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "File scan triggered"
        ));
    }
}
