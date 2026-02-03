package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.InterviewDto;

public interface CalendarService {
    // Generate Google Calendar link
    String generateGoogleCalendarLink(InterviewDto interviewDto);

    // Generate Outlook Calendar link
    String generateOutlookCalendarLink(InterviewDto interviewDto);

    // Generate iCal format for calendar import
    String generateICalContent(InterviewDto interviewDto);
}

