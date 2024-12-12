package com.js.chat_app.service.chat;

import com.js.chat_app.domain.chat.ChatRoom;
import com.js.chat_app.domain.chat.ChatRoomUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageCountService {

    private final RedisTemplate<String,String> redisTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Redis 조회에 사용되는 Key 생성 : unread:room:{roomId}:user:{userId}
     * @param roomId
     * @param userId
     * @return redisKey
     */
    private String getUnreadCountKey(Long roomId, Long userId){
        return String.format("unread:room:%d:user%d", roomId, userId);
    }

    /**
     * 읽지 않은 메시지 수 증가
     * @param roomId
     * @param senderId
     */
    public void incrementUnreadCount(Long roomId, Long senderId, List<Long> roomUserIds){

        for(Long userId : roomUserIds){
            if(!userId.equals(senderId)){
                String countKey = getUnreadCountKey(roomId,userId);
                Long count = redisTemplate.opsForValue().increment(countKey);

                notifyUnreadCount(roomId,userId,count);
            }
        }
    }

    /**
     * 읽지 않은 메시지 수 초기화
     * @param roomId
     * @param userId
     */
    public void resetCount(Long roomId, Long userId){
        String countKey = getUnreadCountKey(roomId,userId);
        redisTemplate.delete(countKey);
        notifyUnreadCount(roomId,userId,0L);
    }

    /**
     * 읽지 않은 메시지 수 조회 
     * @param roomId
     * @param userId
     * @return 읽지 않은 메시지 수
     */
    public Long getCount(Long roomId, Long userId){
        String countKey = getUnreadCountKey(roomId,userId);
        String count = redisTemplate.opsForValue().get(countKey);
        return count != null ? Long.parseLong(count) : 0L;
    }


    /**
     * WebSocket을 통해 읽지 않은 메시지 수의 변경 사항을 클라이언트에게 전파
     * @param roomId
     * @param userId
     * @param count
     */
    public void notifyUnreadCount(Long roomId, Long userId, Long count) {
        Map<String, Object> message = new HashMap<>();
        message.put("roomId",roomId);
        message.put("userId",userId);
        message.put("count",count);

        simpMessagingTemplate.convertAndSend(
                "/topic/room." + roomId + ".count",
                message
        );
    }
}


