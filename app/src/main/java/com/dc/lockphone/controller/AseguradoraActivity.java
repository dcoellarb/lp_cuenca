package com.dc.lockphone.controller;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.dc.lockphone.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

/**
 * Created by dcoellar on 10/13/15.
 */
public class AseguradoraActivity extends AppCompatActivity {

    private String id;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        Bundle b = getIntent().getExtras();
        id = b.getString("id");

        setContentView(R.layout.activity_aseguradora);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Aseguradora");
        query.whereEqualTo("objectId",id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null && object != null) {
                    TextView nombre = (TextView) findViewById(R.id.aseguradora_nombre);
                    nombre.setText(object.getString("Nombre"));
                    TextView telefono = (TextView) findViewById(R.id.aseguradora_telefono);
                    telefono.setText(object.getString("Telefono"));
                    TextView contacto = (TextView) findViewById(R.id.aseguradora_contacto);
                    contacto.setText(object.getString("Contacto"));
                    TextView horario1 = (TextView) findViewById(R.id.aseguradora_horario_1);
                    try {
                        horario1.setText(object.getJSONArray("Horarios").get(0).toString());
                    } catch (JSONException e1) {
                        Log.e("Error", e.getMessage());
                    }
                    TextView horario2 = (TextView) findViewById(R.id.aseguradora_horario_2);
                    try {
                        horario2.setText(object.getJSONArray("Horarios").get(1).toString());
                    } catch (JSONException e2) {
                        Log.e("Error", e.getMessage());
                    }
                    TextView direccion = (TextView) findViewById(R.id.aseguradora_direccion);
                    direccion.setText(object.getString("Direccion"));


                    ImageView img = (ImageView) findViewById(R.id.aseguradora_image);
                    Picasso.with(activity.getBaseContext())
                            .load(object.getParseFile("Logo").getUrl())
                            .into(img);

                } else {
                    Log.e("ERROR", "could not get aseguradora");
                    if (e == null) {
                        Log.e("ERROR", e.getMessage());
                    }
                    //TODO - inform user of issues with parse
                }
            }
        });
    }

}
