package ru.mifi.practice.vol6.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.util.Comparator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Дерево из явного описания связей")
final class TreeTest {

    @DisplayName("Описание задаёт корень дерева")
    @Test
    @Timeout(1)
    void takesTheRootFromTheDescription() throws IOException {
        Tree<Integer> tree = new ParserText<Integer>()
            .parse("1:{ 2,  3}\n3:{ 4,  5}\n", Integer::valueOf, Comparator.naturalOrder());
        assertThat("the description dont set the root", tree.find(1).value(), is(1));
    }

    @DisplayName("Описание связывает левого потомка")
    @Test
    @Timeout(1)
    void linksTheLeftChildFromTheDescription() throws IOException {
        Tree<Integer> tree = new ParserText<Integer>()
            .parse("1:{ 2,  3}\n3:{ 4,  5}\n", Integer::valueOf, Comparator.naturalOrder());
        assertThat("the description dont link the left child", tree.find(3).left().value(), is(4));
    }

    @DisplayName("Описание связывает правого потомка")
    @Test
    @Timeout(1)
    void linksTheRightChildFromTheDescription() throws IOException {
        Tree<Integer> tree = new ParserText<Integer>()
            .parse("1:{ 2,  3}\n3:{ 4,  5}\n", Integer::valueOf, Comparator.naturalOrder());
        assertThat("the description dont link the right child", tree.find(3).right().value(), is(5));
    }

    /**
     * Разбиение отбрасывало хвостовые пустые части, поэтому запись без пробелов падала
     * обращением за границу массива, а та же запись с пробелами читалась.
     */
    @DisplayName("Пропущенный потомок без пробелов читается так же, как с пробелами")
    @Test
    @Timeout(1)
    void readsAnOmittedChildWrittenTightly() throws IOException {
        Tree<Integer> tree = new ParserText<Integer>()
            .parse("1:{2,}\n", Integer::valueOf, Comparator.naturalOrder());
        assertThat("a tightly written omitted child breaks the parser", tree.find(1).right(), is(nullValue()));
    }

    @DisplayName("Описание без второго потомка отвергается сразу")
    @Test
    @Timeout(1)
    void refusesADescriptionWithoutASecondChild() {
        assertThrows(IllegalArgumentException.class,
            () -> new ParserText<Integer>().parse("1:{2}\n", Integer::valueOf, Comparator.naturalOrder()),
            "a description without a second child passes the parser");
    }

    @DisplayName("Пропущенный потомок остаётся пустым")
    @Test
    @Timeout(1)
    void leavesAnOmittedChildEmpty() throws IOException {
        Tree<Integer> tree = new ParserText<Integer>()
            .parse("1:{ 2,  3}\n2:{ 4,   }\n", Integer::valueOf, Comparator.naturalOrder());
        assertThat("an omitted child gets a node", tree.find(2).right(), is(nullValue()));
    }

    @DisplayName("Поиск не находит отсутствующее значение")
    @Test
    @Timeout(1)
    void findsNothingForAMissingValue() throws IOException {
        Tree<Integer> tree = new ParserText<Integer>()
            .parse("1:{ 2,  3}\n", Integer::valueOf, Comparator.naturalOrder());
        assertThat("a missing value is found", tree.find(99), is(nullValue()));
    }

    @DisplayName("Удаление отрывает узел от родителя")
    @Test
    @Timeout(1)
    void detachesADeletedNodeFromItsParent() throws IOException {
        Tree<Integer> tree = new ParserText<Integer>()
            .parse("1:{ 2,  3}\n", Integer::valueOf, Comparator.naturalOrder());
        tree.delete(2);
        assertThat("a deleted node stays attached to its parent", tree.find(1).left(), is(nullValue()));
    }

    @DisplayName("Копия повторяет структуру оригинала")
    @Test
    @Timeout(1)
    void repeatsTheOriginalStructureInACopy() throws IOException {
        Tree<Integer> tree = new ParserText<Integer>()
            .parse("1:{ 2,  3}\n3:{ 4,  5}\n", Integer::valueOf, Comparator.naturalOrder());
        assertThat("the copy dont repeat the original structure",
            tree.copy(value -> value).hash(), is(tree.hash()));
    }

    @DisplayName("Неизвестный владелец не принимает потомков")
    @Test
    @Timeout(1)
    void rejectsChildrenOfAnUnknownOwner() {
        Tree.Standard<Integer> tree = new Tree.Standard<Integer>(Comparator.naturalOrder());
        tree.add(1, 2, 3);
        assertThrows(IllegalArgumentException.class, () -> tree.add(99, 100, 101),
            "children of an unknown owner are accepted");
    }

    @DisplayName("Одиночное добавление создаёт корень пустого дерева")
    @Test
    @Timeout(1)
    void createsTheRootOfAnEmptyTreeByASingleValue() {
        Tree.Standard<Integer> tree = new Tree.Standard<Integer>(Comparator.naturalOrder());
        tree.add(1);
        assertThat("a single value dont create the root", tree.find(1).value(), is(1));
    }

    @DisplayName("Одиночное добавление в непустое дерево отвергается")
    @Test
    @Timeout(1)
    void rejectsASingleValueAddedToANonEmptyTree() {
        Tree.Standard<Integer> tree = new Tree.Standard<Integer>(Comparator.naturalOrder());
        tree.add(1);
        assertThrows(UnsupportedOperationException.class, () -> tree.add(2),
            "a single value is accepted by a non empty tree");
    }
}
