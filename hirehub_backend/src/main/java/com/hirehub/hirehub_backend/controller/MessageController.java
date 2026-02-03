package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.dto.ConversationDto;
import com.hirehub.hirehub_backend.dto.MessageDto;
import com.hirehub.hirehub_backend.service.MessageService;
import com.hirehub.hirehub_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    // Create or get conversation between two users
    @PostMapping("/conversation")
    public ResponseEntity<ConversationDto> createOrGetConversation(
            @RequestHeader("Authorization") String jwt,
            @RequestParam Long receiverId,
            @RequestParam(required = false) Long jobId) throws Exception {
        Long senderId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        ConversationDto conversation = messageService.createOrGetConversation(senderId, receiverId, jobId);
        return new ResponseEntity<>(conversation, HttpStatus.OK);
    }

    // Send a message
    @PostMapping("/message")
    public ResponseEntity<MessageDto> sendMessage(
            @RequestHeader("Authorization") String jwt,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam(required = false) Long jobId) throws Exception {
        Long senderId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        MessageDto message = messageService.sendMessage(senderId, receiverId, content, jobId);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    // Get all conversations for the current user
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDto>> getUserConversations(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long userId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        List<ConversationDto> conversations = messageService.getUserConversations(userId);
        return new ResponseEntity<>(conversations, HttpStatus.OK);
    }

    // Get messages in a conversation
    @GetMapping("/conversation/{conversationId}/messages")
    public ResponseEntity<List<MessageDto>> getConversationMessages(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long conversationId) throws Exception {
        Long userId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        List<MessageDto> messages = messageService.getConversationMessages(conversationId, userId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    // Mark messages as read
    @PutMapping("/conversation/{conversationId}/read")
    public ResponseEntity<String> markMessagesAsRead(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long conversationId) throws Exception {
        Long userId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        messageService.markMessagesAsRead(conversationId, userId);
        return new ResponseEntity<>("Messages marked as read", HttpStatus.OK);
    }

    // Get unread message count
    @GetMapping("/messages/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(
            @RequestHeader("Authorization") String jwt) throws Exception {
        Long userId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        Long count = messageService.getUnreadMessageCount(userId);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    // Delete a message
    @DeleteMapping("/message/{messageId}")
    public ResponseEntity<String> deleteMessage(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long messageId) throws Exception {
        Long userId = userService.findUserByJwt(jwt)
                .orElseThrow(() -> new Exception("User not found")).getId();
        
        messageService.deleteMessage(messageId, userId);
        return new ResponseEntity<>("Message deleted", HttpStatus.OK);
    }
}

