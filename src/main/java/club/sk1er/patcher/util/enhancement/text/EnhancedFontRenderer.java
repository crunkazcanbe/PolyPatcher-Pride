package club.sk1er.patcher.util.enhancement.text;

import cc.polyfrost.oneconfig.libs.caffeine.cache.Cache;
import cc.polyfrost.oneconfig.libs.caffeine.cache.Caffeine;
import club.sk1er.patcher.util.enhancement.Enhancement;
import club.sk1er.patcher.util.enhancement.hash.StringHash;
import net.minecraft.client.renderer.GLAllocation;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class EnhancedFontRenderer implements Enhancement {

    private static final List<EnhancedFontRenderer> instances = new ArrayList<>();
    // Pride Edition: concurrent collections. Mods measure/draw text off the client thread (search trees, tooltips),
    // and the old HashMap/ArrayList threw ConcurrentModificationException ("fontcme").
    private final Set<StringHash> obfuscated = ConcurrentHashMap.newKeySet();
    private final Map<String, Integer> stringWidthCache = new ConcurrentHashMap<>();
    private final Queue<Integer> glRemoval = new ConcurrentLinkedQueue<>();
    private final Cache<StringHash, CachedString> stringCache = Caffeine.newBuilder()
        .removalListener((key, value, cause) -> {
            if (value == null) return;
            glRemoval.add(((CachedString) value).getListId());
        }).executor(POOL).maximumSize(5000).build();

    public EnhancedFontRenderer() {
        instances.add(this);
    }

    public static List<EnhancedFontRenderer> getInstances() {
        return instances;
    }

    @Override
    public String getName() {
        return "Enhanced Font Renderer";
    }

    @Override
    public void tick() {
        if (obfuscated.isEmpty()) return;
        List<StringHash> drained = new ArrayList<>(obfuscated);
        obfuscated.removeAll(drained);
        stringCache.invalidateAll(drained);
    }

    public int getGlList() {
        final Integer poll = glRemoval.poll();
        return poll == null ? GLAllocation.generateDisplayLists(1) : poll;
    }

    public CachedString get(StringHash key) {
        return stringCache.getIfPresent(key);
    }

    public void cache(StringHash key, CachedString value) {
        stringCache.put(key, value);
    }

    public Map<String, Integer> getStringWidthCache() {
        return stringWidthCache;
    }

    public void invalidateAll() {
        this.stringCache.invalidateAll();
    }

    public Set<StringHash> getObfuscated() {
        return obfuscated;
    }
}
