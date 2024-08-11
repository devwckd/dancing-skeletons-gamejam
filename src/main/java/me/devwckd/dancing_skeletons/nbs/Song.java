package me.devwckd.dancing_skeletons.nbs;

import com.google.common.io.LittleEndianDataInputStream;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

public class Song {

    private static final Logger LOGGER = LoggerFactory.getLogger(Song.class);

    private final short length;
    private final short height;
    private final String title;
    private final String author;
    private final String originalAuthor;
    private final String description;
    private final float speed;
    private final Map<Integer, Layer> layers;

    public Song(short length, short height, String title, String author, String originalAuthor, String description, float speed, Map<Integer, Layer> layers) {
        this.length = length;
        this.height = height;
        this.title = title;
        this.author = author;
        this.originalAuthor = originalAuthor;
        this.description = description;
        this.speed = speed;
        this.layers = layers;
    }

    public short getLength() {
        return length;
    }

    public short getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getOriginalAuthor() {
        return originalAuthor;
    }

    public String getDescription() {
        return description;
    }

    public float getSpeed() {
        return speed;
    }

    public Map<Integer, Layer> getLayers() {
        return layers;
    }

    public void play(Player player, int tick) {
        for (final var layer : layers.values()) {
            layer.play(player, tick);
        }
    }

    public static Song parse(InputStream inputStream) {

        try(final var dataInputStream = new LittleEndianDataInputStream(inputStream)) {

            final var length = dataInputStream.readShort();
            final var height = dataInputStream.readShort();
            final var title = readString(dataInputStream);
            final var author = readString(dataInputStream);
            final var originalAuthor = readString(dataInputStream);
            final var description = readString(dataInputStream);
            final var speed = (float) dataInputStream.readShort() / 100f;

            // editor-related bullshit
            dataInputStream.readBoolean();
            dataInputStream.readByte();
            dataInputStream.readByte();
            dataInputStream.readInt();
            dataInputStream.readInt();
            dataInputStream.readInt();
            dataInputStream.readInt();
            dataInputStream.readInt();
            readString(dataInputStream);

            final var layers = new TreeMap<Integer, Layer>();
            var tick = -1;
            while (true) {
                var jumpTicks = dataInputStream.readShort();
                if (jumpTicks == 0) {
                    break;
                }

                tick += jumpTicks;

                var layerIdx = -1;
                while (true) {
                    short jumpLayers = dataInputStream.readShort();
                    if (jumpLayers == 0) {
                        break;
                    }

                    layerIdx += jumpLayers;
                    final var layer = layers.computeIfAbsent(layerIdx, $ -> new Layer());
                    layer.setNote(tick, new Note(dataInputStream.readByte(), dataInputStream.readByte()));
                }

            }

            for (var i = 0; i < height; i++) {
                final var layer = layers.get(i);
                if (layer != null) {
                    layer.setName(readString(dataInputStream));
                    layer.setVolume(dataInputStream.readByte());
                }
            }


            return new Song(
                    length,
                    height,
                    title,
                    author,
                    originalAuthor,
                    description,
                    speed,
                    layers
            );
        } catch (Throwable throwable) {
            LOGGER.error("Could not load song", throwable);
            throw new RuntimeException(throwable);
        }
    }

    private static String readString(@NotNull LittleEndianDataInputStream leids) throws IOException {
        final var length = leids.readInt();
        return new String(leids.readNBytes(length));
    }

}
