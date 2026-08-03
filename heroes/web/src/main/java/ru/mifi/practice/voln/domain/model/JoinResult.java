package ru.mifi.practice.voln.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.UUID;

/** Результат входа в комнату. */
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record JoinResult(@JsonProperty("room_id") UUID roomId, @JsonProperty("retry_sec") Long retrySec) {
}
