# 92-concurrency

Модуль №92. Многопоточность — практика к занятию №17.

Пакеты и примеры по разделам лекции `docs/AADS-3-lecture-plan.md`:

| Пакет | Раздел лекции | Примеры |
|---|---|---|
| `vol92.thread` | Потоки в Java | `HelloThread`, `InterruptibleTask`, `StatefulThread` |
| `vol92.race` | Гонки данных и JMM | `BrokenCounter`, `VolatileCounter`, `HappensBefore`, задание `TaskFixCounter` |
| `vol92.monitor` | Мониторы | `ProducerConsumer`, задание `TaskProducerConsumer` |
| `vol92.sync` | Примитивы синхронизации | `SemaphoreGate`, `LatchedStart`, `TryLockRoom` |
| `vol92.atomic` | Атомарные операции | `AtomicCounter`, `CasLoop`, `AdderStats` |
| `vol92.collection` | Потокобезопасные коллекции | `ConcurrentWordCount`, `QueuePipeline`, `BrokenHashMapDemo` |
| `vol92.pool` | Пулы потоков | `PoolBenchmark`, `PoolShutdown` |
| `vol92.future` | Future и CompletableFuture | `SumFuture`, `ChainFuture`, `ParallelAllOf`, задание `TaskChainFuture` |
| `vol92.philosophers` | Обедающие философы | `Fork`, `Philosopher`, `Table`, задание `TaskNoDeadlock` |
| `vol92.patterns` | Потокобезопасность: паттерны | `ImmutableMessage`, `MessagePassingCounter` |
| `vol92.virtual` | Виртуальные потоки (Loom) | `VirtualThreadsDemo` в `src/main/java21` |

## Задания студенту

Классы с пометкой «Место для решения студента» бросают `UnsupportedOperationException`,
рядом лежит `@Disabled`-тест. Снимите `@Disabled` и добейтесь зелёного теста:

- `race.TaskFixCounter` — починить счётчик с гонкой;
- `monitor.TaskProducerConsumer` — перекачать сообщения через `wait/notify`;
- `philosophers.TaskNoDeadlock` — убрать дедлок у философов;
- `future.TaskChainFuture` — собрать конвейер без вложенных `get`.

## Виртуальные потоки и JDK

Проект собирается на JDK 17, виртуальные потоки стабильны с JDK 21. Пример
`VirtualThreadsDemo` лежит в `src/main/java21` и **не компилируется** сборкой модуля.
Проверьте его вручную на JDK 21+:

```bash
javac --release 21 -d target/java21 \
  src/main/java21/ru/mifi/practice/vol92/virtual/VirtualThreadsDemo.java
java -cp target/java21 ru.mifi.practice.vol92.virtual.VirtualThreadsDemo 1000
```

Ожидаемый вывод — `Завершено задач: 1000 из 1000`. Без аргумента пример запускает 100 000 задач.
