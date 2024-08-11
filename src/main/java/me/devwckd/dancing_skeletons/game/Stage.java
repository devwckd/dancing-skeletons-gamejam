package me.devwckd.dancing_skeletons.game;

import me.devwckd.dancing_skeletons.game.exception.NewStageException;

public interface Stage {

    void onStart();

    void onTick();

    void onEnd();

    default int taskInterval() {
        return 1;
    }

    default void transition(Stage newStage) throws NewStageException {
        throw new NewStageException(newStage);
    }
}
