package me.devwckd.dancing_skeletons.game.stage;

import me.devwckd.dancing_skeletons.DancingSkeletons;
import me.devwckd.dancing_skeletons.game.Stage;
import me.devwckd.dancing_skeletons.note.Note;
import me.devwckd.dancing_skeletons.note.NoteType;
import me.devwckd.dancing_skeletons.nbs.Song;
import me.devwckd.dancing_skeletons.util.Notes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.SharedInstance;
import net.minestom.server.scoreboard.Sidebar;

import java.util.*;

public class GameStage implements Stage {

    private final Song song;
    private final InstanceContainer lobbyContainer;
    private final int songInterval;
    private final Map<UUID, Participant> participants;
    private final Map<Integer, NoteType> notes;

    private InstanceContainer gameContainer;
    private int tick = 0;
    private int songTick = 0;

    public GameStage(Song song, InstanceContainer lobbyContainer) {
        this.song = song;
        this.lobbyContainer = lobbyContainer;
        this.songInterval = (int) Math.round(20.0 / song.getSpeed());
        this.participants = new HashMap<>();
        this.notes = new TreeMap<>();
        Notes.populateNoteMap(DancingSkeletons.NOTES_PATH, notes);
    }

    @Override
    public void onStart() {
        createGameContainer();
        managerPlayers();
    }

    @Override
    public void onTick() {
        if (tick % songInterval == 0) {
            playSong();

            final var skeletonType = notes.get(songTick + 24);
            if (skeletonType != null) {
                for (final var participant : participants.values()) {
                    final var dancingSkeleton = new Note(skeletonType);
                    participant.spawn(dancingSkeleton);
                }
            }

            songTick++;
        }


        for (final var participant : participants.values()) {
            participant.move();
        }

        tick++;
    }

    @Override
    public void onEnd() {

    }

    private void createGameContainer() {
        gameContainer = MinecraftServer.getInstanceManager().createInstanceContainer(lobbyContainer.getChunkLoader());
    }

    private void managerPlayers() {
        for (final var onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            onlinePlayer.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(6.5);
            final var sharedInstance = MinecraftServer.getInstanceManager().createSharedInstance(gameContainer);
            final var participant = new Participant(onlinePlayer, sharedInstance);
            participant.teleport();
            participants.put(onlinePlayer.getUuid(), participant);
        }
    }

    private void playSong() {
        for (final var participant : participants.values()) {
            song.play(participant.getPlayer(), songTick);
        }
    }

    private static class Participant {
        private final Player player;
        private final SharedInstance sharedInstance;
        private final Map<UUID, Note> skeletons;
        private final Sidebar sidebar;

        private int streak = 0;
        private int totalNotes = 0;

        private Participant(Player player, SharedInstance sharedInstance) {
            this.player = player;
            this.sharedInstance = sharedInstance;
            this.skeletons = new HashMap<>();
            this.sidebar = new Sidebar(DancingSkeletons.SIDEBAR_TITLE);

            int i = 9;
            sidebar.createLine(new Sidebar.ScoreboardLine("space-1", Component.text(" "), i--));
            sidebar.createLine(new Sidebar.ScoreboardLine("title", Component.text("  " + DancingSkeletons.SONG.getTitle() + "  ", NamedTextColor.GREEN), i--));
            sidebar.createLine(new Sidebar.ScoreboardLine("author", Component.text("  " + DancingSkeletons.SONG.getOriginalAuthor() + "  ", NamedTextColor.GREEN), i--));
            sidebar.createLine(new Sidebar.ScoreboardLine("space-2", Component.text(" "), i--));
            sidebar.createLine(new Sidebar.ScoreboardLine("streak", Component.text("  Streak: ", NamedTextColor.WHITE).append(Component.text(streak, NamedTextColor.YELLOW)), i--));
            sidebar.createLine(new Sidebar.ScoreboardLine("notes", Component.text("  ", NamedTextColor.WHITE).append(Component.text(streak, NamedTextColor.YELLOW)), i--));
            sidebar.createLine(new Sidebar.ScoreboardLine("space-3", Component.text(" "), i--));

            sidebar.addViewer(player);
            registerListeners();
        }

        public void registerListeners() {
            sharedInstance.eventNode().addListener(EntityAttackEvent.class, event -> {
                if (!(event.getEntity() instanceof Player entityPlayer)) {
                    return;
                }

                if (!(event.getTarget() instanceof Note note)) {
                    return;
                }

                if (entityPlayer.getUuid() != this.player.getUuid()) {
                    return;
                }

                note.remove();
                skeletons.remove(note.getUuid());

                // TODO: CALC POINTS
                // TODO: SPAWN HOLOGRAM
            });
        }

        public Player getPlayer() {
            return player;
        }

        public SharedInstance getSharedInstance() {
            return sharedInstance;
        }

        public void teleport() {
            player.setInstance(sharedInstance, new Pos(154.5, -58.5, 3.5));
            player.setFlying(false);
            player.setAllowFlying(false);
        }

        public void spawn(Note note) {
            skeletons.put(note.getUuid(), note);
            note.spawn(sharedInstance);
        }

        public void move() {
            skeletons.values().removeIf(skeleton -> {
                if (skeleton.getPosition().z() < skeleton.getNoteType().getMissPos().z()) {
                    skeleton.remove();
                    return true;
                }
                skeleton.move();
                return false;
            });
        }


    }
}
