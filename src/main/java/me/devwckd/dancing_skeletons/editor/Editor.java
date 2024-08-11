package me.devwckd.dancing_skeletons.editor;

import me.devwckd.dancing_skeletons.DancingSkeletons;
import me.devwckd.dancing_skeletons.note.NoteType;
import me.devwckd.dancing_skeletons.nbs.Song;
import me.devwckd.dancing_skeletons.util.Notes;
import net.hollowcube.polar.PolarLoader;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.*;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.nio.file.Files;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Editor {

    private final Song song;
    private final PolarLoader polarLoader;

    private int tick = 0;
    private NavigableMap<Integer, NoteType> notes = new TreeMap<>();

    public Editor(Song song, PolarLoader polarLoader) {
        this.song = song;
        this.polarLoader = polarLoader;

        if(Files.exists(DancingSkeletons.NOTES_PATH)) {
            Notes.populateNoteMap(DancingSkeletons.NOTES_PATH, notes);
        }
    }

    public void start() {
        final var instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setChunkLoader(polarLoader);
        instance.setChunkSupplier(LightingChunk::new);

        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockPlaceEvent.class, event -> {
            event.setCancelled(true);
        });

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final var player = event.getPlayer();
            if(!MinecraftServer.getConnectionManager().getOnlinePlayers().isEmpty()) {
                player.kick(Component.text("Not available."));
                return;
            }

            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(7.5, -53.5, -16.5));
            setupEditor(player);
        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, event -> {
            updateInventory(event.getPlayer());
        });
    }

    private void setupEditor(Player player) {
        player.eventNode().addListener(PlayerUseItemEvent.class, event -> {
            byte slot = event.getPlayer().getHeldSlot();

            if(slot < NoteType.values().length) {
                if(player.isSneaking()) {
                    notes.remove(tick);
                    return;
                }
                final var newType = NoteType.values()[slot];
                notes.put(tick, newType);
                updateInventory(player);
                return;
            }

            if(slot == 5) {
                Notes.saveNoteMap(DancingSkeletons.NOTES_PATH, notes);
            }else if(slot == 6) {
                if(tick > 0) {
                    tick --;
                    updateInventory(player);
                    song.play(player, tick);
                }
            } else if(slot == 7) {
                if(player.isSneaking()) {
                    tick = 0;
                } else {
                    song.play(player, tick);
                }
            } else if(slot == 8) {
                if(tick < song.getLength()) {
                    tick ++;
                    updateInventory(player);
                    song.play(player, tick);
                }
            }
        });
    }

    private void updateInventory(Player player) {
        for (NoteType value : NoteType.values()) {
            player.getInventory().setItemStack(value.ordinal(), ItemStack.of(getMaterial(value)));
        }
        player.getInventory().setItemStack(5, ItemStack.of(Material.PAPER));
        player.getInventory().setItemStack(6, ItemStack.of(Material.STICK));
        player.getInventory().setItemStack(7, ItemStack.of(Material.LEATHER));
        player.getInventory().setItemStack(8, ItemStack.of(Material.STICK));
    }

    private Material getMaterial(NoteType noteType) {
        switch (noteType) {
            case GREEN -> {
                if(noteType.equals(notes.get(tick))) {
                    return Material.LIME_CONCRETE;
                }

                return Material.LIME_STAINED_GLASS;
            }
            case RED -> {
                if(noteType.equals(notes.get(tick))) {
                    return Material.RED_CONCRETE;
                }

                return Material.RED_STAINED_GLASS;
            }
            case YELLOW -> {
                if(noteType.equals(notes.get(tick))) {
                    return Material.YELLOW_CONCRETE;
                }

                return Material.YELLOW_STAINED_GLASS;
            }
            case BLUE -> {
                if(noteType.equals(notes.get(tick))) {
                    return Material.BLUE_CONCRETE;
                }

                return Material.BLUE_STAINED_GLASS;
            }
            case ORANGE -> {
                if(noteType.equals(notes.get(tick))) {
                    return Material.ORANGE_CONCRETE;
                }

                return Material.ORANGE_STAINED_GLASS;
            }
        }

        return null;
    }

}
