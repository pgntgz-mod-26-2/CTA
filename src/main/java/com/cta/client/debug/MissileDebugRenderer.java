package com.cta.client.debug;

import com.cta.CTA;
import com.cta.command.CTADebugCommand;
import net.createmod.catnip.api.client.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = CTA.MODID, value = Dist.CLIENT)
public class MissileDebugRenderer {
    
    private static final List<FlightPath> flightPaths = new CopyOnWriteArrayList<>();
    private static final List<ExplosionDebug> explosions = new CopyOnWriteArrayList<>();
    private static final List<PenetrationDebug> penetrations = new CopyOnWriteArrayList<>();
    private static final List<FragmentDebug> fragments = new CopyOnWriteArrayList<>();
    
    private static final int VISUALIZATION_DURATION = 200; 
    
    public static void addFlightPoint(int entityId, Vec3 position, DebugColor color) {
        if (!CTADebugCommand.isDebugEnabled()) return;
        
        FlightPath path = flightPaths.stream()
                .filter(p -> p.entityId == entityId)
                .findFirst()
                .orElse(null);
        
        if (path == null) {
            path = new FlightPath(entityId, color);
            flightPaths.add(path);
        }
        
        path.addPoint(position);
    }
    
    public static void addExplosion(Vec3 center, float radius, DebugColor color) {
        if (!CTADebugCommand.isDebugEnabled()) return;
        explosions.add(new ExplosionDebug(center, radius, color, VISUALIZATION_DURATION));
    }
    
    public static void addPenetrationPath(Vec3 start, Vec3 end, boolean succeeded) {
        if (!CTADebugCommand.isDebugEnabled()) return;
        penetrations.add(new PenetrationDebug(start, end, succeeded, VISUALIZATION_DURATION));
    }
    
    public static void addFragment(Vec3 origin, Vec3 direction, double range, boolean hitEntity) {
        if (!CTADebugCommand.isDebugEnabled()) return;
        fragments.add(new FragmentDebug(origin, direction, range, hitEntity, VISUALIZATION_DURATION));
    }
    
    public static void clearAll() {
        flightPaths.clear();
        explosions.clear();
        penetrations.clear();
        fragments.clear();
    }
    
