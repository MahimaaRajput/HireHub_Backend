package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.InterviewDto;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CalendarServiceImpl implements CalendarService {

    @Override
    public String generateGoogleCalendarLink(InterviewDto interviewDto) {
        try {
            String title = URLEncoder.encode(interviewDto.getJobTitle() + " - Interview", StandardCharsets.UTF_8);
            String details = URLEncoder.encode("Interview for " + interviewDto.getJobTitle() + 
                (interviewDto.getNotes() != null ? "\n\nNotes: " + interviewDto.getNotes() : ""), StandardCharsets.UTF_8);
            String location = interviewDto.getLocation() != null ? 
                URLEncoder.encode(interviewDto.getLocation(), StandardCharsets.UTF_8) : "";
            
            // Convert LocalDateTime to format required by Google Calendar (YYYYMMDDTHHmmssZ)
            ZonedDateTime zonedDateTime = interviewDto.getScheduledAt().atZone(ZoneId.systemDefault());
            String startDate = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
            
            // Add 1 hour for end time
            ZonedDateTime endDateTime = zonedDateTime.plusHours(1);
            String endDate = endDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
            
            return String.format("https://calendar.google.com/calendar/render?action=TEMPLATE&text=%s&dates=%s/%s&details=%s&location=%s",
                    title, startDate, endDate, details, location);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String generateOutlookCalendarLink(InterviewDto interviewDto) {
        try {
            String subject = URLEncoder.encode(interviewDto.getJobTitle() + " - Interview", StandardCharsets.UTF_8);
            String body = URLEncoder.encode("Interview for " + interviewDto.getJobTitle() + 
                (interviewDto.getNotes() != null ? "\n\nNotes: " + interviewDto.getNotes() : ""), StandardCharsets.UTF_8);
            String location = interviewDto.getLocation() != null ? 
                URLEncoder.encode(interviewDto.getLocation(), StandardCharsets.UTF_8) : "";
            
            // Convert LocalDateTime to ISO 8601 format
            ZonedDateTime zonedDateTime = interviewDto.getScheduledAt().atZone(ZoneId.systemDefault());
            String startDate = zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);
            
            // Add 1 hour for end time
            ZonedDateTime endDateTime = zonedDateTime.plusHours(1);
            String endDate = endDateTime.format(DateTimeFormatter.ISO_INSTANT);
            
            return String.format("https://outlook.live.com/calendar/0/deeplink/compose?subject=%s&startdt=%s&enddt=%s&body=%s&location=%s",
                    subject, startDate, endDate, body, location);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String generateICalContent(InterviewDto interviewDto) {
        try {
            ZonedDateTime zonedDateTime = interviewDto.getScheduledAt().atZone(ZoneId.systemDefault());
            ZonedDateTime endDateTime = zonedDateTime.plusHours(1);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
            String startDate = zonedDateTime.format(formatter);
            String endDate = endDateTime.format(formatter);
            
            String ical = "BEGIN:VCALENDAR\n";
            ical += "VERSION:2.0\n";
            ical += "PRODID:-//HireHub//Interview//EN\n";
            ical += "BEGIN:VEVENT\n";
            ical += "DTSTART:" + startDate + "\n";
            ical += "DTEND:" + endDate + "\n";
            ical += "SUMMARY:" + interviewDto.getJobTitle() + " - Interview\n";
            if (interviewDto.getLocation() != null) {
                ical += "LOCATION:" + interviewDto.getLocation() + "\n";
            }
            ical += "DESCRIPTION:Interview for " + interviewDto.getJobTitle();
            if (interviewDto.getNotes() != null) {
                ical += "\\n\\nNotes: " + interviewDto.getNotes();
            }
            ical += "\n";
            ical += "END:VEVENT\n";
            ical += "END:VCALENDAR\n";
            
            return ical;
        } catch (Exception e) {
            return null;
        }
    }
}



