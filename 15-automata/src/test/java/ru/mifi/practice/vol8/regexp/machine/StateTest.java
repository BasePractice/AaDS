package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

@DisplayName("Состояние автомата")
class StateTest {
    @DisplayName("Символьное состояние печатает свой символ")
    @Test
    @Timeout(5)
    void printsItsSymbol() {
        assertThat("symbol state dont print its own character",
            new Manager.Default().symbol('p').toString(), is("p"));
    }

    @DisplayName("Пустая последовательность печатается пустой строкой")
    @Test
    @Timeout(5)
    void printsEmptySequenceAsBlank() {
        assertThat("empty sequence dont print as a blank string",
            new Manager.Default().sequence().toString(), is(""));
    }

    @DisplayName("Пустая параллель печатается пустыми скобками")
    @Test
    @Timeout(5)
    void printsEmptyParallelAsBrackets() {
        assertThat("empty parallel dont print as empty brackets",
            new Manager.Default().parallel().toString(), is("[]"));
    }

    @DisplayName("Последовательность печатается цепочкой стрелок")
    @Test
    @Timeout(5)
    void printsSequenceAsArrowChain() {
        Manager manager = new Manager.Default();
        State.Sequence sequence = manager.sequence();
        sequence.add(manager.symbol('a'));
        sequence.add(manager.symbol('b'));
        assertThat("sequence dont print its own members as an arrow chain",
            sequence.toString(), is("a --> b"));
    }

    @DisplayName("Параллель печатается списком веток")
    @Test
    @Timeout(5)
    void printsParallelAsBranchList() {
        Manager manager = new Manager.Default();
        State.Parallel parallel = manager.parallel();
        parallel.add(manager.symbol('x'));
        parallel.add(manager.symbol('y'));
        assertThat("parallel dont print its own branches as a list",
            parallel.toString(), is("[x, y]"));
    }

    @DisplayName("Состояние любого символа печатается точкой")
    @Test
    @Timeout(5)
    void printsAnyAsDot() {
        assertThat("any state dont print as a dot",
            new Manager.Default().any().toString(), is("."));
    }

    @DisplayName("Отрицание печатается перечёркнутым множеством")
    @Test
    @Timeout(5)
    void printsExcludingAsNegatedSet() {
        Manager manager = new Manager.Default();
        assertThat("excluding state dont print as a negated set",
            manager.excluding(manager.symbol('q')).toString(), is("[^q]"));
    }

    @DisplayName("Необязательное состояние печатается со знаком вопроса")
    @Test
    @Timeout(5)
    void printsNoneOrOneWithQuestionMark() {
        Manager manager = new Manager.Default();
        assertThat("none or one state dont print with a question mark",
            manager.noneOrOne(manager.symbol('c')).toString(), is("(c)?"));
    }

    @DisplayName("Повторение от нуля печатается со звёздочкой")
    @Test
    @Timeout(5)
    void printsNoneOrMoreWithAsterisk() {
        Manager manager = new Manager.Default();
        assertThat("none or more state dont print with an asterisk",
            manager.noneOrMore(manager.symbol('c')).toString(), is("(c)*"));
    }

    @DisplayName("Повторение от единицы печатается с плюсом")
    @Test
    @Timeout(5)
    void printsOneOrMoreWithPlus() {
        Manager manager = new Manager.Default();
        assertThat("one or more state dont print with a plus",
            manager.oneOrMore(manager.symbol('c')).toString(), is("(c)+"));
    }

    @DisplayName("Символ печатает своё продолжение")
    @Test
    @Timeout(5)
    void printsSymbolWithItsContinuation() {
        Manager manager = new Manager.Default();
        State.Symbol symbol = manager.symbol('c');
        symbol.setNext(manager.symbol('d'));
        assertThat("symbol dont print its own continuation",
            symbol.toString(), is("c --> d"));
    }

    @DisplayName("Группа печатается пустой строкой")
    @Test
    @Timeout(5)
    void printsGroupAsBlank() {
        assertThat("group dont print as a blank string",
            new Manager.Default().group().toString(), is(""));
    }

