package me.devwckd.dancing_skeletons.util;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import me.devwckd.dancing_skeletons.note.NoteType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

public final class Notes {

    private Notes() {
        throw new UnsupportedOperationException();
    }

    public static void populateNoteMap(Path path, Map<Integer, NoteType> notes) {
        try {
            final var inputStream = new LittleEndianDataInputStream(new ByteArrayInputStream(Files.readAllBytes(path)));
            final var count = inputStream.readInt();
            for (int i = 0; i < count; i++) {
                final var tick = inputStream.readShort();
                final var note = inputStream.readByte();
                final var skeletonType = NoteType.values()[note];
                notes.put((int) tick, skeletonType);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveNoteMap(Path path, Map<Integer, NoteType> notes) {
        try {
            final var internal = new ByteArrayOutputStream();
            final var outputStream = new LittleEndianDataOutputStream(internal);
            outputStream.writeInt(notes.size());
            for (Map.Entry<Integer, NoteType> entry : notes.entrySet()) {
                outputStream.writeShort(entry.getKey());
                outputStream.writeByte(entry.getValue().ordinal());
            }

            Files.write(path, internal.toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
