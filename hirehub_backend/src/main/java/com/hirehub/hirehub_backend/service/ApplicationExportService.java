package com.hirehub.hirehub_backend.service;

import org.springframework.core.io.Resource;

public interface ApplicationExportService {
    Resource exportApplicationsToCSV(Long jobId) throws Exception;
    Resource exportApplicationsToExcel(Long jobId) throws Exception;
}







