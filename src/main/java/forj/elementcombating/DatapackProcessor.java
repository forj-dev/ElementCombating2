package forj.elementcombating;

import forj.elementcombating.utils.attribute_creator.AttributeCreatorProvider;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatapackProcessor {
    private static final List<Runnable> onReload = new ArrayList<>();

    public static void registerOnReload(Runnable runnable){
        onReload.add(runnable);
    }

    public static void register(){
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return new Identifier("element_combating", "datapack_processor");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        for (Identifier id : manager.findResources("attribute_creators", s -> s.endsWith(".cfg"))) {
                            try (InputStream stream = manager.getResource(id).getInputStream()){
                                InputStreamReader reader = new InputStreamReader(stream);
                                BufferedReader bufferedReader = new BufferedReader(reader);
                                AttributeCreatorProvider.Instance.load(bufferedReader);
                                bufferedReader.close();
                                reader.close();
                            }
                            catch (Exception e){
                                throw new RuntimeException(e);
                            }
                        }
                        for (Runnable runnable : onReload){
                            runnable.run();
                        }
                    }
                }
        );
    }
}
