package elatesoftware.com.testkudan;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import eu.kudan.kudan.ARAPIKey;
import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTrackableListener;
import eu.kudan.kudan.ARImageTracker;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;
import eu.kudan.kudan.ARVideoNode;
import eu.kudan.kudan.ARVideoTexture;

public class ARTestActivity extends ARActivity implements ARImageTrackableListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey("0MqjXUcJfNaDBhmnLvNYAXdMxJmkFqiH1fe2g1dGHIK9SMCSkvmbs2kXE5U0cvBiwdrUjOPRYx66kIb/V9R1WY3y1gL+uval1cv93E/LUwX9ZgDGrkRwkAzby5kS948znUJUU6imKJFaJOg9KUeGHHqxwxPXJs2ShTHDww6h+kCXsNpGPPf+QwDz5MKyK+Oi0paK5J1lzs4UHUvYJQmwHVSADps8O7tMMnwY2ZfXeIbZwY+UPa2fLp2/qOXBTktthOdQMnBTSS3eXwLStxSL9OSdmEbGuitIFn38+mTMk/uzBCvgc1eWGW1yLd4XpR0Jhn3Ha53njuyvDq246VfBC04PPZdWp31YrHO34GhOFVR1PoyXlGIyd+tNR6PIWSDEiksDsDsC/uQsQ1f8gKcLsGRGPcuHwXfUP0LY0rtbKJG5ZbEQ5tNVkPp21q0hybk1P8LnfZ1OdgBbqvHe+4HDOusKEhKiXXOtwS+80BKaPAeKEXbTUQtBMp/pIKmpYugn3pGiO+oBrbTmEXAAbttFSIGguWysCb/knZ7eaBIx6Bk08HF0jpeUSaoNOYRHi5oPifG+tqWdGhbinPXokWEfv+kE8s1VKeE85W5cLq6DzGo+nJX7ukF/08Bu0zwbyCbHGRAU4s7YZ2RM1KmXB86nXeGmmktqh/8HbOTwhg5Hm88=");
    }

    public void setup() {
        // create a trackable from a bundled image.
        ARImageTrackable trackable = new ARImageTrackable("QR");
        trackable.loadFromAsset("marker.png");

// Get instance of image tracker manager
        ARImageTracker trackableManager = ARImageTracker.getInstance();

// Add image trackable to image tracker manager
        trackableManager.addTrackable(trackable);
        //ARImageNode imageNode = new ARImageNode("house.png");

        ARModelImporter modelImporter = new ARModelImporter();
        modelImporter.loadFromAsset("house2.armodel");
        ARModelNode modelNode = (ARModelNode)modelImporter.getNode();

// Load model texture
        ARTexture2D texture2D = new ARTexture2D();
        texture2D.loadFromAsset("house2.jpg");

// Apply model texture file to model texture material and add ambient lighting
        ARLightMaterial material = new ARLightMaterial();
        material.setTexture(texture2D);
        material.setAmbient(0.8f, 0.8f, 0.8f);

// Apply texture material to models mesh nodes
        for (ARMeshNode meshNode : modelImporter.getMeshNodes()){
            meshNode.setMaterial(material);
        }

       // modelNode.setScale(10,10,10);
// Add image node to image trackable
       // ARGyroPlaceManager arGyroPlaceManager
        trackable.getWorld().addChild(modelNode);

        modelNode.setOrientation(0,1,1,1);
    }

    @Override
    public void didDetect(ARImageTrackable trackable) {
        Log.i("+++++++++++++++++++++++", "didDetect tracked");

    }


    @Override
    public void didTrack(ARImageTrackable trackable) {
        Log.i("+++++++++++++++++++++++", "didTracktracked");
    }

    @Override
    public void didLose(ARImageTrackable trackable) {
        Log.i("+++++++++++++++++++++++", "lost " + trackable.getName());

    }

}