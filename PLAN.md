# План приведения кода к RULE.md

> Черновик для ревью. Без явного утверждения пользователем модификация кода не производится.
> Файл в `.gitignore`-логику не входит — **не коммитить**.

## 1. Что сделано и как

Проверено соответствие всех **281** `.java`-файлов (23 388 строк, кроме `target/`) правилу `RULE.md`
пятью параллельными ревью по группам модулей: `vol1–vol3`, `vol4–vol7`, `vol8–vol13+commons`, `heroes`,
`vol_`. Для каждого нарушения зафиксированы `файл:строка` и нарушенный пункт RULE.

**Базовая картина.** Механический стиль в целом уже держится: `mvn validate` гоняет checkstyle
(Indentation, LineLength, EmptyLineSeparator, ModifierOrder и др.) и PMD `quickstart`, сборка фиксируется
на JDK 17 через enforcer. Mockito в проекте нет (0 файлов) — это соответствует «фейки/стабы вместо моков».
Реальные расхождения с RULE.md там, **где checkstyle бессилен**: принципы Elegant Objects
(иммутабельность, отказ от геттеров/сеттеров/статики/`null`, композиция вместо наследования), «Злые тесты»,
отсутствие пустых строк в теле метода, пунктуация сообщений, семантика «Парных скобок» и «Монотонного
отступа», Puzzle Driven Development.

**Градиент качества по модулям.**

| Уровень | Модули | Комментарий |
|---|---|---|
| Высокое соответствие | `commons`, `vol9`–`vol13`, `vol5`, `vol1` | Единичные замечания, в основном `@Timeout` |
| Среднее | `vol3`, `vol4`, `vol7`, `heroes/game` | Точечные нарушения EO + тесты |
| Низкое (легаси-слой) | `vol2/yasymb`, `vol6`, `vol8`, `vol_`, `heroes/web` | Системные нарушения, требуют рефакторинга |

## 2. Границы работ — что НЕ трогаем

Эти категории исключены из плана осознанно; RULE это прямо допускает («терпи баги в upstream»,
«оставляй загадки на потом», «предлагай issue, а не исправляй молча»).

1. **Учебные пазлы.** JavaDoc-маркеры «В коде есть ошибка / Найдите здесь ошибку / Почему реализация не
   оптимальна» + рядом `@Disabled`-тест — это фичи курса (`QuickSort`, `HashTable.resize`,
   `Search.PolynomialSearchCached`, `Center` и т.п.). Не исправляем, но см. §6 про оформление по PDD.
2. **Ограничения фреймворков** (чистое соответствие RULE недостижимо):
   - Spring: бины не могут быть `final`; `*Controller/*Service`; field-injection через `@Value`;
     `instanceof UserDetails`; `UserMapper.MAPPER` (MapStruct).
   - JPA/Hibernate: `UserEntity/AuthorityEntity/MessageEntity` — не-final, no-args ctor, `@Getter`.
   - Swing: `BattleGui extends JFrame`, `BattlePanel extends JPanel`, мутабельное состояние перерисовки.
   - ByteBuddy/`sun.reflect` в `vol_/object/ObjectPool` — рефлексия неизбежна по сути задачи (прокси-пул).
3. **Демо-точки входа** (`Main`, `GameGui`): статика/утилитарность — низкий приоритет, не в основной работе.

## 3. Решения, которые нужны до старта (отметьте в ревью)

План разветвляется в зависимости от ваших ответов:

- **Р1. Иммутабельность структур данных.** Массивы, списки, кучи, деревья, автоматы по сути мутабельны, а
  RULE требует «только неизменяемые объекты». Варианты: **(a)** признать осознанным отклонением и не трогать
  не-final поля структур данных; **(b)** заморозить всё, что мутируется алгоритмически неоправданно, а сами
  структуры оставить; **(c)** заворачивать мутацию в новые объекты. → *Рекомендация: (b).*
- **Р2. Статические фабрики в интерфейсах** (`Counter.create`, `Node.root`, `PathTree.create`,
  `Machine.of`, `Weight.ofInteger`, `BytesMatrix.defaults`…). EO запрещает статику вообще, но это
  распространённая идиома «умного интерфейса». Убирать (заменить на публичные классы-реализации) или принять?
  → *Рекомендация: принять как отклонение, статику вне интерфейсов — убирать.*
