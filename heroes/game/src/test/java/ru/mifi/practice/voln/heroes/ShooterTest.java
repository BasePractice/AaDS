package ru.mifi.practice.voln.heroes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/** Проверка стрельбы: дальность выстрела, падение урона с расстоянием и ближний бой. */
@DisplayName("Стрелок")
final class ShooterTest {

    @DisplayName("Ближний выстрел бьёт сильнее дальнего")
    @Test
    @Timeout(1)
    void weakensTheShotWithTheDistance() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        map.addRight(5, 7, stack(10, 0, 500, 5));
        map.addRight(5, 10, stack(10, 0, 500, 5));
        assertThat("the distance dont weaken the shot",
            map.shot(5, 5, 5, 10), is(lessThan(map.shot(5, 5, 5, 7))));
    }

    @DisplayName("Цель в пределах дальности стрелок достаёт")
    @Test
    @Timeout(1)
    void reachesTheTargetWithinTheRange() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        map.addRight(5, 11, stack(10, 0, 500, 5));
        assertThat("a target within the range stays out of reach", map.shot(5, 5, 5, 11), is(greaterThan(0)));
    }

    @DisplayName("За дальностью стрелок не достаёт")
    @Test
    @Timeout(1)
    void dropsTheTargetBeyondTheRange() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        map.addRight(5, 12, stack(10, 0, 500, 5));
        assertThat("a target beyond the range still gets shot", map.shot(5, 5, 5, 12), is(0));
    }

    @DisplayName("Пехота не стреляет")
    @Test
    @Timeout(1)
    void keepsTheWalkerFromShooting() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, stack(100, 0, 100, 5));
        map.addRight(5, 8, stack(10, 0, 500, 5));
        assertThat("a walker shoots at a distant target", map.shot(5, 5, 5, 8), is(0));
    }

    @DisplayName("В ближнем бою стрелок не стреляет")
    @Test
    @Timeout(1)
    void keepsTheShooterFromShootingInMelee() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        map.addRight(5, 6, stack(10, 0, 500, 5));
        map.addRight(5, 9, stack(10, 0, 500, 5));
        assertThat("a shooter with an enemy at hand still shoots", map.shot(5, 5, 5, 9), is(0));
    }

    @DisplayName("Выстрел оставляет стрелка на месте")
    @Test
    @Timeout(1)
    void keepsTheShooterOnItsCell() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        map.addRight(5, 9, stack(10, 0, 500, 5));
        map.shoot(5, 5, 5, 9);
        map.endAction();
        assertThat("the shot drags the shooter off its cell", map.getStack(5, 5), is(notNullValue()));
    }

    @DisplayName("Выстрел отправляет снаряд в полёт")
    @Test
    @Timeout(1)
    void sendsTheBoltFlyingBeforeTheHit() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        map.addRight(5, 9, stack(10, 0, 500, 5));
        map.shoot(5, 5, 5, 9);
        assertThat("the shot lands before the bolt flies", map.isAnimating(), is(true));
    }

    @DisplayName("До попадания цель невредима")
    @Test
    @Timeout(1)
    void holdsTheDamageUntilTheBoltLands() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        Unit.Stack target = stack(10, 0, 500, 5);
        map.addRight(5, 9, target);
        map.shoot(5, 5, 5, 9);
        assertThat("the target loses health before the bolt lands", target.totalHealth(), is(500));
    }

    @DisplayName("Выстрел ранит цель")
    @Test
    @Timeout(1)
    void hurtsTheTargetFromAfar() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        Unit.Stack target = stack(10, 0, 500, 5);
        map.addRight(5, 9, target);
        map.shoot(5, 5, 5, 9);
        map.endAction();
        assertThat("the shot leaves the target untouched", target.totalHealth(), is(lessThan(500)));
    }

    @DisplayName("На выстрел цель не отвечает")
    @Test
    @Timeout(1)
    void takesNoAnswerForTheShot() {
        BattleMap map = new BattleMap();
        Unit.Stack shooter = shooters(100, 100);
        map.addLeft(5, 5, shooter);
        map.addRight(5, 9, stack(100, 0, 500, 5));
        map.shoot(5, 5, 5, 9);
        map.endAction();
        assertThat("the target answers a shot fired from afar", shooter.totalHealth(), is(100));
    }

    @DisplayName("Погибшая от выстрела цель снимается с поля")
    @Test
    @Timeout(1)
    void clearsTheCellOfATargetShotDown() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        map.addRight(0, 14, stack(10, 0, 500, 5));
        map.addRight(5, 9, stack(10, 0, 5, 5));
        map.shoot(5, 5, 5, 9);
        map.endAction();
        assertThat("a stack shot down stays on the field", map.getStack(5, 9), is(nullValue()));
    }

    @DisplayName("Выстрел передаёт ход дальше")
    @Test
    @Timeout(1)
    void passesTheTurnAfterTheShot() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        map.addRight(5, 9, stack(10, 0, 500, 5));
        map.shoot(5, 5, 5, 9);
        map.endAction();
        assertThat("the shooter keeps the turn after the shot", map.isLeftTurn(), is(false));
    }

    @DisplayName("Выстрел попадает в журнал боя")
    @Test
    @Timeout(1)
    void logsTheShot() {
        BattleMap map = new BattleMap();
        List<String> logs = new ArrayList<>();
        map.addPropertyChangeListener(e -> {
            if ("log".equals(e.getPropertyName())) {
                logs.add((String) e.getNewValue());
            }
        });
        map.addLeft(5, 5, shooters(100, 100));
        map.addRight(5, 9, stack(10, 0, 500, 5));
        map.shoot(5, 5, 5, 9);
        map.endAction();
        assertThat("the shot dont reach the battle log", logs, hasItem(containsString("стреляет")));
    }

    @DisplayName("Руками стрелок бьёт слабее, чем стреляет в упор")
    @Test
    @Timeout(1)
    void swingsWeakerThanItShootsPointBlank() {
        Unit.Stack shooter = shooters(100, 100);
        assertThat("the shooter swings as hard as it shoots point blank",
            shooter.maximumMelee(), is(shooter.maximumShot(1) * 70 / 100));
    }

    @DisplayName("Пехота бьёт руками в полную силу")
    @Test
    @Timeout(1)
    void keepsTheWalkerSwingAtFullForce() {
        Unit.Stack walker = stack(100, 0, 100, 5);
        assertThat("the walker loses force when it swings", walker.maximumMelee(), is(walker.maximumAttack()));
    }

    @DisplayName("Дальнюю цель тактика расстреливает, а не сближается")
    @Test
    @Timeout(1)
    void shootsInsteadOfApproaching() {
        BattleMap map = new BattleMap();
        map.addLeft(5, 5, shooters(100, 100));
        map.addRight(5, 9, stack(10, 0, 500, 3));
        assertThat("tactics walk up to a target it can already shoot",
            new Tactics.Greedy().decide(map).kind(), is(Tactics.Kind.SHOOT));
    }

    private static Unit.Stack shooters(int attack, int health) {
        Unit.Stack stack = new Unit.Stack(Unit.Type.SHOOTER);
        stack.add(new Unit(attack, 0, health, 4));
        return stack;
    }

    private static Unit.Stack stack(int attack, int defense, int health, int speed) {
        Unit.Stack stack = new Unit.Stack(Unit.Type.WALKER);
        stack.add(new Unit(attack, defense, health, speed));
        return stack;
    }
}
