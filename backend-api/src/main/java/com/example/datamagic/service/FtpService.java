package com.example.datamagic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FtpService {

    private final SystemConfigService systemConfigService;

    public boolean uploadFile(String localFilePath) {
        String ftpHost = systemConfigService.getConfigValue("ftp_host");
        String ftpPort = systemConfigService.getConfigValue("ftp_port");
        String ftpUsername = systemConfigService.getConfigValue("ftp_username");
        String ftpPassword = systemConfigService.getConfigValue("ftp_password");
        String ftpRemotePath = systemConfigService.getConfigValue("ftp_remote_path");

        if (ftpHost == null || ftpUsername == null || ftpPassword == null) {
            log.warn("FTP configuration not found, skipping upload");
            return false;
        }

        FTPClient ftpClient = new FTPClient();
        File inputFile = new File(localFilePath);

        if (!inputFile.exists()) {
            log.error("File does not exist: {}", localFilePath);
            return false;
        }

        try {
            int port = ftpPort != null ? Integer.parseInt(ftpPort) : 21;
            ftpClient.connect(ftpHost, port);
            ftpClient.login(ftpUsername, ftpPassword);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            if (ftpRemotePath != null && !ftpRemotePath.isEmpty()) {
                String[] paths = ftpRemotePath.split("/");
                for (String path : paths) {
                    if (!path.isEmpty()) {
                        if (!ftpClient.changeWorkingDirectory(path)) {
                            ftpClient.makeDirectory(path);
                            ftpClient.changeWorkingDirectory(path);
                        }
                    }
                }
            }

            String fileName = inputFile.getName();
            try (FileInputStream fis = new FileInputStream(inputFile)) {
                boolean done = ftpClient.storeFile(fileName, fis);
                if (done) {
                    log.info("File uploaded successfully: {}", fileName);
                    return true;
                } else {
                    log.error("Failed to upload file: {}", fileName);
                    return false;
                }
            }

        } catch (IOException e) {
            log.error("Error uploading file to FTP: {}", localFilePath, e);
            return false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                log.error("Error disconnecting from FTP", e);
            }
        }
    }
}