- **Р3. Lombok.** `@Getter/@Setter/@Data/@Builder` порождают аксессоры (нарушение п.4). Убирать полностью
  (кроме вынужденного в JPA/DTO) или оставить? → *Рекомендация: убрать из доменных классов
  (`vol8`, `vol_/games`, `vol_/prime`), оставить в JPA/Jackson-DTO.*
- **Р4. Глубина рефакторинга легаси (`vol8`, `vol2/yasymb`).** Делать сейчас в рамках этой работы или
  оформить пачкой GitHub-issue и приводить постепенно? → *Рекомендация: issues (Фаза 4).*

## 4. Приоритизация

Работа разбита на фазы по возрастанию риска/стоимости. Внутри фаз — от массового и дешёвого к точечному.
После каждой фазы — зелёная сборка `mvn -B clean package` (включая checkstyle/PMD).

---

### Фаза 1 — Тесты («Злые тесты», п.15/16). Массово, механически, низкий риск

Самое массовое нарушение проекта: **`@Timeout` есть лишь в 7 из 55 тест-файлов**.

1. **`@Timeout` на каждый тест-метод** (RULE «Рантайм»). Затронуто ~48 файлов практически во всех модулях
   (`vol2`, `vol3`, `vol4`, `vol6`, `vol7`, `vol8` частично, `vol9`–`vol13`, `heroes/game`, весь `vol_`).
2. **Один assert на тест, последним оператором.** Разбить множественные проверки на отдельные тесты:
   `heroes/game/TurnQueueTest` (~8 assert), `BattleMapTest`, `FlyerTest`, `vol6/RBTTest.testAdd` (7),
   `vol8/StateTest`, `vol_/PatternTest`, `vol_/RaptorCodesTest`, `vol_/prime/*Test`,
   `vol_/ObjectPoolTest`, `vol2/BytecodeTest`. Убрать условные assert (`if (mobIndex!=-1)`) в `PatternTest`.
3. **Негативные сообщения без апострофов.** Позитивные формулировки → негативные: `RBTTest`
   («Root must be black»), `heroes/game/*`, `vol_/RaptorCodesTest`, `MersenneNumbersTest`. Проверить
   `dont/cannot` без апострофов (в большинстве уже соблюдено).
4. **Имена-предложения + `@DisplayName`.** `testAdd/testDelete/testGameInitialization/parse/match` → фразы.
5. **Изоляция: убрать общий `@BeforeEach`/константы/статические хелперы** (RULE: тест автономен). Точки:
   `vol8/HippopotamusTest` (`@BeforeEach`, поле `mapper`, константы `A/B/C`), `vol8/AbstractPatternTest`
   (абстрактный базовый тест), `vol_/CacheableValueRangeTest` (`setUp`, `USER_ID`), `vol6/RBTTest`
   (общие `verifyRBTProps/checkNode`), `vol_/JohnsonTrotterTest` (6 static-хелперов),
   `vol_/machine/post/ProgramTest` (общие константы), `vol3/SortTest.Marked`, `vol2/*Test` хелперы.
6. **Соответствие 1:1 «тест ↔ файл фичи».** Разбить `vol3/SortTest` на 5 файлов (`MergeSort`, `CountSort`,
   `QuickSort`, `HeapSort`, `RadixSort`); переименовать/перепривязать `vol2/SudokuBlocksTest`,
   `vol2/BytecodeTest`; `heroes/game/BattlePatternTest`; `vol_/PatternTest`. Добавить недостающие тесты:
   `vol3/BinaryNaturalSearch`, `vol3/BinaryRealSearch`.
7. **Убрать побочные эффекты и работу с ФС/потоками в тестах:** `vol8/MachineTest`/`TreeTest` (запись
   `.puml`/файлов), `vol_/games/transmit/TransmitTest` (мутация `System.in`/`System.out`).
8. **`heroes/web` — тестов нет вообще.** Крупный объём (auth, JWT, presence, chat). → **Отдельный issue**,
   не в основной пачке (см. Фаза 4).

---

### Фаза 2 — Механический стиль в `main`. Низкий риск

1. **Пустые строки внутри тел методов** (RULE «Пакуй тела без пустых строк»). Точки: `vol1/rpn`,
   `vol2/yasymb/*`, `vol5/AntShortestPath`, `vol6/AVL·BSТ·RBT·huffman`, `vol7/Levenshtein·Match`,
   `vol_/object/ObjectPool`, `vol_/games/logic/GameAuto`, `heroes/game/BattlePanel`,
   `heroes/web/ChatController`.
