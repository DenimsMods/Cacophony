package dev.denimred.cacophony;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public interface CacophonyPlatform {
    @ExpectPlatform
    static CacophonyPlatform getInstance() {
        throw new AssertionError("Missing injected platform instance");
    }

    Path getConfigDir();
    boolean isOnServer();
}