    public static void removeFlightPath(int entityId) {
        flightPaths.removeIf(p -> p.entityId == entityId);
    }
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!CTADebugCommand.isDebugEnabled()) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        updateTimers();
        renderOutlines();
    }

    private static void renderOutlines() {
        Outliner outliner = Outliner.getInstance();

        for (FlightPath path : flightPaths) {
            List<Vec3> points = path.points;
            for (int i = 0; i < points.size() - 1; i++) {
                outliner.showLine("cta_dbg_fp_" + path.entityId + "_" + i, points.get(i), points.get(i + 1))
                        .colored(path.color.rgb())
                        .lineWidth(1 / 16f);
            }
        }

        int expIdx = 0;
        for (ExplosionDebug explosion : explosions) {
            drawSphereWireframe(outliner, "cta_dbg_exp_" + (expIdx++), explosion.center, explosion.radius, explosion.color);
        }

        int penIdx = 0;
        for (PenetrationDebug pen : penetrations) {
            DebugColor color = pen.succeeded ? DebugColor.ORANGE : DebugColor.DARK_RED;
            String keyPrefix = "cta_dbg_pen_" + (penIdx++);
            outliner.showLine(keyPrefix + "_line", pen.start, pen.end)
                    .colored(color.rgb())
                    .lineWidth(1 / 16f);
            if (!pen.succeeded) {
                drawX(outliner, keyPrefix + "_x", pen.end, 0.3, DebugColor.DARK_RED);
            }
        }

        int fragIdx = 0;
        for (FragmentDebug frag : fragments) {
            Vec3 end = frag.origin.add(frag.direction.scale(frag.range));
            DebugColor color = frag.hitEntity ? DebugColor.RED : DebugColor.YELLOW;
            outliner.showLine("cta_dbg_frag_" + (fragIdx++), frag.origin, end)
                    .colored(color.rgb())
                    .lineWidth(1 / 16f);
        }
    }
    
    private static void updateTimers() {
        explosions.removeIf(exp -> {
            exp.ticksRemaining--;
            return exp.ticksRemaining <= 0;
        });
        
        penetrations.removeIf(pen -> {
            pen.ticksRemaining--;
            return pen.ticksRemaining <= 0;
        });
        
        fragments.removeIf(frag -> {
            frag.ticksRemaining--;
            return frag.ticksRemaining <= 0;
        });
    }
    
    private static void drawX(Outliner outliner, String keyPrefix, Vec3 center, double size, DebugColor color) {
        outliner.showLine(keyPrefix + "_0", center.add(-size, 0, -size), center.add(size, 0, size))
                .colored(color.rgb())
                .lineWidth(1 / 16f);
        outliner.showLine(keyPrefix + "_1", center.add(size, 0, -size), center.add(-size, 0, size))
                .colored(color.rgb())
                .lineWidth(1 / 16f);
        outliner.showLine(keyPrefix + "_2", center.add(0, -size, -size), center.add(0, size, size))
                .colored(color.rgb())
                .lineWidth(1 / 16f);
    }
    
    private static void drawSphereWireframe(Outliner outliner, String keyPrefix, Vec3 center, float radius, DebugColor color) {
        int segments = 16;
        
        for (int i = 0; i < segments; i++) {
            double angle1 = (i / (double) segments) * Math.PI * 2;
            double angle2 = ((i + 1) / (double) segments) * Math.PI * 2;
            
            Vec3 p1 = center.add(Math.cos(angle1) * radius, 0, Math.sin(angle1) * radius);
            Vec3 p2 = center.add(Math.cos(angle2) * radius, 0, Math.sin(angle2) * radius);
            outliner.showLine(keyPrefix + "_xz_" + i, p1, p2).colored(color.rgb()).lineWidth(1 / 16f);
            
            p1 = center.add(Math.cos(angle1) * radius, Math.sin(angle1) * radius, 0);
            p2 = center.add(Math.cos(angle2) * radius, Math.sin(angle2) * radius, 0);
            outliner.showLine(keyPrefix + "_xy_" + i, p1, p2).colored(color.rgb()).lineWidth(1 / 16f);
            
            p1 = center.add(0, Math.cos(angle1) * radius, Math.sin(angle1) * radius);
            p2 = center.add(0, Math.cos(angle2) * radius, Math.sin(angle2) * radius);
            outliner.showLine(keyPrefix + "_yz_" + i, p1, p2).colored(color.rgb()).lineWidth(1 / 16f);
        }
    }
    
    public enum DebugColor {
        RED(255, 0, 0, 255),
        DARK_RED(139, 0, 0, 255),
        ORANGE(255, 165, 0, 255),
        YELLOW(255, 255, 0, 255),
        GREEN(0, 255, 0, 255),
        BLUE(0, 100, 255, 255),
        CYAN(0, 255, 255, 255),
        WHITE(255, 255, 255, 255);
        
        final int r, g, b, a;
        
        DebugColor(int r, int g, int b, int a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        int rgb() {
            return (a << 24) | (r << 16) | (g << 8) | b;
        }
    }
    
    private static class FlightPath {
        final int entityId;
        final DebugColor color;
        final List<Vec3> points = new ArrayList<>();
        
        FlightPath(int entityId, DebugColor color) {
            this.entityId = entityId;
            this.color = color;
        }
        
        void addPoint(Vec3 point) {
            if (points.size() > 500) {
                points.remove(0);
            }
            points.add(point);
        }
    }
    
    private static class ExplosionDebug {
        final Vec3 center;
        final float radius;
        final DebugColor color;
        int ticksRemaining;
        
        ExplosionDebug(Vec3 center, float radius, DebugColor color, int duration) {
            this.center = center;
            this.radius = radius;
            this.color = color;
            this.ticksRemaining = duration;
        }
    }
    
    private static class PenetrationDebug {
        final Vec3 start;
        final Vec3 end;
        final boolean succeeded;
        int ticksRemaining;
        
        PenetrationDebug(Vec3 start, Vec3 end, boolean succeeded, int duration) {
            this.start = start;
            this.end = end;
            this.succeeded = succeeded;
            this.ticksRemaining = duration;
        }
    }
    
    private static class FragmentDebug {
        final Vec3 origin;
        final Vec3 direction;
        final double range;
        final boolean hitEntity;
        int ticksRemaining;
        
        FragmentDebug(Vec3 origin, Vec3 direction, double range, boolean hitEntity, int duration) {
            this.origin = origin;
            this.direction = direction;
            this.range = range;
            this.hitEntity = hitEntity;
            this.ticksRemaining = duration;
        }
    }
}
