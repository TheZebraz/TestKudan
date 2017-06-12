package elatesoftware.com.testkudan.model;

public class Model {

    private String mName;
    private String mModelPath;
    private String mTexturePath;

    public Model(String name, String modelPath, String texturePath) {
        mName = name;
        mModelPath = modelPath;
        mTexturePath = texturePath;
    }


    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getModelPath() {
        return mModelPath;
    }

    public void setModelPath(String modelPath) {
        mModelPath = modelPath;
    }

    public String getTexturePath() {
        return mTexturePath;
    }

    public void setTexturePath(String texturePath) {
        mTexturePath = texturePath;
    }
}
