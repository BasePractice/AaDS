[![Java CI with Maven](https://github.com/BasePractice/AaDS/actions/workflows/maven.yml/badge.svg)](https://github.com/BasePractice/AaDS/actions/workflows/maven.yml)

# Алгоритмы и структуры данных

Практика к курсу «Алгоритмы и структуры данных».
[Презентация](https://docs.google.com/presentation/d/1wnUpkpPjfUgslAAFgakvIRrrGXmNPuwz/edit?usp=sharing).

## Сборка

Проект собирается **на JDK 17**. Начиная с JDK 23 javac отключает аннотационную обработку по
умолчанию — Lombok молча перестаёт генерировать код, и `15-automata` не компилируется. Версия проверяется
`maven-enforcer-plugin`, поэтому сборка на другом JDK падает сразу и с понятным сообщением.

```bash
mvn -B clean package
```

Фаза `validate` прогоняет checkstyle и PMD, фаза `test` — JUnit 5.

## Занятия и модули

Курс читается за 16 занятий. Расписание с лекциями, слайдами, заданиями практики и примерами — в
[docs/LESSONS.md](docs/LESSONS.md). Номер в имени модуля — номер первого занятия, на котором он нужен.

| № | Занятие | Модули |
|---|---|---|
| 1 | Асимптотика и сложность | `01-basics/bigo`, `00-commons`, `90-sandbox/prime` (решето) |
| 2 | Линейные структуры: массивы, списки, стек, очередь, дек | `01-basics`, `90-sandbox/buffer` (кольцевой буфер) |
| 3 | Рекурсия | `03-recursion`, `90-sandbox/trampoline` (рекурсия без роста стека) |
| 4 | Комбинаторика | [04-combinatorics](04-combinatorics) с ребусом `yasymb`, `90-sandbox/jt` (Джонсон — Троттер) |
| 5 | Сортировка и поиск | `05-sorting-search` |
| 6 | Хеширование | `06-hashing` |
| 7 | Графы: представления, обходы, Дейкстра | `07-graphs` |
| 8 | Куча, пирамидальная сортировка, Дейкстра на куче | `08-heap-trees/tree/Heap`, `05-sorting-search`, `07-graphs` |
| 9 | СНМ и остовные деревья | [09-disjoint-set](09-disjoint-set), [09-spanning-tree](09-spanning-tree) |
| 10 | Топологическая сортировка; Беллман — Форд, Флойд — Уоршелл | [10-topological-sort](10-topological-sort), `07-graphs` |
| 11 | Деревья: обходы, LCA, диаметр, центр | `08-heap-trees` |
| 12 | Деревья поиска и сжатие: BST, AVL, Хаффман | `08-heap-trees` |
| 13 | Динамическое программирование | `13-dynamic-programming` |
| 14 | Строки: наивный поиск, КМП, Рабин — Карп | [14-strings](14-strings) |
| 15 | Конечные автоматы | `15-automata` |
| 16 | Лабиринт и клеточный автомат | [16-mazes-life](16-mazes-life), пакеты `mazes` и `life` |

Модуль `90-sandbox` — песочница вне программы курса: кэши, Raptor-коды, машина Поста, простые числа,
пул объектов, slab-распределитель памяти (`voln.slab`), текстовый квест. В нём же пакет `ru.mifi.practice.boring` — сорок алгоритмов,
которых в программе нет, разложенных по темам: неочевидные структуры, строки, графы, геометрия,
теория чисел, динамическое программирование, обработка сигналов и прикладная тройка. Список и
карта пакетов — в [PLAN.md](PLAN.md).

Модуль [91-heroes](91-heroes) тоже вне программы и состоит из двух приложений:
[91-heroes/game](91-heroes/game) — пошаговый бой на сетке со Swing-интерфейсом, в двух режимах — против
компьютера и по сети; [91-heroes/web](91-heroes/web) — чат-комнаты и комнаты боя на Spring Boot.
Общего кода у них нет: игра говорит с сервером по HTTP.

Чего в примерах не хватает и что стоит добавить — в [docs/EXAMPLES.md](docs/EXAMPLES.md).

Модуль `00-commons` — общий счётчик операций, которым примеры занятий меряют фактическое число шагов.

### Модуль и пакет

Номер в имени модуля — номер занятия. Пакеты Java остались прежними: имя пакета не может начинаться
с цифры, а переименование затронуло бы сотни файлов.

| Модуль | Пакет |
|---|---|
| `00-commons` | `ru.mifi.practice.commons` |
| `01-basics` | `ru.mifi.practice.vol1` |
| `03-recursion` | `ru.mifi.practice.vol2` |
| `04-combinatorics` | `ru.mifi.practice.vol9` |
| `05-sorting-search` | `ru.mifi.practice.vol3` |
| `06-hashing` | `ru.mifi.practice.vol4` |
| `07-graphs` | `ru.mifi.practice.vol5` |
| `08-heap-trees` | `ru.mifi.practice.vol6` |
| `09-disjoint-set` | `ru.mifi.practice.vol10` |
| `09-spanning-tree` | `ru.mifi.practice.vol12` |
| `10-topological-sort` | `ru.mifi.practice.vol11` |
| `13-dynamic-programming` | `ru.mifi.practice.vol7` |
| `14-strings` | `ru.mifi.practice.vol13` |
| `15-automata` | `ru.mifi.practice.vol8` |
| `16-mazes-life` | `ru.mifi.practice.vol14` |
| `90-sandbox` | `ru.mifi.practice.voln`, `ru.mifi.practice.boring` |
| `91-heroes/game` | `ru.mifi.practice.voln.heroes` |
| `91-heroes/web` | `ru.mifi.practice.voln` |

## Задания

Часть ошибок в коде оставлена намеренно и помечена в JavaDoc («В коде есть ошибка», «Найдите здесь
ошибку», «Почему реализация не оптимальна?»). Рядом с каждой лежит `@Disabled`-тест, описывающий
ожидаемое поведение: снимите `@Disabled`, добейтесь зелёного теста.

Задачи практики со слайдов 259, 437, 476, 515, 540 и 584 лежат заготовками: класс с пометкой
«Место для решения студента» бросает `UnsupportedOperationException`, а рядом — `@Disabled`-тесты.
Список по занятиям — в [docs/LESSONS.md](docs/LESSONS.md).
