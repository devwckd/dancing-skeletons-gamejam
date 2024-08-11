package me.devwckd.dancing_skeletons;

import me.devwckd.dancing_skeletons.editor.Editor;
import me.devwckd.dancing_skeletons.game.Game;
import me.devwckd.dancing_skeletons.game.stage.WaitStage;
import me.devwckd.dancing_skeletons.nbs.Layer;
import me.devwckd.dancing_skeletons.nbs.Song;
import net.hollowcube.polar.AnvilPolar;
import net.hollowcube.polar.ChunkSelector;
import net.hollowcube.polar.PolarLoader;
import net.hollowcube.polar.PolarWriter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.batch.RelativeBlockBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.DyedItemColor;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.time.Tick;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DancingSkeletons {

    public static final Song SONG = Song.parse(DancingSkeletons.class.getClassLoader().getResourceAsStream("billie_jean.nbs"));
    public static final Path NOTES_PATH = Path.of("billie_jean.notes");
    public static final Component SIDEBAR_TITLE = MiniMessage.miniMessage().deserialize("<gradient:#4d8bff:#4fff6c>ᴅᴀɴᴄɪɴɢ sᴋᴇʟᴇᴛᴏɴs");

    public static void main(String[] args) throws IOException {
        final var minecraftServer = MinecraftServer.init();

        final var polarWorld = AnvilPolar.anvilToPolar(Path.of(URI.create("file:///home/devwckd/world")), ChunkSelector.all());
        final var polarLoader = new PolarLoader(polarWorld);

        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, event -> {
            event.setCancelled(true);
        });

        final var game = new Game(new WaitStage(polarLoader));
        game.start();

//        final var editor = new Editor(SONG, polarLoader, Path.of("billie_jean.notes"));
//        editor.start();

        minecraftServer.start("127.0.0.1", 25565);
    }

}
