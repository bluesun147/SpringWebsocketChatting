package com.umc.pol.domain.chat.controller;

import com.umc.pol.domain.chat.dto.ChatMessage;
import com.umc.pol.domain.chat.dto.ChatPageDto;
import com.umc.pol.domain.chat.repository.ChatRoomRepository;
import com.umc.pol.domain.chat.security.JwtTokenProvider;
import com.umc.pol.domain.chat.service.ChatService;
import com.umc.pol.global.response.ResponseService;
import com.umc.pol.global.response.SingleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class ChatController {

//    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;

    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     * @MessageMapping 통해서 웹소켓으로 들어오는 메시지 발행 처리
     * 클라에서는 prefix 붙여서 /pub/chat/message로 발행 요청하면 컨트롤러가 해당 메시지 받아 처리
     * 메시지 발행되면 /sub/chat/room/{roomId}로 메시지 send -> 클라는 해당 주소 구독하고 있다가 전달되면 출력
     * /sub/chat/room/{roomId}는 채팅룰 구분값 -> pub/sub에서 topic 역할.
     */
//    @MessageMapping("/chat/message")
//    public void message(ChatMessage message) {
//        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
//            chatRoomRepository.enterChatRoom(message.getRoomId());
//            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
//        }
//        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
//        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
//    }

    /////

//    @MessageMapping("/chat/message")
//    public void message(ChatMessage message, @Header("token") String token) {
//        String nickname = jwtTokenProvider.getUserNameFromJwt(token);
//
//        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
//            chatRoomRepository.enterChatRoom(message.getRoomId());
//            message.setMessage(nickname + "님이 입장하셨습니다.");
//        }
//        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
//        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
//    }

    /////

    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("token") String token) {
        String nickname = jwtTokenProvider.getUserNameFromJwt(token);
        // 로그인 회원 정보로 대화명 설정
        message.setSender(nickname);
        // 채팅방 입장시에는 대화명과 메시지를 자동으로 세팅한다.
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setSender("[알림]");
            message.setMessage(nickname + "님이 입장하셨습니다.");
        }
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}