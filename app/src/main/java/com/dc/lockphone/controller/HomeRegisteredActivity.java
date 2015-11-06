package com.dc.lockphone.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dc.lockphone.R;
import com.lockphone.lockphone.Constants;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Map;

/**
 * Created by dcoellar on 10/2/15.
 */
public class HomeRegisteredActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ListView drawerList;
    public String[] layers;
    private ActionBarDrawerToggle drawerToggle;
    private Map map;
    private LayoutInflater inflater;
    private int[] layersImages;
    private Activity activity;
    private ParseObject parseDevice;
    private String aseguradoraId;
    private ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        inflater = getLayoutInflater();

        setContentView(R.layout.activity_home_registered);

        user = ParseUser.getCurrentUser();

        TelephonyManager mngr = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Devices");
        query.whereEqualTo("user", user);
        query.whereEqualTo("imei", mngr.getDeviceId());
        query.include("brand");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    parseDevice = object;
                    TextView txtBrand = (TextView) findViewById(R.id.brand);
                    txtBrand.setText(object.getParseObject("brand").getString("brand"));
                    TextView txtModel = (TextView) findViewById(R.id.model);
                    txtModel.setText(object.getParseObject("brand").getString("model"));
                    TextView txtIMEI = (TextView) findViewById(R.id.imei);
                    txtIMEI.setText(object.getString("imei"));
                    ImageView img = (ImageView) findViewById(R.id.img);
                    Picasso.with(activity.getBaseContext())
                            .load(object.getParseObject("brand").getString("imageUrl"))
                            .into(img);

                    ParseQuery<ParseObject> queryInsurance = ParseQuery.getQuery("DeviceInsurance");
                    queryInsurance.whereEqualTo("device", object);
                    queryInsurance.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if (e == null && object != null) {
                                TextView txtInsuredValue = (TextView) findViewById(R.id.insured_value);
                                TextView txtDeductible = (TextView) findViewById(R.id.deductible);
                                TextView txtValue = (TextView) findViewById(R.id.recieved_value);
                                TextView txtMontlyCost = (TextView) findViewById(R.id.montly_cost);

                                txtInsuredValue.setText("$ " + String.format("%.2f", object.getDouble("insurance")));
                                txtDeductible.setText("$ " + String.format("%.2f", object.getDouble("depreciation") + object.getDouble("deductible")));
                                txtValue.setText("$ " + String.format("%.2f", object.getDouble("insurance") - (object.getDouble("depreciation") + object.getDouble("deductible"))));
                                txtMontlyCost.setText("$ " + String.format("%.2f", object.getDouble("price")));

                                aseguradoraId = object.getParseObject("aseguradora").getObjectId();
                            } else {
                                Log.e("ERROR", "could not get device");
                                if (e == null) {
                                    Log.e("ERROR", e.getMessage());
                                }
                                //TODO - inform user of issues with parse
                            }
                        }
                    });

                } else {
                    Log.e("ERROR", "could not get device");
                    if (e == null) {
                        Log.e("ERROR", e.getMessage());
                    }
                    //TODO - inform user of issues with parse
                }
            }
        });

        onCreateDrawer();
    }

    protected void onCreateDrawer() {


        TextView userName = (TextView) findViewById(R.id.user_name);
        userName.setText(user.getString("nombre"));
        TextView userEmail = (TextView) findViewById(R.id.user_email);
        userEmail.setText(user.getEmail());

        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout,0,0)
        {
            public void onDrawerClosed(View view)
            {
                getSupportActionBar().setTitle(R.string.app_name);
            }

            public void onDrawerOpened(View drawerView)
            {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        layers = new String[] {"Contrato","Aseguradora","Reclamos","Cerrar Sesion"};
        layersImages = new int[] {R.drawable.ic_attach_file_white_48dp,R.drawable.ic_store_white_48dp,R.drawable.ic_assignment_white_48dp,R.drawable.ic_power_settings_new_white_48dp};
        drawerList = (ListView) findViewById(R.id.menu_list);

        drawerList.setAdapter(new MenuAdapter());

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                switch (pos) {
                    case 0:
                        Intent intent = new Intent(activity, ContratoActivity.class);
                        intent.putExtra("id", aseguradoraId);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intentAseguradora = new Intent(activity, AseguradoraActivity.class);
                        intentAseguradora.putExtra("id", aseguradoraId);
                        startActivity(intentAseguradora);
                        break;
                    case 2:
                        Intent intentReclamo = new Intent(activity, ReclamoActivity.class);
                        startActivity(intentReclamo);
                        break;
                    case 3:
                        if (Constants.IS_PROD) {
                            Logout();
                        }
                        if (!Constants.IS_PROD) {
                            DeleteUserData();
                        }
                        break;
                }
            }
        });
    }

    private void Logout(){
        ParseUser.logOut();
        Intent login = new Intent(activity,LoginActivity.class);
        startActivity(login);
    }

    private void DeleteUserData(){
        deleteDeviceInsurance();
    }

    private void deleteDeviceInsurance(){
        ParseQuery<ParseObject> queryInsurance = ParseQuery.getQuery("DeviceInsurance");
        queryInsurance.whereEqualTo("device", parseDevice);
        queryInsurance.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e("ERROR", "error deleting device insurance");
                                Log.e("ERROR", e.getMessage());
                                showError();
                            } else {
                                deleteDevice();
                            }
                        }
                    });
                } else {
                    Log.e("ERROR", "could not get device insurance");
                    if (e == null) {
                        Log.e("ERROR", e.getMessage());
                    }
                    showError();
                }
            }
        });
    }

    private void deleteDevice(){
        parseDevice.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("ERROR", "error deleting device");
                    Log.e("ERROR", e.getMessage());
                    showError();
                } else {
                    deleteUser();
                }
            }
        });
    }

    private void deleteUser(){
        ParseUser.getCurrentUser().deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("ERROR", "error deleting user");
                    Log.e("ERROR", e.getMessage());
                    showError();
                } else {
                    ParseUser.logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void showError(){
        Toast toast = Toast.makeText(activity.getBaseContext(), "No se pudo eliminar todos los datos de esta aplicacion de demo, por favor contactar a soporte.", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 100);
        toast.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    class MenuAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return layers.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = inflater.inflate(R.layout.item_navigation_drawer, viewGroup, false);

            TextView textView = (TextView)view.findViewById(R.id.menu_title);
            textView.setText(layers[i]);

            ImageView imageView = (ImageView)view.findViewById(R.id.menu_image);
            imageView.setImageDrawable(getResources().getDrawable(layersImages[i]));

            return view;
        }
    }
}
