package elatesoftware.com.testkudan;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import eu.kudan.kudan.ARAPIKey;
import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARArbiTrack;
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

    private ARBITRACK_STATE arbitrack_state;

    //Tracking enum
    enum ARBITRACK_STATE {
        ARBI_PLACEMENT,
        ARBI_TRACKING
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artest);
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey("0MqjXUcJfNaDBhmnLvNYAXdMxJmkFqiH1fe2g1dGHIK9SMCSkvmbs2kXE5U0cvBiwdrUjOPRYx66kIb/V9R1WY3y1gL+uval1cv93E/LUwX9ZgDGrkRwkAzby5kS948znUJUU6imKJFaJOg9KUeGHHqxwxPXJs2ShTHDww6h+kCXsNpGPPf+QwDz5MKyK+Oi0paK5J1lzs4UHUvYJQmwHVSADps8O7tMMnwY2ZfXeIbZwY+UPa2fLp2/qOXBTktthOdQMnBTSS3eXwLStxSL9OSdmEbGuitIFn38+mTMk/uzBCvgc1eWGW1yLd4XpR0Jhn3Ha53njuyvDq246VfBC04PPZdWp31YrHO34GhOFVR1PoyXlGIyd+tNR6PIWSDEiksDsDsC/uQsQ1f8gKcLsGRGPcuHwXfUP0LY0rtbKJG5ZbEQ5tNVkPp21q0hybk1P8LnfZ1OdgBbqvHe+4HDOusKEhKiXXOtwS+80BKaPAeKEXbTUQtBMp/pIKmpYugn3pGiO+oBrbTmEXAAbttFSIGguWysCb/knZ7eaBIx6Bk08HF0jpeUSaoNOYRHi5oPifG+tqWdGhbinPXokWEfv+kE8s1VKeE85W5cLq6DzGo+nJX7ukF/08Bu0zwbyCbHGRAU4s7YZ2RM1KmXB86nXeGmmktqh/8HbOTwhg5Hm88=");

        arbitrack_state  = ARBITRACK_STATE.ARBI_PLACEMENT;
    }

    public void setup() {
        //setupHouse();
        setupNoMarker();
    }

    private void setupNoMarker() {
        setupArbiTrack();
//        ARGyroPlaceManager arGyroPlaceManager = ARGyroPlaceManager.getInstance();
//        arGyroPlaceManager.initialise();
//        arGyroPlaceManager.getWorld().addChild(getHouseModel());
    }

    private void setupHouse() {
        ARImageTrackable trackable = new ARImageTrackable("QR");
        trackable.loadFromAsset("marker.png");
        ARImageTracker trackableManager = ARImageTracker.getInstance();
        trackableManager.addTrackable(trackable);

        ARModelNode modelNode = getHouseModel();
        trackable.getWorld().addChild(modelNode);
    }

    //Sets up arbi track
    public void setupArbiTrack() {

        // Create an image node to be used as a target node
        ARImageNode targetImageNode = new ARImageNode("target.png");

        // Scale and rotate the image to the correct transformation.
        targetImageNode.scaleByUniform(0.3f);
        targetImageNode.rotateByDegrees(90, 1, 0, 0);

        // Initialise gyro placement. Gyro placement positions content on a virtual floor plane where the device is aiming.
        ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
        gyroPlaceManager.initialise();

        // Add target node to gyro place manager
        gyroPlaceManager.getWorld().addChild(targetImageNode);

        // Initialise the arbiTracker
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
        arbiTrack.initialise();

        // Set the arbiTracker target node to the node moved by the user.
        arbiTrack.setTargetNode(targetImageNode);

        // Add model node to world
        arbiTrack.getWorld().addChild(getHouseModel());

    }

    public void lockPosition(View view) {

        Button b = (Button)findViewById(R.id.lockButton);
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();

        // If in placement mode start arbi track, hide target node and alter label
        if(arbitrack_state.equals(ARBITRACK_STATE.ARBI_PLACEMENT)) {

            //Start Arbi Track
            arbiTrack.start();

            //Hide target node
            arbiTrack.getTargetNode().setVisible(false);
            arbiTrack.getWorld();
            //Change enum and label to reflect Arbi Track state
            arbitrack_state = ARBITRACK_STATE.ARBI_TRACKING;
            b.setText("Stop Tracking");
        }

        // If tracking stop tracking, show target node and alter label
        else {

            // Stop Arbi Track
            arbiTrack.stop();


            // Display target node
            arbiTrack.getTargetNode().setVisible(true);

            //Change enum and label to reflect Arbi Track state
            arbitrack_state = ARBITRACK_STATE.ARBI_PLACEMENT;
            b.setText("Start Tracking");

        }

    }

    @NonNull
    private ARModelNode getHouseModel() {
        ARModelImporter modelImporter = new ARModelImporter();
        modelImporter.loadFromAsset("house2.armodel");

        ARModelNode modelNode = modelImporter.getNode();

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

        modelNode.setScale(0.5f, 0.5f, 0.5f);
        modelNode.setOrientation(1,0,0, 1);
        return modelNode;
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