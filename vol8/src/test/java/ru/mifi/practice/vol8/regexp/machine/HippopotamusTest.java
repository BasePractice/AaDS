package ru.mifi.practice.vol8.regexp.machine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mifi.practice.vol8.regexp.tree.Tree;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

//NOTICE: Специально для Сергея
@DisplayName("Hippopotamus")
public final class HippopotamusTest {
    private static final Hippopotamus A = new Hippopotamus(0);
    private static final Hippopotamus B = new Hippopotamus(1);
    private static final Hippopotamus C = new Hippopotamus(2);

    private static Stream<Arguments> patternMatching() {
        return Stream.of(
            Arguments.of(true, List.of(A), "a|b"),
            Arguments.of(true, List.of(B), "a|b"),
            Arguments.of(false, List.of(C), "a|b"),
            Arguments.of(true, List.of(A, B), "ab|c"),
            Arguments.of(false, List.of(A, B, C), "ab|c")
        );
    }

    private HippopotamusMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HippopotamusMapper(Map.of('a', A, 'b', B, 'c', C));
    }

    @ParameterizedTest
    @MethodSource("patternMatching")
    void match(boolean isMatch, List<Hippopotamus> hippopotamuses, String pattern) {
        Tree tree = new Tree.Default(pattern);
        Matcher match = new Matcher.Default(tree, new Manager.Default(mapper));
        Input input = new HippopotamusInput(hippopotamuses);
        boolean matched = match.match(input);
        assertEquals(isMatch, matched);
    }

    private record HippopotamusMapper(Map<Character, Hippopotamus> map)
        implements Manager.CharacterMapper<Hippopotamus> {

        @Override
        public Hippopotamus map(Character c) {
            return map.get(c);
        }
    }

    private record Hippopotamus(int index) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Hippopotamus that = (Hippopotamus) o;
            return index == that.index;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(index);
        }

        @Override
        public String toString() {
            return "Hippo{" + index + "}";
        }
    }

    private static final class HippopotamusInput extends Input.ObjectsInput {
        public HippopotamusInput(List<Hippopotamus> hippopotamuses) {
            super(hippopotamuses.toArray());
        }

        private HippopotamusInput(Object[] hippopotamuses, int index) {
            super(hippopotamuses, index);
        }

        @Override
        public Input copy() {
            return new HippopotamusInput(objects, index());
        }
    }
}
