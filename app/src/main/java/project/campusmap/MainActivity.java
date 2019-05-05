package project.campusmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;


import static project.campusmap.CampusSearchManager.searchByName;

public class MainActivity extends Activity implements CameraListener, DrivingSession.DrivingRouteListener{

    private final String MAPKIT_API_KEY = "3fae3855-a5f8-4897-8e5f-fbcf862fc33e";

    private MapView mapView;
    private TextView mistakeText;
    private EditText searchEdit, fromEdit, toEdit;
    private ImageButton plusButton, minusButton, rountingButton;
    private Button okButton, backButton;
    private ListView suggestResultView, fromSuggestResultView, toSuggestResultView;
    private ArrayAdapter resultAdapter, fromResultAdapter, toResultAdapter;
    private List<String> suggestResult, fromSuggestResult, toSuggestResult;
    private CampusSearchManager campusSearchManager = new CampusSearchManager();
    private SlidingUpPanelLayout slidingPaneLayout;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;

    private void search(String query) {
        List<Building> campusSearch = CampusSearchManager.searchByName(query);
        if (campusSearch.isEmpty()) {

        } else {
            suggestResultView.setVisibility(View.INVISIBLE);
            searchCampus(campusSearch.get(0));
            moveCamera(campusSearch.get(0).location);
        }
    }

    private void searchCampus(Building building) {
        mapObjects.clear();
        mapObjects.addPlacemark(building.location,
                ImageProvider.fromResource(this, R.drawable.search_result));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);
        DirectionsFactory.initialize(this);


        setContentView(R.layout.activity_map);
        super.onCreate(savedInstanceState);

        mistakeText = (TextView) findViewById(R.id.mistake_view);
        searchEdit = (EditText) findViewById(R.id.search_edit);
        fromEdit = (EditText) findViewById(R.id.from_edit);
        toEdit = (EditText) findViewById(R.id.to_edit);
        mapView = (MapView) findViewById(R.id.mapview);
        plusButton = (ImageButton) findViewById(R.id.plus);
        minusButton = (ImageButton) findViewById(R.id.minus);
        rountingButton = (ImageButton) findViewById(R.id.routing);
        okButton = (Button) findViewById(R.id.ok_button);
        backButton = (Button) findViewById(R.id.back_button);

        suggestResultView = (ListView) findViewById(R.id.suggest_result);
        fromSuggestResultView = (ListView) findViewById(R.id.from_suggest_result);
        toSuggestResultView = (ListView) findViewById(R.id.to_suggest_result);
        slidingPaneLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPaneLayout.setVisibility(View.INVISIBLE);
        slidingPaneLayout.setTouchEnabled(false);


        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapView.getMap().getMapObjects().addCollection();

        initListeners();

