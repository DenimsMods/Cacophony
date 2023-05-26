package dev.denimred.cacophony.config;

import com.google.gson.*;
import net.dv8tion.jda.api.OnlineStatus;

import java.lang.reflect.Type;

public class OnlineStatusAdapter implements JsonSerializer<OnlineStatus>, JsonDeserializer<OnlineStatus> {
    @Override
    public OnlineStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return OnlineStatus.fromKey(json.getAsString());
    }

    @Override
    public JsonElement serialize(OnlineStatus src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getKey());
    }
}
