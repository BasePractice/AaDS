package ru.mifi.practice.voln.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mifi.practice.voln.controller.model.JoinResult;
import ru.mifi.practice.voln.controller.model.PingResult;

import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @GetMapping(path = "/{room-id}/join", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestHeader("X-User-ID") Long userId, @PathVariable("room-id") UUID id) {
        return Flux.empty();
    }

    @PostMapping(path = "/{room-id}/ping")
    public Mono<PingResult> ping(@RequestHeader("X-User-ID") Long userId, @PathVariable("room-id") UUID id) {
        return Mono.empty();
    }

    @PostMapping(path = "/join", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<JoinResult> join(@RequestHeader("X-User-ID") Long userId) {
        return Mono.empty();
    }
}
