package ru.mifi.practice.vol11;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Задание слайда 540: волны сборки. Реализуйте Waves.Layered, снимите {@code @Disabled} и
 * добейтесь зелёных тестов.
 */
@DisplayName("Волны сборки")
final class WavesTest {

    @Disabled("Учебное задание слайда 540: волны сборки")
    @DisplayName("Цепочка зависимостей собирается по одному модулю в волне")
    @Test
    @Timeout(1)
    void buildsAChainOneModulePerWave() {
        List<Waves.Dependency> dependencies = List.of(
            new Waves.Dependency("web", "core"), new Waves.Dependency("core", "commons")
        );
        assertThat("a chain dont build one module per wave",
            new Waves.Layered().waves(dependencies), contains(Set.of("commons"), Set.of("core"), Set.of("web")));
    }

    @Disabled("Учебное задание слайда 540: параллельная волна")
    @DisplayName("Независимые модули попадают в одну волну")
    @Test
    @Timeout(1)
    void putsIndependentModulesIntoOneWave() {
        List<Waves.Dependency> dependencies = List.of(
            new Waves.Dependency("graphs", "commons"), new Waves.Dependency("strings", "commons"),
            new Waves.Dependency("app", "graphs"), new Waves.Dependency("app", "strings")
        );
        assertThat("independent modules dont share a wave",
            new Waves.Layered().waves(dependencies).get(1), is(Set.of("graphs", "strings")));
    }

    @Disabled("Учебное задание слайда 540: цикл зависимостей")
    @DisplayName("Цикл зависимостей делает сборку невозможной")
    @Test
    @Timeout(1)
    void refusesACyclicBuild() {
        List<Waves.Dependency> dependencies = List.of(
            new Waves.Dependency("a", "b"), new Waves.Dependency("b", "a")
        );
        assertThrows(IllegalStateException.class, () -> new Waves.Layered().waves(dependencies),
            "a cyclic build gets a build order");
    }
}
