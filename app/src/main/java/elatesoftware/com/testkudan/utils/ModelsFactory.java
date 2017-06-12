package elatesoftware.com.testkudan.utils;

import android.support.annotation.NonNull;

import java.io.File;

import elatesoftware.com.testkudan.model.Model;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;

public class ModelsFactory {

    public static final String MODELS_PATH = "models";

    @NonNull
    public static ARModelNode getModel(Model model) {
        ARModelImporter modelImporter = new ARModelImporter();

        String modelsFilesPAth = MODELS_PATH + File.separator + model.getName() + File.separator;
        modelImporter.loadFromAsset(modelsFilesPAth + model.getModelPath());

        ARModelNode modelNode = modelImporter.getNode();

        // Load model texture
        ARTexture2D texture2D = new ARTexture2D();
        texture2D.loadFromAsset(modelsFilesPAth + model.getTexturePath());

        // Apply model texture file to model texture material and add ambient lighting
        ARLightMaterial material = new ARLightMaterial();
        material.setTexture(texture2D);
        material.setAmbient(0.8f, 0.8f, 0.8f);

        // Apply texture material to models mesh nodes
        for (ARMeshNode meshNode : modelImporter.getMeshNodes()){
            meshNode.setMaterial(material);
        }
        return modelNode;
    }

}
