package me.devwckd.dancing_skeletons.game.stage;

import me.devwckd.dancing_skeletons.DancingSkeletons;
import me.devwckd.dancing_skeletons.game.Stage;
import net.hollowcube.polar.PolarLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.scoreboard.Sidebar;

public class WaitStage implements Stage {

    private final PolarLoader polarLoader;

    private Sidebar sidebar;
    private InstanceContainer lobbyContainer;
    private EventNode<Event> waitNode;

    private int millis = 15 * 1000;
    private int seconds = 15;

    public WaitStage(PolarLoader polarLoader) {
        this.polarLoader = polarLoader;
    }

    @Override
    public void onStart() {
        createSidebar();
        createContainer();
        registerListeners();
    }

    @Override
    public void onTick() {
        millis -= 1000/20;
        if(seconds != millis / 1000) {
            seconds = millis / 1000;
            sidebar.updateLineContent("time", Component.text("  " + seconds + " seconds", NamedTextColor.WHITE));
        }

        if (seconds < 1) {
            if(MinecraftServer.getConnectionManager().getOnlinePlayers().isEmpty()) {
                seconds = 60;
                millis = 15 * 1000;

                return;
            }

            transition(new GameStage(DancingSkeletons.SONG, lobbyContainer));
        }
    }

    @Override
    public void onEnd() {
        destroySidebar();
        unregisterListeners();
    }

    private void createSidebar() {
        sidebar = new Sidebar(DancingSkeletons.SIDEBAR_TITLE); //<gradient:#4d8bff:#4fff6c>
        sidebar.createLine(new Sidebar.ScoreboardLine("top-space", Component.empty(), 4));
        sidebar.createLine(new Sidebar.ScoreboardLine("starting", Component.text("  Game starting in:  ", NamedTextColor.YELLOW), 3));
        sidebar.createLine(new Sidebar.ScoreboardLine("time", Component.text("  " + seconds + " seconds  ", NamedTextColor.WHITE), 2));
        sidebar.createLine(new Sidebar.ScoreboardLine("bottom-space", Component.empty(), 1));
    }

    private void createContainer() {
        lobbyContainer = MinecraftServer.getInstanceManager().createInstanceContainer(polarLoader);
        lobbyContainer.setChunkSupplier(LightingChunk::new);
    }

    private void registerListeners() {
        waitNode = EventNode.all("waitNode");
        waitNode.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(lobbyContainer);

            final var player = event.getPlayer();
            player.setRespawnPoint(new Pos(7.5, -53.5, -16.5));
        });
        waitNode.addListener(PlayerSpawnEvent.class, event -> {
            final var player = event.getPlayer();
            sidebar.addViewer(player);
            player.setAllowFlying(true);
        });
        MinecraftServer.getGlobalEventHandler().addChild(waitNode);
    }

    private void destroySidebar() {
        for (final var viewer : sidebar.getViewers()) {
            sidebar.removeViewer(viewer);
        }
    }

    private void unregisterListeners() {
        MinecraftServer.getGlobalEventHandler().removeChild(waitNode);
    }
}
