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
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            Arguments.of(false, List.of(C), "a|b")
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
        Match match = new Match.Machine(tree, new Manager.Default(mapper));
        Input input = new HippopotamusInput(hippopotamuses);
        boolean matched = match.match(input);
        if (isMatch) {
            assertTrue(matched);
        } else {
            assertFalse(matched);
        }
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
            return String.valueOf(index);
        }
    }

    private static final class HippopotamusInput implements Input {
        private final List<Hippopotamus> hippopotamuses;
        private int index;

        public HippopotamusInput(List<Hippopotamus> hippopotamuses, int index) {
            this.hippopotamuses = List.copyOf(hippopotamuses);
            this.index = index;
        }

        public HippopotamusInput(List<Hippopotamus> hippopotamuses) {
            this(hippopotamuses, 0);
        }

        @Override
        public Marker mark() {
            return new Marker(index);
        }

        @Override
        public void reset(int pos) {
            index = pos;
        }

        @Override
        public Optional<Object> peek() {
            if (hasNext()) {
                return Optional.of(hippopotamuses.get(index));
            }
            return Optional.empty();
        }

        @Override
        public void next() {
            if (hasNext()) {
                index++;
            }
        }

        @Override
        public Input copy() {
            return new HippopotamusInput(hippopotamuses, index);
        }

        @Override
        public boolean hasNext() {
            return index < hippopotamuses.size();
        }

        @Override
        public int index() {
            return index;
        }
    }
}
