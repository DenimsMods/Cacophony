package dev.denimred.cacophony.forge;

import dev.denimred.cacophony.CacophonyPlatform;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class CacophonyPlatformImpl implements CacophonyPlatform {
    private static final CacophonyPlatform INSTANCE = new CacophonyPlatformImpl();

    public static CacophonyPlatform getInstance() {
        return INSTANCE;
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isOnServer() {
        return FMLEnvironment.dist.isDedicatedServer();
    }
}
