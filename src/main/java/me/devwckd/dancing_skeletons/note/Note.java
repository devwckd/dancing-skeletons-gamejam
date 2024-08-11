package me.devwckd.dancing_skeletons.note;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.joml.Quaternionf;

public class Note extends Entity {

    private static final ItemStack ITEM_STACK = ItemStack.of(Material.SKELETON_SKULL);

    private final NoteType noteType;
    private final Vec dir;

    private final Entity display;

    private float rotation = 0;
    private Quaternionf quat = new Quaternionf();

    public Note(NoteType noteType) {
        super(EntityType.INTERACTION);
        this.noteType = noteType;
        this.dir = noteType.getClickPos().asVec().sub(noteType.getSpawnPos().asVec()).div(24 * 3);

        editEntityMeta(InteractionMeta.class, meta -> {
            meta.setWidth(0.8f);
            meta.setHeight(0.8f);
            meta.setResponse(true);
        });

        this.display = new Entity(EntityType.INTERACTION);
        this.display.editEntityMeta(ItemDisplayMeta.class, meta -> {
            meta.setTranslation(new Pos(0.0f, 0.6f, 0.0f));
            meta.setItemStack(ITEM_STACK);
            meta.setHasGlowingEffect(true);
            meta.setGlowColorOverride(noteType.getTeamColor().value());
        });
    }

    public NoteType getNoteType() {
        return noteType;
    }

    public void spawn(Instance instance) {
        setInstance(instance, noteType.getSpawnPos());
        display.setInstance(instance, noteType.getSpawnPos());
    }

    public void move() {
        final var newPos = getPosition().add(dir);

        display.editEntityMeta(ItemDisplayMeta.class, meta -> {
            meta.setTransformationInterpolationStartDelta(-1);
            meta.setTransformationInterpolationDuration(1);
            quat.rotateAxis((float) (Math.PI / 18f), 0.0f, 1.0f, 0.0f);
            meta.setLeftRotation(new float[] {quat.x, quat.y, quat.z, quat.w});
        });
        display.teleport(newPos);
//        rotation += (float) (Math.PI / 18);

        teleport(newPos);
    }

    @Override
    public void remove() {
        super.remove();
        display.remove();
    }
}