2. **Инлайн-комментарии и закомментированный (мёртвый) код** (RULE «Пропускай инлайн-комментарии»):
   `vol2/vm·yasymb`, `vol3/RadixSort`, `vol6/AVL` (`//LL //RR`), `vol7/backpack` (`//NOTICE`),
   `vol8/*` (россыпь `//Nothing/…`), весь `vol_/codes·mazes·prime`, `heroes/web/*Configuration`
   (`//.setCachePeriod`). `//FIXME/TODO` без ссылки на issue — переоформить по PDD (§6).
3. **Сообщения об ошибках/логов: одно предложение с контекстом, без точки в конце** (RULE «Стиль»):
   - Точки в конце: `vol_/fsm/BytesMatrix` («Not supported yet.»), `vol_/fsm/BytesMatrixFormatter`.
   - Пустой/безконтекстный `throw`: `vol2/VirtualMachine:203`, `vol2/MathCalculator:102`,
     `vol_/machine/post/Compiler`, `vol_/fsm/BytesMatrix`, `vol5/Graph:146`, `vol6/Tree:68`,
     `heroes/game/BattleMap:62,74`.
   - Проглатывание исключений: `vol8/Statistics` (`printStackTrace`), `heroes/web/Telegram…` (`log.error("",e)`),
     `heroes/web/SecurityConfiguration` (`System.out::println` вместо логгера).
   - Двухчастные/оборванные: `vol5/PathTree:81`, `vol_/games/logic/Person:187`, `vol_/otp/OTP:57`.
4. **Имена переменных — одиночные существительные** (дешёвые случаи): `tmp/temp`, `l/r`, `cs/k/v`,
   `toArge`-опечатка (`vol_/games/logic/Person`), `nd/mtr/drs` (`heroes/game/BattleMap`),
   составные `summaryPheromones/finalNext` (`vol5`). Массив односимвольных в `vol2/yasymb` и `vol6/RBT`
   переносится в Фазу 4 (вместе с декомпозицией методов).
5. **Docblock «о назначении» перед классами**, где его нет (RULE «Код»). Массово в `vol6`, `vol7`, `vol_`.
   Заменить `//`-строки-описания на `/** … */` (`vol7/Levenshtein`, `LongestCommonSubsequence`).

---

### Фаза 3 — Elegant Objects, «лёгкий слой». Средний риск

Локальные изменения без смены архитектуры.

1. **`final` у классов, где ничего не мешает:** `vol4/CachedPolynomial`, `vol_/cache/SimpleCacheableValue`,
   `vol_/games/logic/Item.DamageItem`, `vol_/images/Gif`, `vol_/fsm/BytesMatrixImage` (сейчас `@UtilityClass`
   без `final`), `vol8/State.Parallel`, тесты-классы (`vol6/RBTTest`, `heroes/game/*Test`).
2. **Приватизировать публичные статические литералы** (п.4): `vol6/Heap.FORMAT`, `vol_/codes/RaptorConfiguration`
   (`DEFAULT_C/DELTA/PARITIES`), `heroes/web/SecurityConfiguration` (`BEARER_PREFIX/HEADER_NAME`),
   `vol8/Machine.MACHINE_*`, `vol8/otp/OTPMachine.PERSISTENCE_CODE`, `vol2/YaSymbol.START_DIGIT` (не используется —
   удалить), `heroes/game/Constants` (см. п.5). Литералы в интерфейсах (`vol4/Hash`, `vol2/Value.DIGITS`)
   пересмотреть отдельно; `Value.DIGITS` — ещё и публичный изменяемый массив, минимум сделать неизменяемым.
3. **Заморозить поля, мутируемые без нужды** (не структуры данных — см. Р1): `vol2/Recursion3.step`,
   `vol2/YaSymbol.Metrics.operations`, `vol_/prime/DiffieHellman.sharedSecret`, `vol_/fsm/GoL.matrix`.
4. **Делегирующие вторичные конструкторы** (п.9): `vol_/prime/DiffieHellman` (3 ctor),
   `vol2/YaSymbol.Metrics·Context`, `vol2/VirtualMachine.Binary`, `vol8/otp/OTPMachine`.
