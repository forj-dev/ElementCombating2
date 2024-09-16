package forj.elementcombating.utils.attribute_creator;

import net.minecraft.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeCreatorProvider {
    public static final AttributeCreatorProvider Instance = new AttributeCreatorProvider();
    private final Map<String, AttributeCreator> creators = new HashMap<>();

    public AttributeCreatorProvider() {
    }

    @SuppressWarnings("unused")
    public AttributeCreatorProvider(String path) {
        try (BufferedReader file = new BufferedReader(new FileReader(path))) {
            load(file);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to read attribute creator file", e);
        }
    }

    public void load(BufferedReader reader){
        try {
            String creatorId = "";
            List<Pair<AttributeCreator.OutputVariable, String>> expressions = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("$")) {
                    if (!creatorId.isEmpty()) {
                        if (creators.containsKey(creatorId))
                            creators.get(creatorId).setExpressions(expressions);
                        else creators.put(creatorId, new AttributeCreator(expressions));
                    }
                    expressions = new ArrayList<>();
                    creatorId = line.substring(1);
                    continue;
                }
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
//                    expressions.add(new Pair<>(parts[0].trim(), parts[1].trim()));
                    String variable = parts[0].trim();
                    if (variable.startsWith("INT ")){
                        variable = variable.substring(4).trim();
                        expressions.add(new Pair<>(
                                new AttributeCreator.OutputVariable(variable, AttributeCreator.OutputVariable.Type.INT),
                                parts[1].trim()
                        ));
                        continue;
                    }
                    if (variable.startsWith("FLOAT ")){
                        variable = variable.substring(6).trim();
                        expressions.add(new Pair<>(
                                new AttributeCreator.OutputVariable(variable, AttributeCreator.OutputVariable.Type.FLOAT),
                                parts[1].trim()
                        ));
                        continue;
                    }
                    if (variable.startsWith("BOOL ")){
                        variable = variable.substring(5).trim();
                        expressions.add(new Pair<>(
                                new AttributeCreator.OutputVariable(variable, AttributeCreator.OutputVariable.Type.BOOL),
                                parts[1].trim()
                        ));
                        continue;
                    }
                    expressions.add(new Pair<>(
                            new AttributeCreator.OutputVariable(variable, AttributeCreator.OutputVariable.Type.AUTO),
                            parts[1].trim()
                    ));
                }
                else throw new IllegalArgumentException("Invalid line in attribute creator file: " + line);
            }
            if (!creatorId.isEmpty()) {
                if (creators.containsKey(creatorId))
                    creators.get(creatorId).setExpressions(expressions);
                else creators.put(creatorId, new AttributeCreator(expressions));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read attribute creator file", e);
        }
    }

    public AttributeCreator getCreator(String creatorId) {
        return creators.get(creatorId);
    }
}
