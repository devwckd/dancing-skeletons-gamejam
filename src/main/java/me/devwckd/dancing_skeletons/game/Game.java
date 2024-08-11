package me.devwckd.dancing_skeletons.game;

import me.devwckd.dancing_skeletons.game.exception.NewStageException;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private boolean started = false;
    private Stage stage;
    private Task stageTask;

    public Game(Stage initialStage) {
        stage = initialStage;
    }

    public void start() {
        if(started) {
            throw new IllegalStateException("Game has already started.");
        }

        startStage(stage);
        started = true;
    }

    private void startStage(Stage newStage) {
        if(started && stage != null) {
            if(stageTask != null) {
                stageTask.cancel();
            }

            stage.onEnd();
        }

        this.stage = newStage;
        stage.onStart();
        stageTask = MinecraftServer.getSchedulerManager().submitTask(() -> {
            try {
                stage.onTick();
            } catch (NewStageException exception) {
                startStage(exception.getNewStage());
                return TaskSchedule.stop();
            } catch (Throwable throwable) {
                LOGGER.error("Exception while ticking {}:", stage.getClass().getSimpleName(), throwable);
            }

            return TaskSchedule.tick(stage.taskInterval());
        }, ExecutionType.TICK_END);
    }

}