5. **Публичные методы — объявить в интерфейсе или скрыть** (п.8): `vol3/BinaryIndexSearch.search(Integer,…)`,
   `vol5/Graph.indexOf`, `vol5·vol6/ParserText.parse(String,…)`, `vol6/Tree.Standard.add(3 арг)`,
   `vol_/codes/RaptorDecoder`, `vol_/codes/RobustSolitonDistribution.sampleDegree`, `vol_/fsm/GoL.tick`,
   `vol_/features/CountRange.addCount`, `vol2/recurs/Factorial·Fibonacci`.
6. **Убрать одиночные статические методы вне интерфейсов** (Р2): `vol1/Sortable.swap`, `vol3/MergeSort.merge`,
   `vol3/NumberGenerator`, `vol2/Generator.exchange`, `vol_/prime/*` (`toHex/fromHex/…`),
   `vol_/fsm/BytesMatrixFormatter`, `vol13/Substring.prefixes`. Переоформить как методы объектов.

---

### Фаза 4 — Крупные рефакторинги. Высокий риск → GitHub-issue (PDD)

RULE прямо требует «предлагай issue, а не исправляй молча» и «держи изменения минимальными». Поэтому каждый
пункт — отдельная ветка по номеру issue, приводится постепенно, с TDD. Предлагаемый список issue:

1. **Устранить рефлексию и `instanceof`/касты в `vol8`** → полиморфизм/visitor: `Machine.of`
   (`Class.forName/newInstance`), `machine/Manager`, `machine/MachineGenerator`, `machine/PlantUmlTextGenerator`,
   `otp/OTPKey`, `process/Duplicate`. Аналогично: `vol_/games/AdventureGame·GameAuto` (россыпь `instanceof`).
2. **Композиция вместо наследования реализации** (п.7): деревья `vol6` (AVL/BST/RBT над `AbstractBinaryTree`,
   `Standard` над `AbstractTree`); `vol8/State`-иерархия и `AbstractVisitor`/`AbstractStringVisitor`;
   `vol7/Levenshtein`; `vol_/games/logic` (`Hummer`←`DamageItem`, `Mob`/`Player`←`AbstractPerson`);
   `vol2/RecursionFallback`.
3. **Декомпозиция «god-методов»** (п.14, «Монотонный отступ»): `vol2/SimpleGen1.process` (~185 строк),
   `vol2/Stage2.Step.step` (~180), `vol2/Stage.Generation.permutation`, `vol_/codes/RaptorDecoder.decode`,
   `vol_/mazes/NodeFinder.findPath`, `vol_/mazes/NodeGenerator.generate`, `vol_/object/ObjectPool.getObject`,
   `vol8/otp/OTP.next` (гигантский switch), `vol6/RBT.fixInsert·fixDelete`, `heroes/game/BattleMap.attack`,
   `heroes/web/Telegram…processing`.
4. **Разукрупнить классы > 4 полей** (п.2): `vol2/Stage2.Step` (13 полей), `vol2/Stage.Generation` (14),
   `heroes/game/BattleMap`·`BattlePanel` (по 10), `vol_/object/ObjectPool.Generic` (9),
   `vol_/games/AdventureGame` (8), `vol5/AntShortestPath.Ant` (8), `vol_/cache/SimpleCacheableValue` (7),
   `vol_/codes/RaptorEncoder` (record 9), `vol6/Node.Default` (6).
5. **Утилитарные классы → объекты** (п.5): `vol_/codes/BytesXor·Precode`, `vol_/prime/MersenneNumbers·
   SieveOfEratosthenes`, `vol6/huffman/Tree`, `vol3/NumberGenerator`, `vol9/Combinatorics` (интерфейс из
   одной статики), `vol8/process/Information·Questions`, `vol1/bigo/Main` (низкий приоритет).
6. **Переименовать классы на «-er/-or» и `Pool`** (п.5): `vol8/Manager·Matcher·*Generator`, `vol2/Parser·
   Lexer·Stepper·Generator`, `vol_/machine/post/Compiler`, `vol_/mazes/NodeFinder·NodeGenerator`,
   `vol_/object/ObjectPool.Wrapper`, `vol_/codes/BytesXor`. (`*Controller/*Service` — исключены, §2.)
