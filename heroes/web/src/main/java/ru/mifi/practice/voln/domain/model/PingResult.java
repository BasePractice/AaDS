package ru.mifi.practice.voln.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/** Результат проверки соединения с комнатой. */
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PingResult() {
}
