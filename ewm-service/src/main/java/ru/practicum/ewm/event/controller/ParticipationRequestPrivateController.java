package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.event.service.participationRequest.ParticipationRequestService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class ParticipationRequestPrivateController {
    private final ParticipationRequestService participationRequestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipation(@PathVariable Long userId,
                                                    @RequestParam Long eventId) {
        log.info("Запрос на Добавление запроса от текущего пользователя на участие в событии." +
                " Переданные данные: {}, {} ", userId,eventId);
        return participationRequestService.addParticipation(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    ParticipationRequestDto cancelParticipation(@PathVariable Long userId,
                                                @PathVariable Long requestId) {
        log.info("Запрос на отмену своего запроса на участие в событии Переданные данные: {}, {} ", userId, requestId);
        return participationRequestService.cancelParticipation(userId, requestId);
    }

    @GetMapping("/{userId}/requests")
    List<ParticipationRequestDto> findRequests(@PathVariable Long userId) {
        log.info("Запрос на Получение информации о заявках текущего пользователя на участие в чужих событиях." +
                " Переданные данные: {} ",userId);
        return participationRequestService.findRequests(userId);
    }
}