7. **Устранить возврат/приём `null` → fail-fast/`Optional`** (п.6): `vol6/Node.search·Tree.find`,
   `vol2/Stage.up`, `vol8/Machine.get`, `vol_/cache/SimpleCacheableValue·CacheableMap*`,
   `vol_/mazes/NodeFinder`, `heroes/game/BattleMap.getStack*`.
8. **Убрать Lombok-аксессоры из доменных классов** (Р3): `vol8/Machine·State·Duplicate`,
   `vol_/games/AdventureGame·Person`, `vol_/prime/DiffieHellman`, `heroes/game/Unit·BattleMap`.
9. **`heroes/web` — покрыть тестами** (Фаза 1.8): auth, JWT, presence, chat, telegram.

---

### Фаза 5 — Инфраструктура проверок. Замыкающая

RULE: «доверяй проверяльщикам, а не подавляй их». После того как код приведён в норму, **закрепить
достигнутое машинно**, чтобы регресс ловился автоматически:

- Добавить в `checkstyle.xml` то, что checkstyle умеет: `FinalClass`, `VisibilityModifier`,
  `HideUtilityClassConstructor`/запрет утилит, `MagicNumber` (по вкусу), правило на отсутствие пустых строк
  в теле метода (`RegexpSinglelineJava`/`SeparatorWrap`), запрет trailing-period в сообщениях
  (`RegexpSinglelineJava`).
- Проверить, не подавлены ли где-то предупреждения (`@SuppressWarnings`, PMD-`suppress` в `vol6/huffman/Tree`,
  `//noinspection` в `heroes/game/BattleGui`) — снять там, где нарушение теперь исправлено.

## 5. Порядок исполнения (по RULE «Рабочий процесс»)

1. `git pull` перед началом; ветка **по номеру GitHub-issue** на каждую единицу работы.
2. TDD: сначала тест (для Фазы 1 — приведение самих тестов), затем изменение; баг воспроизводится тестом.
3. `mvn -B clean package` зелёный после каждой фазы (checkstyle + PMD + JUnit на JDK 17).
4. `git push` — **только с вашего явного разрешения**; PR через `gh pr create`.
5. Коммиты — осмысленным одним предложением, в стиле истории репозитория (русский, повелительное наклонение).

Рекомендуемая последовательность веток: Фаза 1 (тесты) → Фаза 2 (стиль) → Фаза 3 (лёгкий EO) →
issue-пачка Фазы 4 по одному → Фаза 5 (проверяльщики).

## 6. Оформление отложенного по Puzzle Driven Development

Все существующие `//FIXME`/`//TODO` без ссылки на issue привести к формату PDD (что, почему не сейчас,
ссылка на issue): `vol2/Stage2:279` («Грязный хак»), `vol8/Machine:122`, `vol8/State:283`,
`heroes/web/UserRegistrationRequest:7`, `vol_/cache/NotifiableRedis:74`, `vol_/object/ObjectPoolTest`.
Учебные пазлы (§2.1) в JavaDoc желательно сопроводить ссылкой на issue, не меняя сам код-загадку.

## 7. Оценка охвата

| Модуль | Файлов | С нарушениями (без пазлов/демо) | Тяжесть |
|---|---|---|---|
| `vol1` | 21 | ~6 | лёгкая (в осн. `@Timeout`) |
| `vol2` | 25 | ~18 | тяжёлая (`yasymb`) |
| `vol3` | 15 | ~8 | средняя |
| `vol4` | 10 | ~5 | лёгкая |
| `vol5` | 11 | ~5 | лёгкая |
| `vol6` | 30 | ~20 | тяжёлая (деревья, `RBTTest`) |
| `vol7` | 10 | ~7 | средняя |
| `vol8` | 30 | ~23 | тяжёлая (легаси) |
| `vol9`–`vol13` | 10 | ~5 | лёгкая (в осн. `@Timeout`) |
| `commons` | 1 | 1 | лёгкая (`create`) |
| `heroes` | 49 | ~30 | тяжёлая (web без тестов) |
| `vol_` | 69 | ~45 | тяжёлая (`games`, `object`, `codes`) |

Сквозные проблемы всего проекта: **(1)** нет `@Timeout` (48/55 тестов); **(2)** мутабельность и Lombok-
аксессоры; **(3)** утилитарные/статические классы и `-er/-or`-имена; **(4)** `instanceof`/касты/`null`;
**(5)** длинные методы с глубокой вложенностью и инлайн-комментарии.