    @DisplayName("Параллель раздаёт продолжение своим веткам")
    @Test
    @Timeout(5)
    void spreadsItsContinuationOverBranches() {
        Manager manager = new Manager.Default();
        State.Parallel parallel = manager.parallel();
        parallel.add(manager.symbol('x'));
        parallel.add(manager.symbol('y'));
        parallel.setNext(manager.symbol('z'));
        assertThat("parallel dont spread its own continuation over the branches",
            parallel.toString(), is("[x --> z, y --> z]"));
    }

    @DisplayName("Символ принимает свой символ")
    @Test
    @Timeout(5)
    void letsSymbolAcceptItsOwnCharacter() {
        assertThat("symbol dont accept its own character",
            new Manager.Default().symbol('c').accept(Input.of("c")), is(true));
    }

    @DisplayName("Символ отвергает чужой символ")
    @Test
    @Timeout(5)
    void makesSymbolRejectAnotherCharacter() {
        assertThat("symbol accepts a foreign character",
            new Manager.Default().symbol('c').accept(Input.of("d")), is(false));
    }

    @DisplayName("Состояние любого символа отвергает пустой вход")
    @Test
    @Timeout(5)
    void makesAnyRejectEmptyInput() {
        assertThat("any state accepts an empty input",
            new Manager.Default().any().accept(Input.of("")), is(false));
    }

    @DisplayName("Отрицание отвергает исключённый символ")
    @Test
    @Timeout(5)
    void makesExcludingRejectTheExcludedSymbol() {
        Manager manager = new Manager.Default();
        assertThat("excluding state accepts the symbol it excludes",
            manager.excluding(manager.symbol('o')).accept(Input.of("o")), is(false));
    }

    @DisplayName("Отрицание принимает остальные символы")
    @Test
    @Timeout(5)
    void letsExcludingAcceptAnotherSymbol() {
        Manager manager = new Manager.Default();
        assertThat("excluding state rejects a symbol it dont exclude",
            manager.excluding(manager.symbol('o')).accept(Input.of("z")), is(true));
    }

    @DisplayName("Группа не принимает никакой вход")
    @Test
    @Timeout(5)
    void makesGroupRejectEveryInput() {
        assertThat("group accepts an input",
            new Manager.Default().group().accept(Input.of("a")), is(false));
    }

    @DisplayName("Последовательность передаёт приём своему началу")
    @Test
    @Timeout(5)
    void delegatesAcceptToTheStartOfSequence() {
        Manager manager = new Manager.Default();
        State.Sequence sequence = manager.sequence();
        sequence.add(manager.symbol('a'));
        sequence.add(manager.symbol('b'));
        assertThat("sequence dont delegate accept to its own start",
            sequence.accept(Input.of("b")), is(false));
    }

    @DisplayName("Параллель принимает символ любой из веток")
    @Test
    @Timeout(5)
    void letsParallelAcceptAnyOfItsBranches() {
        Manager manager = new Manager.Default();
        State.Parallel parallel = manager.parallel();
        parallel.add(manager.symbol('x'));
        parallel.add(manager.symbol('y'));
        assertThat("parallel dont accept the symbol of its own second branch",
            parallel.accept(Input.of("y")), is(true));
    }

    @DisplayName("Повторение от единицы отвергает чужой символ")
    @Test
    @Timeout(5)
    void makesOneOrMoreRejectWhatItsBodyRejects() {
        Manager manager = new Manager.Default();
        assertThat("one or more state accepts what its own body rejects",
            manager.oneOrMore(manager.symbol('a')).accept(Input.of("b")), is(false));
    }

    @DisplayName("Повторение от нуля принимает чужой символ")
    @Test
    @Timeout(5)
    void letsNoneOrMoreAcceptWhatItsBodyRejects() {
        Manager manager = new Manager.Default();
        assertThat("none or more state rejects what its own body rejects",
            manager.noneOrMore(manager.symbol('a')).accept(Input.of("b")), is(true));
    }

    @DisplayName("Символ совпадает вместе со своим продолжением")
    @Test
    @Timeout(5)
    void matchesSymbolTogetherWithItsContinuation() {
        Manager manager = new Manager.Default();
        State.Symbol symbol = manager.symbol('c');
        symbol.setNext(manager.symbol('d'));
        assertThat("symbol dont match together with its own continuation",
            symbol.match(Input.of("cd")).ok(), is(true));
    }

