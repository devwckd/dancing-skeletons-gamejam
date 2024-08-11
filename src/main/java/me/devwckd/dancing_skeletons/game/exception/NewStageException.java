package me.devwckd.dancing_skeletons.game.exception;

import me.devwckd.dancing_skeletons.game.Stage;

public class NewStageException extends RuntimeException {

    private final Stage newStage;

    public NewStageException(Stage newStage) {
        this.newStage = newStage;
    }

    public Stage getNewStage() {
        return newStage;
    }
}
