package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ConversationDto;
import com.hirehub.hirehub_backend.dto.MessageDto;
import com.hirehub.hirehub_backend.entity.Conversation;
import com.hirehub.hirehub_backend.entity.Message;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.ConversationRepository;
import com.hirehub.hirehub_backend.repository.JobRepository;
import com.hirehub.hirehub_backend.repository.MessageRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired(required = false)
    private EmailService emailService;

    @Autowired(required = false)
    private UserNotificationPreferencesService preferencesService;

    @Override
    public ConversationDto createOrGetConversation(Long userId1, Long userId2, Long jobId) throws Exception {
        // Validate users exist
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new Exception("User with id " + userId1 + " not found"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new Exception("User with id " + userId2 + " not found"));

        // Check if conversation already exists
        Optional<Conversation> existingConversation;
        if (jobId != null) {
            existingConversation = conversationRepository.findConversationByJobAndUsers(jobId, userId1, userId2);
        } else {
            existingConversation = conversationRepository.findConversationBetweenUsers(userId1, userId2);
        }

        Conversation conversation;
        if (existingConversation.isPresent()) {
            conversation = existingConversation.get();
        } else {
            // Create new conversation
            conversation = new Conversation();
            conversation.setParticipant1(user1);
            conversation.setParticipant2(user2);
            if (jobId != null) {
                conversation.setJob(jobRepository.findById(jobId)
                        .orElseThrow(() -> new Exception("Job with id " + jobId + " not found")));
            }
            conversation = conversationRepository.save(conversation);
        }

        return convertToConversationDto(conversation, userId1);
    }

    @Override
    public MessageDto sendMessage(Long senderId, Long receiverId, String content, Long jobId) throws Exception {
        // Validate users exist
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new Exception("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new Exception("Receiver not found"));

        // Get or create conversation
        ConversationDto conversationDto = createOrGetConversation(senderId, receiverId, jobId);
        Conversation conversation = conversationRepository.findById(conversationDto.getId())
                .orElseThrow(() -> new Exception("Conversation not found"));

        // Create message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setIsRead(false);
        message = messageRepository.save(message);

        // Update conversation's last message timestamp
        conversation.setLastMessageAt(message.getCreatedAt());
        conversationRepository.save(conversation);

        // Send notification to receiver (in-app and email)
        try {
            String notificationTitle = "New message from " + sender.getFullName();
            String messagePreview = content.length() > 100 ? content.substring(0, 100) + "..." : content;
            com.hirehub.hirehub_backend.dto.NotificationDto notificationDto = new com.hirehub.hirehub_backend.dto.NotificationDto();
            notificationDto.setUserId(receiverId);
            notificationDto.setTitle(notificationTitle);
            notificationDto.setMessage(messagePreview);
            notificationDto.setType("MESSAGE");
            notificationDto.setRelatedEntityType("CONVERSATION");
            notificationDto.setRelatedEntityId(conversation.getId());
            notificationService.createNotification(receiverId, notificationDto);
            
            // Send email notification if user has email notifications enabled for messages
            if (emailService != null && preferencesService != null && 
                preferencesService.isEmailNotificationEnabled(receiverId, "MESSAGE")) {
                try {
                    String conversationLink = "https://hirehub.com/messages/" + conversation.getId();
                    emailService.sendNewMessageEmail(
                        receiver.getEmail(),
                        receiver.getFullName(),
                        sender.getFullName(),
                        messagePreview,
                        conversationLink
                    );
                } catch (Exception emailEx) {
                    // Log error but don't fail the message sending
                    System.err.println("Failed to send email notification: " + emailEx.getMessage());
                }
            }
        } catch (Exception e) {
            // Log error but don't fail the message sending
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        return convertToMessageDto(message);
    }

    @Override
    public List<ConversationDto> getUserConversations(Long userId) throws Exception {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        List<Conversation> conversations = conversationRepository.findConversationsByUserId(userId);
        return conversations.stream()
                .map(conv -> convertToConversationDto(conv, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> getConversationMessages(Long conversationId, Long userId) throws Exception {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new Exception("Conversation not found"));

        // Verify user is a participant
        if (!conversation.getParticipant1().getId().equals(userId) && 
            !conversation.getParticipant2().getId().equals(userId)) {
            throw new Exception("User is not a participant in this conversation");
        }

        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        return messages.stream()
                .map(this::convertToMessageDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markMessagesAsRead(Long conversationId, Long userId) throws Exception {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new Exception("Conversation not found"));

        // Verify user is a participant
        if (!conversation.getParticipant1().getId().equals(userId) && 
            !conversation.getParticipant2().getId().equals(userId)) {
            throw new Exception("User is not a participant in this conversation");
        }

        List<Message> unreadMessages = messageRepository.findUnreadMessages(conversationId, userId);
        LocalDateTime now = LocalDateTime.now();
        for (Message message : unreadMessages) {
            message.setIsRead(true);
            message.setReadAt(now);
        }
        messageRepository.saveAll(unreadMessages);
    }

    @Override
    public Long getUnreadMessageCount(Long userId) throws Exception {
        return messageRepository.countAllUnreadMessagesForUser(userId);
    }

    @Override
    public void deleteMessage(Long messageId, Long userId) throws Exception {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new Exception("Message not found"));

        // Only sender can delete their message
        if (!message.getSender().getId().equals(userId)) {
            throw new Exception("You can only delete your own messages");
        }

        messageRepository.delete(message);
    }

    private ConversationDto convertToConversationDto(Conversation conversation, Long currentUserId) {
        ConversationDto dto = new ConversationDto();
        dto.setId(conversation.getId());
        
        User participant1 = conversation.getParticipant1();
        User participant2 = conversation.getParticipant2();
        
        dto.setParticipant1Id(participant1.getId());
        dto.setParticipant1Name(participant1.getFullName());
        dto.setParticipant1Email(participant1.getEmail());
        
        dto.setParticipant2Id(participant2.getId());
        dto.setParticipant2Name(participant2.getFullName());
        dto.setParticipant2Email(participant2.getEmail());
        
        if (conversation.getJob() != null) {
            dto.setJobId(conversation.getJob().getId());
            dto.setJobTitle(conversation.getJob().getJobTitle());
        }
        
        dto.setLastMessageAt(conversation.getLastMessageAt());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        
        // Get unread count for current user
        Long unreadCount = messageRepository.countUnreadMessages(conversation.getId(), currentUserId);
        dto.setUnreadCount(unreadCount);
        
        return dto;
    }

    private MessageDto convertToMessageDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setSenderEmail(message.getSender().getEmail());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setReceiverName(message.getReceiver().getFullName());
        dto.setReceiverEmail(message.getReceiver().getEmail());
        dto.setContent(message.getContent());
        dto.setIsRead(message.getIsRead());
        dto.setReadAt(message.getReadAt());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}

