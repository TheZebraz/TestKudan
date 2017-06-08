package elatesoftware.com.testkudan;

import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;

import eu.kudan.kudan.ARAPIKey;
import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARRenderer;
import eu.kudan.kudan.ARTexture2D;

import static elatesoftware.com.testkudan.MainActivity.STATE.PLACEMENT;
import static elatesoftware.com.testkudan.MainActivity.STATE.TRACKING;

public class MainActivity extends ARActivity  implements GestureDetector.OnGestureListener{

    private static final String KUDAN_KEY = "0MqjXUcJfNaDBhmnLvNYAXdMxJmkFqiH1fe2g1dGHIK9SMCSkvmbs2kXE5U0cvBiwdrUjOPRYx66kIb/V9R1WY3y1gL+uval1cv93E/LUwX9ZgDGrkRwkAzby5kS948znUJUU6imKJFaJOg9KUeGHHqxwxPXJs2ShTHDww6h+kCXsNpGPPf+QwDz5MKyK+Oi0paK5J1lzs4UHUvYJQmwHVSADps8O7tMMnwY2ZfXeIbZwY+UPa2fLp2/qOXBTktthOdQMnBTSS3eXwLStxSL9OSdmEbGuitIFn38+mTMk/uzBCvgc1eWGW1yLd4XpR0Jhn3Ha53njuyvDq246VfBC04PPZdWp31YrHO34GhOFVR1PoyXlGIyd+tNR6PIWSDEiksDsDsC/uQsQ1f8gKcLsGRGPcuHwXfUP0LY0rtbKJG5ZbEQ5tNVkPp21q0hybk1P8LnfZ1OdgBbqvHe+4HDOusKEhKiXXOtwS+80BKaPAeKEXbTUQtBMp/pIKmpYugn3pGiO+oBrbTmEXAAbttFSIGguWysCb/knZ7eaBIx6Bk08HF0jpeUSaoNOYRHi5oPifG+tqWdGhbinPXokWEfv+kE8s1VKeE85W5cLq6DzGo+nJX7ukF/08Bu0zwbyCbHGRAU4s7YZ2RM1KmXB86nXeGmmktqh/8HbOTwhg5Hm88=";

    private Button mButton;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetectorCompat mScrollDetector;

    private ARModelNode mCurrentModel;
    private ARImageNode mTargetModel;

    private STATE mCurrentState;

    enum STATE {
        PLACEMENT,
        TRACKING
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artest);
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey(KUDAN_KEY);

        mButton = (Button) findViewById(R.id.lockButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
                if(mCurrentState.equals(PLACEMENT)) {
                    arbiTrack.start();
                    arbiTrack.getTargetNode().setVisible(false);
                    mButton.setText("Stop tracking");
                    mCurrentState = TRACKING;
                }
                else {
                    arbiTrack.stop();
                    arbiTrack.getTargetNode().setVisible(true);
                    mButton.setText("Start tracking");
                    mCurrentState = PLACEMENT;
                }
            }
        });
    }

    public void setup() {
        //setupHouse();
        setupNoMarker();
    }

    private void setupNoMarker() {
        mCurrentState = PLACEMENT;
        setupModel();
        setupArbiTrack();
        mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());
        mScrollDetector = new GestureDetectorCompat(this, this);
    }

    private void setupModel() {
        mCurrentModel = getHouseModel();
    }

//    private void setupHouse() {
//        ARImageTrackable trackable = new ARImageTrackable("QR");
//        trackable.loadFromAsset("marker.png");
//        ARImageTracker trackableManager = ARImageTracker.getInstance();
//        trackableManager.addTrackable(trackable);
//
//        mCurrentModel = getHouseModel();
//        trackable.getWorld().addChild(mCurrentModel);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mScaleDetector.onTouchEvent(ev);
        mScrollDetector.onTouchEvent(ev);
        return true;
    }

    public void setupArbiTrack() {

        // Create an image node to be used as a target node
        mTargetModel = new ARImageNode("target.png");

        // Scale and rotate the image to the correct transformation.
        mTargetModel.scaleByUniform(0.3f);
        mTargetModel.rotateByDegrees(90, 1, 0, 0);

        // Initialise gyro placement. Gyro placement positions content on a virtual floor plane where the device is aiming.
        ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
        gyroPlaceManager.initialise();

        // Add target node to gyro place manager
        gyroPlaceManager.getWorld().addChild(mTargetModel);

        // Initialise the arbiTracker
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
        arbiTrack.initialise();

        // Set the arbiTracker target node to the node moved by the user.
        arbiTrack.setTargetNode(mTargetModel);

        // Add model node to world
        arbiTrack.getWorld().addChild(mCurrentModel);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            synchronized (ARRenderer.getInstance()) {
                mCurrentModel.setScale(mCurrentModel.getScale().mult(scaleFactor));
            }
            return true;
        }
    }

    private void panGesture(float distanceX, float distanceY) {
        if (mCurrentState == TRACKING) {
            synchronized (ARRenderer.getInstance()) {
                mCurrentModel.rotateByDegrees(distanceX, 0.0f, 1.0f, 0.0f);
            }
        }
//        } else if (mCurrentState == PLACEMENT) {
//            Vector3f position = this.mCurrentModel.getPosition();
//            synchronized (ARRenderer.getInstance()) {
//                mCurrentModel.setPosition(position.getX(), position.getY() + distanceY, position.getZ());
//                mCurrentModel.setPosition(position.getX(), position.getY(), position.getZ() + distanceX);
//                mTargetModel.setPosition(position.getX(), position.getY() + distanceY, position.getZ());
//                mTargetModel.setPosition(position.getX(), position.getY(), position.getZ() + distanceX);
//            }
//        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        panGesture(distanceX,distanceY);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
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
        return modelNode;
    }
}