        suggestResult = new ArrayList<>();
        fromSuggestResult = new ArrayList<>();
        toSuggestResult = new ArrayList<>();
        initListeners();
        resultAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                suggestResult);
        toResultAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                toSuggestResult);
        fromResultAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                fromSuggestResult);
        suggestResultView.setAdapter(resultAdapter);
        toSuggestResultView.setAdapter(toResultAdapter);
        fromSuggestResultView.setAdapter(fromResultAdapter);

        initListeners();

        mapView.getMap().move(new CameraPosition(new Point(60.006174, 30.372777), 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 5), null);
    }

    private long lastAction = 0L;
    private static final long REPEAT_INTERVAL = 5L;

    private void hideKey(View view ) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initListeners() {
        mapView.getMap().addCameraListener(this);
        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(searchEdit.getText().toString());
                    suggestResultView.setVisibility(View.INVISIBLE);
                    hideKey(searchEdit);
                }
                return false;
            }
        });

        fromEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onEditEnd(fromEdit, fromSuggestResultView, fromSuggestResult, fromResultAdapter);
                }
                return false;
            }
        });


        toEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onEditEnd(toEdit, toSuggestResultView, toSuggestResult, toResultAdapter);
                }
                return false;
            }
        });



        plusButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long currTime = SystemClock.uptimeMillis();

                CameraPosition curPosition = mapView.getMap().getCameraPosition();
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        lastAction = currTime;
                        mapView.getMap().move(new CameraPosition(curPosition.getTarget(),
                                curPosition.getZoom() + 0.7f, curPosition.getAzimuth(),
                                curPosition.getTilt()), new Animation(Animation.Type.SMOOTH, 0.4f), null);
                        break;
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        if (currTime - lastAction >= REPEAT_INTERVAL) {
                            lastAction = currTime;

                            mapView.getMap().move(new CameraPosition(curPosition.getTarget(),
                                    curPosition.getZoom() + 0.7f, curPosition.getAzimuth(),
                                    curPosition.getTilt()), new Animation(Animation.Type.SMOOTH, 0.1f), null);
                        }
                        break;
                }
                return true;
            }
        });


        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraPosition curPosition = mapView.getMap().getCameraPosition();
                mapView.getMap().move(new CameraPosition(curPosition.getTarget(),
                                curPosition.getZoom() - 0.7f, curPosition.getAzimuth(), curPosition.getTilt()),
                        new Animation(Animation.Type.SMOOTH, 0.4f), null);
            }
        });

        rountingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingPaneLayout.setVisibility(View.VISIBLE);
                slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                toEdit.setText("");
                fromEdit.setText("");
                mistakeText.setText("");

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSlidePanel();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkClick();
            }
        });

        if (suggestResultView != null)
            suggestResultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    searchEdit.clearFocus();
                    searchEdit.setText(suggestResult.get(position));
                    search(suggestResult.get(position));
                    hideKey(searchEdit);
                }
            });
        if (toSuggestResultView != null)
            toSuggestResultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    toEdit.clearFocus();
                    toEdit.setText(toSuggestResult.get(position));
                    onEditEnd(toEdit, toSuggestResultView, toSuggestResult, toResultAdapter);
                }
            });
        if (fromSuggestResultView != null)
            fromSuggestResultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    fromEdit.clearFocus();
                    fromEdit.setText(fromSuggestResult.get(position));
                    onEditEnd(fromEdit, fromSuggestResultView, fromSuggestResult, fromResultAdapter);
                }
            });
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchEdit.isFocused()) {
                    requstSuggestCampus(editable.toString(), suggestResult, resultAdapter, suggestResultView);
                }
            }
        });
        fromEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (fromEdit.isFocused()) {
                    requstSuggestCampus(editable.toString(), fromSuggestResult, fromResultAdapter, fromSuggestResultView);
                }
            }
        });
        toEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (toEdit.isFocused()) {
                    requstSuggestCampus(editable.toString(), toSuggestResult, toResultAdapter, toSuggestResultView);
                }
            }
        });
    }

    private void onOkClick() {
        Building from = CampusSearchManager.searchExactlyByName(fromEdit.getText().toString());
        Building to = CampusSearchManager.searchExactlyByName(toEdit.getText().toString());
        if (from == null || to == null) {
            mistakeText.setText("Некорректные данные, повторите ввод");
        } else {
            closeSlidePanel();
            submitDrivingRequest(from.location, to.location);
        }

    }

    private void closeSlidePanel() {
        slidingPaneLayout.setAnchorPoint(1.0f);
        slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        slidingPaneLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    public void onCameraPositionChanged(Map map, CameraPosition cameraPosition, CameraUpdateSource cameraUpdateSource, boolean finished) {

    }

    private void requstSuggestCampus(String query, List<String> suggestResult,
                                     ArrayAdapter resultAdapter, ListView suggestResultView) {
        suggestResult.clear();
        List<Building> searchResult = searchByName(query);
        for (int i = 0; i < Math.min(5, searchResult.size()); i++) {
            suggestResult.add(searchResult.get(i).name);
        }
        resultAdapter.notifyDataSetChanged();
        suggestResultView.setVisibility(View.VISIBLE);
    }

    private void moveCamera(Point position) {
        mapView.getMap().move(new CameraPosition(position, 13.5f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 3), null);
    }


    private void moveCamera(Point pos1, Point pos2) {
        mapView.getMap().move(new CameraPosition(new Point((pos1.getLatitude() + pos2.getLatitude())/2, (pos1.getLongitude() + pos2.getLongitude())/2), 13.5f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 3), null);
    }

    private void onEditEnd(EditText editText, ListView suggestView, List<String> suggestResult, ArrayAdapter resultAdapter) {
        hideKey(editText);
        suggestResult.clear();
        resultAdapter.notifyDataSetChanged();
        suggestView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDrivingRoutes(List<DrivingRoute> routes) {
        mapObjects.addPolyline(routes.get(0).getGeometry());
    }

    @Override
    public void onDrivingRoutesError(Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void submitDrivingRequest(Point from, Point to) {
        DrivingOptions options = new DrivingOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(from, RequestPointType.WAYPOINT, null));
        requestPoints.add(new RequestPoint(to, RequestPointType.WAYPOINT, null));
        drawFromAndToPoints(from, to);
        drivingSession = drivingRouter.requestRoutes(requestPoints, options, this);
        moveCamera(from, to);
    }

    private void drawFromAndToPoints(Point from, Point to) {
        mapObjects.clear();
        mapObjects.addPlacemark(from, ImageProvider.fromResource(this, R.drawable.search_result));
        mapObjects.addPlacemark(to, ImageProvider.fromResource(this, R.drawable.search_result));
    }
}