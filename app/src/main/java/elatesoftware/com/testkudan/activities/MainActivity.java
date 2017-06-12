package elatesoftware.com.testkudan.activities;

import android.content.res.AssetManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import elatesoftware.com.testkudan.R;
import elatesoftware.com.testkudan.model.Model;
import elatesoftware.com.testkudan.utils.ModelsFactory;
import eu.kudan.kudan.ARAPIKey;
import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARRenderer;
import static elatesoftware.com.testkudan.activities.MainActivity.STATE.PLACEMENT;
import static elatesoftware.com.testkudan.activities.MainActivity.STATE.TRACKING;

public class MainActivity extends ARActivity implements GestureDetector.OnGestureListener{

    private static final String KUDAN_KEY = "0MqjXUcJfNaDBhmnLvNYAXdMxJmkFqiH1fe2g1dGHIK9SMCSkvmbs2kXE5U0cvBiwdrUjOPRYx66kIb/V9R1WY3y1gL+uval1cv93E/LUwX9ZgDGrkRwkAzby5kS948znUJUU6imKJFaJOg9KUeGHHqxwxPXJs2ShTHDww6h+kCXsNpGPPf+QwDz5MKyK+Oi0paK5J1lzs4UHUvYJQmwHVSADps8O7tMMnwY2ZfXeIbZwY+UPa2fLp2/qOXBTktthOdQMnBTSS3eXwLStxSL9OSdmEbGuitIFn38+mTMk/uzBCvgc1eWGW1yLd4XpR0Jhn3Ha53njuyvDq246VfBC04PPZdWp31YrHO34GhOFVR1PoyXlGIyd+tNR6PIWSDEiksDsDsC/uQsQ1f8gKcLsGRGPcuHwXfUP0LY0rtbKJG5ZbEQ5tNVkPp21q0hybk1P8LnfZ1OdgBbqvHe+4HDOusKEhKiXXOtwS+80BKaPAeKEXbTUQtBMp/pIKmpYugn3pGiO+oBrbTmEXAAbttFSIGguWysCb/knZ7eaBIx6Bk08HF0jpeUSaoNOYRHi5oPifG+tqWdGhbinPXokWEfv+kE8s1VKeE85W5cLq6DzGo+nJX7ukF/08Bu0zwbyCbHGRAU4s7YZ2RM1KmXB86nXeGmmktqh/8HbOTwhg5Hm88=";
    private static final String TARGET_FILE_NAME = "target.png";

    private Button mButton;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetectorCompat mScrollDetector;

    private ARModelNode mCurrentModel;
    private ARImageNode mTargetModel;
    private int mCurrentModelId;

    private STATE mCurrentState;
    private List<Model> mModels;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    enum STATE {
        PLACEMENT,
        TRACKING
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey(KUDAN_KEY);

        mCurrentModelId = 0;
        setupViews();
    }

    private void setupViews() {
        mButton = (Button) findViewById(R.id.lockButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackButtonCLick();
            }
        });


        View view = findViewById(R.id.framelayout_main_content);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                mScrollDetector.onTouchEvent(event);
                return true;
            }
        });


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        getModels();
        createMenu(mNavigationView);
    }

    private void createMenu(NavigationView navigationView) {
        MenuInflater menuInflater = getMenuInflater();
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < mModels.size(); i++) {
            MenuItem menuItem = menu.add(R.id.models, i, Menu.NONE, mModels.get(i).getName());
            menuItem.setCheckable(true);
            menuItem.setChecked(false);
        }
        menuInflater.inflate(R.menu.drawer_menu, menu);
    }

    private void getModels(){
        mModels = new ArrayList<>();
        try {
            AssetManager assetManager = getAssets();
            String[] modelsFolders = assetManager.list(ModelsFactory.MODELS_PATH);
            for (String modelsFolder : modelsFolders) {
                String[] modelFiles = assetManager.list(ModelsFactory.MODELS_PATH + File.separator + modelsFolder);
                mModels.add(new Model(modelsFolder, modelFiles[0], modelFiles[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectDrawerItem(MenuItem menuItem) {
        mCurrentModelId = menuItem.getItemId();
        update();
        mDrawerLayout.closeDrawers();
    }

    private void trackButtonCLick() {
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
        if(mCurrentState.equals(PLACEMENT)) {
            arbiTrack.start();
            arbiTrack.getTargetNode().setVisible(false);
            mButton.setText(getResources().getText(R.string.track_button_text_stop));
            mCurrentState = TRACKING;
        }
        else {
            arbiTrack.stop();
            arbiTrack.getTargetNode().setVisible(true);
            mButton.setText(getResources().getText(R.string.track_button_text_start));
            mCurrentState = PLACEMENT;
        }
    }

    public void setup() {
        mCurrentState = PLACEMENT;
        setupModel();
        setupArbiTrack();
        mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());
        mScrollDetector = new GestureDetectorCompat(this, this);
    }

    private void update(){
        setupModel();
        ARArbiTrack.getInstance().getWorld().removeAllChildren();
        ARArbiTrack.getInstance().getWorld().addChild(mCurrentModel);
    }

    private void setupModel() {
        mCurrentModel = ModelsFactory.getModel(mModels.get(mCurrentModelId));
    }

    public void setupArbiTrack() {
        mTargetModel = new ARImageNode(TARGET_FILE_NAME);
        mTargetModel.scaleByUniform(0.3f);
        mTargetModel.rotateByDegrees(90, 1, 0, 0);
        ARGyroPlaceManager gyroPlaceManager = ARGyroPlaceManager.getInstance();
        gyroPlaceManager.initialise();
        gyroPlaceManager.getWorld().addChild(mTargetModel);
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
        arbiTrack.initialise();
        arbiTrack.setTargetNode(mTargetModel);
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
}