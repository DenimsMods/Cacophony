package dev.denimred.cacophony.fabric;

import dev.denimred.cacophony.CacophonyPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class CacophonyPlatformImpl implements CacophonyPlatform {
    private static final CacophonyPlatform INSTANCE = new CacophonyPlatformImpl();

    public static CacophonyPlatform getInstance() {
        return INSTANCE;
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isOnServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }
}
