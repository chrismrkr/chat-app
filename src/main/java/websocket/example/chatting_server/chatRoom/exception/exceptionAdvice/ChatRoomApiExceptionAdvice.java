package websocket.example.chatting_server.chatRoom.exception.exceptionAdvice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import websocket.example.chatting_server.chatRoom.controller.ChatRoomController;
import websocket.example.chatting_server.chatRoom.exception.response.ChatRoomApiFieldErrorException;

@RestControllerAdvice(assignableTypes = {ChatRoomController.class})
public class ChatRoomApiExceptionAdvice {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ChatRoomApiFieldErrorException responseFieldError(MethodArgumentNotValidException e) {
        String message = e.getMessage();
        ChatRoomApiFieldErrorException chatRoomApiFieldErrorException = new ChatRoomApiFieldErrorException(message);
        e.getFieldErrors().forEach(fieldError -> {
            chatRoomApiFieldErrorException.getMessages()
                    .put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return chatRoomApiFieldErrorException;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public void ChatRoomApiGlobalErrorException(IllegalArgumentException e) {
        String message = e.getMessage();
    }
}
