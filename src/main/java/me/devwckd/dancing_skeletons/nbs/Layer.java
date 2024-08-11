package me.devwckd.dancing_skeletons.nbs;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;

import java.util.Map;
import java.util.TreeMap;

public class Layer {
    private final Map<Integer, Note> notes = new TreeMap<>();
    private String name;
    private byte volume;

    public Layer() {
    }

    public void setNote(int tick, Note note) {
        notes.put(tick, note);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        this.volume = volume;
    }

    public void play(Player player, int tick) {
        final var note = notes.get(tick);
        if (note == null) {
            return;
        }

        Key soundKey = getSoundKey(note.instrument());

//        if (soundKey == BLOCK_NOTE_HARP) {
//            return;
//        }

        final var sound = Sound
                .sound()
                .source(Sound.Source.RECORD)
                .type(soundKey)
                //                .volume(volume / 1000000f)
                .volume(1.0f)
                .pitch(NOTES[note.key() - 33])
                .build();

        player.playSound(sound, player);
    }

    private static final Float[] NOTES = new Float[]{0.5F, 0.53F, 0.56F, 0.6F, 0.63F, 0.67F, 0.7F, 0.76F, 0.8F, 0.84F, 0.9F, 0.94F, 1.0F, 1.06F, 1.12F, 1.18F, 1.26F, 1.34F, 1.42F, 1.5F, 1.6F, 1.68F, 1.78F, 1.88F, 2.0F};

    private static final Key BLOCK_NOTE_HARP = Key.key("block.note_block.harp");
    private static final Key BLOCK_NOTE_BASS = Key.key("block.note_block.bass");
    private static final Key BLOCK_NOTE_BASEDRUM = Key.key("block.note_block.basedrum");
    private static final Key BLOCK_NOTE_SNARE = Key.key("block.note_block.snare");
    private static final Key BLOCK_NOTE_HAT = Key.key("block.note_block.hat");
    private static final Key BLOCK_NOTE_GUITAR = Key.key("block.note_block.guitar");
    private static final Key BLOCK_NOTE_FLUTE = Key.key("block.note_block.flute");
    private static final Key BLOCK_NOTE_BELL = Key.key("block.note_block.bell");
    private static final Key BLOCK_NOTE_CHIME = Key.key("block.note_block.chime");
    private static final Key BLOCK_NOTE_XYLOPHONE = Key.key("block.note_block.xylophone");

    private static Key getSoundKey(byte instrument) {
        switch (instrument) {
            case 0:
                return BLOCK_NOTE_HARP;
            case 1:
                return BLOCK_NOTE_BASS;
            case 2:
                return BLOCK_NOTE_BASEDRUM;
            case 3:
                return BLOCK_NOTE_SNARE;
            case 4:
                return BLOCK_NOTE_HAT;
            case 5:
                return BLOCK_NOTE_GUITAR;
            case 6:
                return BLOCK_NOTE_FLUTE;
            case 7:
                return BLOCK_NOTE_BELL;
            case 8:
                return BLOCK_NOTE_CHIME;
            case 9:
                return BLOCK_NOTE_XYLOPHONE;
            default:
                return BLOCK_NOTE_HARP;
        }
    }
}
