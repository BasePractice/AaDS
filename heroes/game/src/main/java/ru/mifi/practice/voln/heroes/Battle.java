package ru.mifi.practice.voln.heroes;

/**
 * Партия: чья сторона ходит, куда попадают решения игрока и кто отвечает за вторую сторону.
 *
 * <p>Интерфейс отделяет правила боя от того, откуда берётся ход соперника. Локальная партия
 * получает его от тактики, сетевая — из потока событий комнаты, а поле боя об этой разнице
 * не знает.
 */
public interface Battle {

    BattleMap map();

    /** Наша ли сторона ходит сейчас: пока не наша, клики по полю игнорируются. */
    boolean ours();

    /** Применить решение игрока. */
    void apply(Tactics.Decision decision);

    /** Дать сходить сопернику, если очередь его. */
    void answer();

    /** Перенести решение на поле боя от имени отряда, стоящего в голове очереди. */
    default void perform(Tactics.Decision decision) {
        int[] coord = map().getStackCoord(map().getTurnQueue().peekFirst());
        if (coord.length != 2) {
            return;
        }
        switch (decision.kind()) {
            case ATTACK -> map().attack(coord[0], coord[1], decision.row(), decision.column());
            case MOVE -> map().move(coord[0], coord[1], decision.row(), decision.column());
            case WAIT -> map().waitTurn();
            case SKIP -> map().skipTurn();
            default -> throw new IllegalArgumentException("Неизвестное решение тактики: " + decision.kind());
        }
    }

    /**
     * Игра за одним экраном: левой стороной играет человек, правой — тактика.
     *
     * <p>Ход соперника не считается сразу: интерфейс вызывает answer с задержкой, иначе вся
     * очередь правых отыгрывается быстрее, чем успевает нарисоваться хоть один кадр.
     */
    final class Local implements Battle {
        private final BattleMap map;
        private final Tactics tactics;

        public Local(BattleMap map, Tactics tactics) {
            this.map = map;
            this.tactics = tactics;
        }

        @Override
        public BattleMap map() {
            return map;
        }

        @Override
        public boolean ours() {
            return map.isLeftTurn();
        }

        @Override
        public void apply(Tactics.Decision decision) {
            perform(decision);
        }

        @Override
        public void answer() {
            if (ours() || map.isAnimating() || map.getTurnQueue().isEmpty()) {
                return;
            }
            perform(tactics.decide(map));
        }
    }
}
