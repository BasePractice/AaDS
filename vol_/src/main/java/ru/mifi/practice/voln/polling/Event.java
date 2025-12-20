package ru.mifi.practice.voln.polling;

import lombok.Builder;

@Builder(toBuilder = true)
public record Event(long id, Data data) {
    public record Data() {

    }
}
