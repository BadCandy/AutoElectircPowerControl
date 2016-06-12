package org.androidtown.ictttapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.androidtown.ictttapplication.fragments.ExampleFragment;
import org.androidtown.ictttapplication.fragments.FragmentAdapter;
import org.androidtown.ictttapplication.fragments.IFragmentListener;
import org.androidtown.ictttapplication.service.BTCTemplateService;
import org.androidtown.ictttapplication.utils.AppSettings;
import org.androidtown.ictttapplication.utils.Constants;
import org.androidtown.ictttapplication.utils.Logs;
import org.androidtown.ictttapplication.utils.RecycleUtils;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements IFragmentListener, NavigationView.OnNavigationItemSelectedListener, BeaconConsumer {

    //Beacon
    //static String TAG = "BeaconsEverywhere";
    public BeaconManager beaconManager;
    // Debugging
    private static final String TAG = "RetroWatchActivity";
    // Context, System
    private Context mContext;

    private BTCTemplateService mService;
    private ActivityHandler mActivityHandler;

    // Global


    // UI stuff
    private FragmentManager mFragmentManager;
    private FragmentAdapter mSectionsPagerAdapter;

    // Refresh timer
    private Timer mRefreshTimer = null;
    public static String auto_setting_saved_state = "00"; //
    public static double usage_saved = 0;

    /*****************************************************
     * Overrided methods
     ******************************************************/

    @Override
    public synchronized void onStart() {
        super.onStart();
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        // Stop the timer
        if (mRefreshTimer != null) {
            mRefreshTimer.cancel();
            mRefreshTimer = null;
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
        finalizeActivity();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // onDestroy is not always called when applications are finished by Android system.
        finalizeActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item1) {
        Intent intent;
        switch (item1.getItemId()) {

            case R.id.action_scan:
                // Launch the DeviceListActivity to see devices and do scan
                doScan();
                return true;
            case R.id.action_discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            case R.id.management:
                intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
                finish();
                break;
            case R.id.calculator://네비게이션 뷰에서 전기요금계산기 눌러질 시 계산화면
                intent = new Intent(getApplicationContext(), Calculator.class);
                startActivity(intent);
                break;
            case R.id.query: // 문의 처 띄울 화면
                intent = new Intent(getApplicationContext(), qna.class);
                startActivity(intent);
                break;
        }
        return false;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // This prevents reload after configuration changes
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Implements TabListener
     *
     * @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
     * // When the given tab is selected, switch to the corresponding page in the ViewPager.
     * mViewPager.setCurrentItem(tab.getPosition());
     * }
     * @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
     * }
     * @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
     * }
     */
    @Override
    public void OnFragmentCallback(int msgType, int arg0, int arg1, String arg2, String
            arg3, Object arg4) {
        switch (msgType) {
            case IFragmentListener.CALLBACK_RUN_IN_BACKGROUND:
                if (mService != null)
                    mService.startServiceMonitoring();
                break;
            case IFragmentListener.CALLBACK_SEND_MESSAGE:
                if (mService != null && arg2 != null)
                    mService.sendMessageToRemote(arg2);

            default:
                break;
        }
    }


    /*****************************************************
     *	Private methods
     ******************************************************/

    /**
     * Service connection
     */
    private ServiceConnection mServiceConn = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, "Activity - Service connected");

            mService = ((BTCTemplateService.ServiceBinder) binder).getService();

            // Activity couldn't work with mService until connections are made
            // So initialize parameters and settings here. Do not initialize while running onCreate()
            initialize();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    /**
     * Start service if it's not running
     */

    private void doStartService() {
        Log.d(TAG, "# Activity - doStartService()");
        startService(new Intent(this, BTCTemplateService.class));
        bindService(new Intent(this, BTCTemplateService.class), mServiceConn, Context.BIND_AUTO_CREATE);
    }

    /**
     * Stop the service
     */
    private void doStopService() {
        Log.d(TAG, "# Activity - doStopService()");
        mService.finalizeService();
        stopService(new Intent(this, BTCTemplateService.class));
    }

    /**
     * Initialization / Finalization
     */
    private void initialize() {
        Logs.d(TAG, "# Activity - initialize()");
        mService.setupService(mActivityHandler);

        // If BT is not on, request that it be enabled.
        // RetroWatchService.setupBT() will then be called during onActivityResult
        if (!mService.isBluetoothEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }

        // Load activity reports and display
        if (mRefreshTimer != null) {
            mRefreshTimer.cancel();
        }

        // Use below timer if you want scheduled job
        //mRefreshTimer = new Timer();
        //mRefreshTimer.schedule(new RefreshTimerTask(), 5*1000);
    }

    private void finalizeActivity() {
        Logs.d(TAG, "# Activity - finalizeActivity()");

        if (!AppSettings.getBgService()) {
            doStopService();
        } else {
        }

        // Clean used resources
        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
    }

    /**
     * Launch the DeviceListActivity to see devices and do scan
     */
    private void doScan() {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CONNECT_DEVICE);
    }

    /**
     * Ensure this device is discoverable by others
     */
    private void ensureDiscoverable() {
        if (mService.getBluetoothScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);
        }
    }


    /*****************************************************
     * Public classes
     ******************************************************/

    //전달된 인탠트로 디바이스와 연결
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logs.d(TAG, "onActivityResult " + resultCode);
        //mservice.

        switch (requestCode) {
            case Constants.REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS); //전달된 MAC 주소 확인하고 변수에 저장
                    if (address != null && mService != null)
                        mService.connectDevice(address); // 해당 MAC 주소를 통해 연결을 위한 메소드 호출
                }
                break;

            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a BT session
                    mService.setupBT();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Logs.e(TAG, "BT is not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                }
                break;
        }    // End of switch(requestCode)
    }


    /*****************************************************
     * Handler, Callback, Sub-classes
     ******************************************************/

    public class ActivityHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                // Receives BT state messages from service
                // and updates BT state UI
                case Constants.MESSAGE_BT_STATE_INITIALIZED:

                    break;
                case Constants.MESSAGE_BT_STATE_LISTENING:

                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTING:

                    break;
                case Constants.MESSAGE_BT_STATE_CONNECTED:
                    if (mService != null) {
                        String deviceName = mService.getDeviceName();
                        if (deviceName != null) {
                            MyRunnableThread send_thread = new MyRunnableThread(msg);
                            Thread thread = new Thread(send_thread);
                            thread.setDaemon(true);
                            thread.start();
                        }

                    }
                    //msg.what = 201;
                    break;
                case Constants.MESSAGE_BT_STATE_ERROR:

                    break;

                // BT Command status
                case Constants.MESSAGE_CMD_ERROR_NOT_CONNECTED:

                    break;

                ///////////////////////////////////////////////
                // When there's incoming packets on bluetooth
                // do the UI works like below
                ///////////////////////////////////////////////
                case Constants.MESSAGE_READ_CHAT_DATA:
                    if (msg.obj != null) {
                        //Log.d("AAAAAAAAA ", msg.toString());
                        ExampleFragment frg = (ExampleFragment) mSectionsPagerAdapter.getItem(FragmentAdapter.FRAGMENT_POS_EXAMPLE);
                        frg.showMessage((String) msg.obj);
                        if (msg.obj.toString().charAt(0) != 'A') {

                                Log.d("RCV TIME :   ", msg.obj.toString());
                                Log.d("dddddddddddd :   ", auto_setting_saved_state);
                                usage = Double.parseDouble(msg.obj.toString()) / 3600;
                        }
                    }
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
            usage_saved = usage;
        }

    }    // End of class ActivityHandler

    public class MyRunnableThread implements Runnable {
        Message msg;

        MyRunnableThread(Message msg) {
            this.msg = msg;
        }

        public void run() {
            ExampleFragment frg = (ExampleFragment) mSectionsPagerAdapter.getItem(FragmentAdapter.FRAGMENT_POS_EXAMPLE);
            frg.showMessage((String) msg.obj);
            // TODO Auto-generated method stub
            while (true) {
                frg.onClick_send();
                try {
                    Thread.sleep(1000); //통신의 속도/
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public class dist_conf_thread implements Runnable {

        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                if (beaconDistance >= userSettingDistance) {
                    saved_state = auto_setting_saved_state;
                    Log.d("BEACON CONF : ", "BEACON IS DEAD!!");
                    //Toast.makeText(MainActivity.this, "비콘에서 벗어났습니다. 어플을 종료 합니다.", Toast.LENGTH_LONG).show();

                    Message msg = null;
                    msg.obj = auto_setting_saved_state;
                    MyRunnableThread send_thread = new MyRunnableThread(msg);
                    Thread thread = new Thread(send_thread);
                    thread.setDaemon(true);
                    thread.start();
                    try {
                        Thread.sleep(1000); // 1초 = 1000밀리초
                    } catch (InterruptedException ignore) {}
                    moveTaskToBack(true);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    Log.d("BEACON CONF : ", "BEACON IS REAL!!");
                    Thread.interrupted();
                }
            }
        }
    }


    /**
     * Auto-refresh Timer
     */
    private class RefreshTimerTask extends TimerTask {
        public RefreshTimerTask() {
        }

        public void run() {
            mActivityHandler.post(new Runnable() {
                public void run() {
                    // TODO:
                    mRefreshTimer = null;
                }
            });
        }
    }


    public static int aim = 200; // 사용자가 설정한 목표
    public static int min = (int) aim / 2; // 사용자가 설정한 목표/2
    public static double usage = 10; // 네트워크에서 받아 올 사용량
    public static int price = 607;
    public static String uuid = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    public static int userSettingDistance = 2;
    public static double beaconDistance = 5000;

    public static String saved_state = "77";  //★★★★★★★★★★★★★//
    /*
    saved_state
    0b000 0 48 - off off off
    0b001 1 49 - off off on
    0b010 2 50 - off on off
    0b011 3 51 - off on on
    0b100 4 52 - on off off
    0b101 5 53 - on off on
    0b110 6 54 - on on off
    0b111 7 55 - on on on
     */

    public void changeface() { // 사용량목표에 따라 얼굴표정 변화시키는 메소드
        ImageView smileface = (ImageView) findViewById(R.id.smileface);
        ImageView sosoface = (ImageView) findViewById(R.id.sosoface);
        ImageView sadface = (ImageView) findViewById(R.id.sadface);

        if (usage <= aim) { // 절약 시 웃는 표정
            smileface.setVisibility(View.VISIBLE);
            sosoface.setVisibility(View.INVISIBLE);
            sadface.setVisibility(View.INVISIBLE);
        } else if (aim < usage && usage < aim * 2) { // 목표 초과 시 무표정
            smileface.setVisibility(View.INVISIBLE);
            sosoface.setVisibility(View.VISIBLE);
            sadface.setVisibility(View.INVISIBLE);
        } else {//목표 두배 초과 시 우는 표정
            smileface.setVisibility(View.INVISIBLE);
            sosoface.setVisibility(View.INVISIBLE);
            sadface.setVisibility(View.VISIBLE);
        }
    }

    public void calculatePrice() { // 사용량에 따라 요금계산해주는 메소드

        if (usage < 100)
            price = (int) (60.7 * usage);
        else if (usage >= 100 && usage <= 200)
            price = (int) (125.9 * usage);
        else if (usage > 200 && usage <= 300)
            price = (int) (187.9 * usage);
        else if (usage > 300 && usage <= 400)
            price = (int) (280.6 * usage);
        else if (usage > 400 && usage <= 500)
            price = (int) (417.7 * usage);
        else
            price = (int) (709.5 * usage);
        TextView priceview = (TextView) findViewById(R.id.priceview);
        priceview.setText(Integer.toString(price));
    }

    public void refreshusage() { // 사용량에 따라 요금계산해주는 메소드
        TextView usageview = (TextView) findViewById(R.id.usageview);
        String result = String.format("%.3f", usage_saved);

        usageview.setText(result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);//왼쪽에서네비게이션뷰
        navigationView.setNavigationItemSelectedListener(this);
        //블루투스


        //----- System, Context
        mContext = this;    //.getApplicationContext();
        mActivityHandler = new ActivityHandler();
        AppSettings.initializeAppSettings(mContext);

        // Create the adapter that will return a fragment for each of the primary sections of the app.
        mFragmentManager = getSupportFragmentManager();
        mSectionsPagerAdapter = new FragmentAdapter(mFragmentManager, mContext, this, mActivityHandler);

        // Do data initialization after service started and binded
        doStartService();


        //블루투스


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        ImageButton c = (ImageButton) findViewById(R.id.controlButton);//누를 시 제어 화면으로 이동
        c.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {

                                     if (userSettingDistance >= beaconDistance) {

                                         Intent intent = new Intent(getApplicationContext(),controlActivity.class);
                                         startActivity(intent);
                                     } else
                                         Toast.makeText(MainActivity.this, "비콘 신호가 없어, 제어가 불가능합니다.", Toast.LENGTH_SHORT).show();

                                 }
                             }

        );

        TextView usageview = (TextView) findViewById(R.id.usageview);
        TextView priceview = (TextView) findViewById(R.id.priceview);

        usageview.setText(Double.toString(usage));

        calculatePrice();

        priceview.setText(Integer.toString(price));

        ImageView smileface = (ImageView) findViewById(R.id.smileface);
        ImageView sosoface = (ImageView) findViewById(R.id.sosoface);
        ImageView sadface = (ImageView) findViewById(R.id.sadface);

        smileface.setVisibility(View.INVISIBLE);
        sosoface.setVisibility(View.INVISIBLE);
        sadface.setVisibility(View.INVISIBLE);

        if (usage <= aim)

        { // 절약 시 웃는 표정
            smileface.setVisibility(View.VISIBLE);
            sosoface.setVisibility(View.INVISIBLE);
            sadface.setVisibility(View.INVISIBLE);
        } else if (aim < usage && usage < aim * 2)

        { // 목표 초과 시 무표정
            smileface.setVisibility(View.INVISIBLE);
            sosoface.setVisibility(View.VISIBLE);
            sadface.setVisibility(View.INVISIBLE);
        } else

        {//목표 두배 초과 시 우는 표정
            smileface.setVisibility(View.INVISIBLE);
            sosoface.setVisibility(View.INVISIBLE);
            sadface.setVisibility(View.VISIBLE);
        }

        ImageButton refreshbtn = (ImageButton) findViewById(R.id.refreshButton);
        refreshbtn.setOnClickListener(new View.OnClickListener()

                                      {//새로고침버튼누를시 표정과 요금 재 계산
                                          @Override
                                          public void onClick(View view) {

                                              changeface();
                                              calculatePrice();
                                              refreshusage();
                                          }
                                      }

        );

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);




    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) { // 네비게이션의 아이템
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.management) {//네비게
            // 이션 뷰에서 설정 눌러질 시 설정 화면으로
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
        } else if (id == R.id.calculator) {//네비게이션 뷰에서 전기요금계산기 눌러질 시 계산화면
            Intent intent = new Intent(getApplicationContext(), Calculator.class);
            startActivity(intent);
        } else if (id == R.id.query) { // 문의 처 띄울 화면
            Intent intent = new Intent(getApplicationContext(), qna.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("myBeacons", Identifier.parse(uuid), null, null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {

                try {
                    Log.d(TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "didExitRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                }

            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon : beacons) {
                    beaconDistance = beacon.getDistance();
                    if (beaconDistance >= userSettingDistance) {
                        Log.d("BEACON CONF : ", "BEACON OUT REGION");
                        saved_state = auto_setting_saved_state;
                        finish(saved_state);
                    } else {
                        Log.d("BEACON CONF : ", "BEACON IN REGION");
                        restart(saved_state);
                    }
                }
            }
        });
        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
        }
    }

    private final long	FINSH_INTERVAL_TIME    = 2000;
    private long		backPressedTime        = 0;
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;

            Toast.makeText(getApplicationContext(), "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

        }
    }

}
