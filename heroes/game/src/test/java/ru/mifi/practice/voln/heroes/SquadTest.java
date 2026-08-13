package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/** Проверка снимка отряда: он считает то же, что живой отряд, и при этом ничего не меняет. */
@DisplayName("Снимок отряда")
final class SquadTest {

    @DisplayName("Снимок теряет от удара то же, что и живой отряд")
    @Test
    @Timeout(5)
    void losesTheSame() {
        Random random = new Random(20260814);
        String live = "";
        String snapshot = "";
        for (int trial = 0; trial < 200; trial++) {
            Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
            for (int health = 1; health <= 1 + random.nextInt(8); health++) {
                stack.add(new Unit(10 + random.nextInt(20), random.nextInt(10), health * 13, 4));
            }
            Unit.Squad damaged = stack.snapshot().damaged(trial);
            stack.damage(trial);
            live += String.format("%d:%d ", stack.size(), stack.totalHealth());
            snapshot += String.format("%d:%d ", damaged.size(), damaged.totalHealth());
        }
        assertThat("the snapshot counts the damage differently from the live stack",
            snapshot, is(live));
    }

    @DisplayName("Удар оставляет исходный снимок нетронутым")
    @Test
    @Timeout(1)
    void keepsTheStruckSnapshotWhole() {
        Unit.Squad squad = new Unit.Squad(Unit.Type.WALKER, List.of(new Unit.Fighter(10, 0, 40, 4)), 0);
        squad.damaged(1000);
        assertThat("a strike counted in the mind wipes out the snapshot it was counted on",
            squad.size(), is(1));
    }

    @DisplayName("Раненый боец остаётся в строю")
    @Test
    @Timeout(1)
    void keepsTheWoundedFighter() {
        Unit.Squad squad = new Unit.Squad(Unit.Type.WALKER, List.of(new Unit.Fighter(10, 0, 40, 4)), 0);
        assertThat("the fighter who survived the strike falls out of the squad",
            squad.damaged(10).totalHealth(), is(30));
    }

    @DisplayName("Слабый удар отряду ничего не делает")
    @Test
    @Timeout(1)
    void survivesTheWeakStrike() {
        Unit.Squad squad = new Unit.Squad(Unit.Type.WALKER, List.of(new Unit.Fighter(10, 15, 40, 4)), 0);
        assertThat("a strike weaker than the defense still wounds the fighter",
            squad.damaged(10).totalHealth(), is(40));
    }

    @DisplayName("Погибших от удара снимок считает наперёд")
    @Test
    @Timeout(1)
    void countsTheCasualtiesBeforehand() {
        Unit.Squad squad = new Unit.Squad(Unit.Type.WALKER,
            List.of(new Unit.Fighter(10, 0, 40, 4), new Unit.Fighter(10, 0, 40, 4)), 0);
        assertThat("the squad promises a number of casualties other than the strike brings",
            squad.casualties(50), is(squad.size() - squad.damaged(50).size()));
    }

    @DisplayName("Потраченная стрела оставляет исходный снимок при своих")
    @Test
    @Timeout(1)
    void keepsTheQuiverOfTheSnapshot() {
        Unit.Squad squad = new Unit.Squad(Unit.Type.SHOOTER, List.of(new Unit.Fighter(10, 0, 100, 4)), 3);
        squad.spent();
        assertThat("an arrow counted in the mind empties the quiver of the snapshot",
            squad.shots(), is(3));
    }

    @DisplayName("Без стрел снимок стрелять отказывается")
    @Test
    @Timeout(1)
    void refusesToShootWithoutArrows() {
        Unit.Squad squad = new Unit.Squad(Unit.Type.SHOOTER, List.of(new Unit.Fighter(10, 0, 100, 4)), 0);
        assertThat("the empty quiver still lets the shooter shoot",
            squad.maximumShot(1), is(0));
    }

    @DisplayName("Дальний выстрел слабее ближнего")
    @Test
    @Timeout(1)
    void weakensTheDistantShot() {
        Unit.Squad squad = new Unit.Squad(Unit.Type.SHOOTER, List.of(new Unit.Fighter(100, 0, 100, 4)), 5);
        assertThat("the distant shot hits as hard as the one fired point-blank",
            squad.maximumShot(Constants.SPAN) < squad.maximumShot(1), is(true));
    }

    @DisplayName("Скорость снимка — скорость самого медленного бойца")
    @Test
    @Timeout(1)
    void walksAtThePaceOfTheSlowest() {
        Unit.Squad squad = new Unit.Squad(Unit.Type.WALKER,
            List.of(new Unit.Fighter(10, 0, 100, 6), new Unit.Fighter(10, 0, 100, 2)), 0);
        assertThat("the squad walks faster than its slowest fighter",
            squad.speed(), is(2));
    }
}
