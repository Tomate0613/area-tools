package dev.doublekekse.area_tools.component.area;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.List;

public class EventsComponent {
    public List<String> onEnter;
    public List<String> onExit;

    public EventsComponent() {
        onEnter = new ArrayList<>();
        onExit = new ArrayList<>();
    }

    public EventsComponent(List<String> onEnter, List<String> onExit) {
        this.onEnter = new ArrayList<>(onEnter);
        this.onExit = new ArrayList<>(onExit);
    }

    public static Codec<EventsComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.listOf().fieldOf("on_enter").forGetter(v -> v.onEnter),
        Codec.STRING.listOf().fieldOf("on_exit").forGetter(v -> v.onExit)
    ).apply(instance, EventsComponent::new));

    public boolean isEmpty() {
        return onEnter.isEmpty() && onExit.isEmpty();
    }
}