    @DisplayName("Символ не совпадает без своего продолжения")
    @Test
    @Timeout(5)
    void cannotMatchSymbolWithoutItsContinuation() {
        Manager manager = new Manager.Default();
        State.Symbol symbol = manager.symbol('c');
        symbol.setNext(manager.symbol('d'));
        assertThat("symbol matches without its own continuation",
            symbol.match(Input.of("c")).ok(), is(false));
    }

    @DisplayName("Повторение от единицы съедает все повторы")
    @Test
    @Timeout(5)
    void consumesEveryRepetitionWithOneOrMore() {
        Manager manager = new Manager.Default();
        assertThat("one or more state dont consume every repetition",
            manager.oneOrMore(manager.symbol('a')).match(Input.of("aaa")).input().index(), is(3));
    }

    @DisplayName("Повторение от нуля пропускает своё тело")
    @Test
    @Timeout(5)
    void reportsSuccessWhenNoneOrMoreSkipsItsBody() {
        Manager manager = new Manager.Default();
        assertThat("none or more state dont report success when it skips its own body",
            manager.noneOrMore(manager.symbol('a')).match(Input.of("b")).ok(), is(true));
    }

    @DisplayName("Необязательное состояние не совпадает с чужим символом")
    @Test
    @Timeout(5)
    void cannotMatchNoneOrOneAgainstForeignSymbol() {
        Manager manager = new Manager.Default();
        assertThat("none or one state matches a foreign symbol",
            manager.noneOrOne(manager.symbol('a')).match(Input.of("b")).ok(), is(false));
    }

    @DisplayName("Группа не совпадает ни с каким входом")
    @Test
    @Timeout(5)
    void cannotMatchGroup() {
        assertThat("group matches an input",
            new Manager.Default().group().match(Input.of("a")).ok(), is(false));
    }

    @DisplayName("Концом последовательности остаётся последнее добавленное состояние")
    @Test
    @Timeout(5)
    void keepsLastAddedStateAsSequenceEnd() {
        Manager manager = new Manager.Default();
        State.Sequence sequence = manager.sequence();
        sequence.add(manager.symbol('a'));
        State.Symbol last = manager.symbol('b');
        sequence.add(last);
        assertThat("sequence dont keep its own last added state as the end",
            sequence.lastState(), is(sameInstance(last)));
    }

    @DisplayName("Концом параллели остаётся она сама")
    @Test
    @Timeout(5)
    void keepsItselfAsParallelEnd() {
        Manager manager = new Manager.Default();
        State.Parallel parallel = manager.parallel();
        parallel.add(manager.symbol('x'));
        assertThat("parallel dont keep itself as the end",
            parallel.lastState(), is(sameInstance(parallel)));
    }

    @DisplayName("Концом символа остаётся его продолжение")
    @Test
    @Timeout(5)
    void keepsContinuationAsSymbolEnd() {
        Manager manager = new Manager.Default();
        State.Symbol symbol = manager.symbol('c');
        State.Symbol next = manager.symbol('d');
        symbol.setNext(next);
        assertThat("symbol dont keep its own continuation as the end",
            symbol.lastState(), is(sameInstance(next)));
    }

    @DisplayName("Символ подписывается на диаграмме своим символом")
    @Test
    @Timeout(5)
    void labelsSymbolWithItsCharacter() {
        assertThat("symbol dont label itself with its own character",
            new Manager.Default().symbol('c').diagramLabel(), is("c"));
    }

    @DisplayName("Группа подписывается на диаграмме эпсилоном")
    @Test
    @Timeout(5)
    void labelsGroupAsEpsilon() {
        assertThat("group dont label itself as an epsilon",
            new Manager.Default().group().diagramLabel(), is("Epsilon"));
    }

    @DisplayName("Совпадение завершено на исчерпанном входе")
    @Test
    @Timeout(5)
    void completesWhenTheInputIsExhausted() {
        assertThat("match dont complete on an exhausted input",
            State.Match.ok(Input.of("")).isCompleted(), is(true));
    }

    @DisplayName("Совпадение не завершено с остатком входа")
    @Test
    @Timeout(5)
    void cannotCompleteWithTheInputLeft() {
        assertThat("match completes with the input left",
            State.Match.ok(Input.of("a")).isCompleted(), is(false));
    }

    @DisplayName("Отказ не считается совпадением")
    @Test
    @Timeout(5)
    void reportsFailureAsNoMatch() {
        assertThat("failure counts as a match",
            State.Match.failure(Input.of("a")).ok(), is(false));
    }
}
