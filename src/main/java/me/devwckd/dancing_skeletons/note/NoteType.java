package me.devwckd.dancing_skeletons.note;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;

public enum NoteType {
    GREEN(new Pos(156.5, -59.0, 32.5, 0, 0), new Pos(156.5, -59.0, 11.5, 0, 0), new Pos(156.5, -59.0, 11, 0, 0),NamedTextColor.GREEN),
    RED(new Pos(155.5, -59.0, 32.5, 0, 0), new Pos(155.5, -59.0, 11.5, 0, 0), new Pos(155.5, -59.0, 11, 0, 0),NamedTextColor.RED),
    YELLOW(new Pos(154.5, -59.0, 32.5, 0, 0), new Pos(154.5, -59.0, 11.5, 0, 0), new Pos(154.5, -59.0, 11, 0, 0),NamedTextColor.YELLOW),
    BLUE(new Pos(153.5, -59.0, 32.5, 0, 0), new Pos(153.5, -59.0, 11.5, 0, 0), new Pos(153.5, -59.0, 11, 0, 0),NamedTextColor.BLUE),
    ORANGE(new Pos(152.5, -59.0, 32.5, 0, 0), new Pos(152.5, -59.0, 11.5, 0, 0), new Pos(152.5, -59.0, 11, 0, 0),NamedTextColor.GOLD);

    private final Pos spawnPos;
    private final Pos clickPos;
    private final Pos missPos;
    private final NamedTextColor teamColor;

    NoteType(Pos spawnPos, Pos clickPos, Pos missPos, NamedTextColor teamColor) {
        this.spawnPos = spawnPos;
        this.clickPos = clickPos;
        this.missPos = missPos;
        this.teamColor = teamColor;
    }

    public Pos getSpawnPos() {
        return spawnPos;
    }

    public Pos getClickPos() {
        return clickPos;
    }

    public Pos getMissPos() {
        return missPos;
    }

    public NamedTextColor getTeamColor() {
        return teamColor;
    }
}
