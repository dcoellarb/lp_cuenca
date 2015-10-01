package com.dc.lockphone;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by dcoellar on 9/23/15.
 */
public class PayActivity extends Activity {

    private LayoutInflater inflater;
    private int selected = -1;
    private PayListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflater = getLayoutInflater();

        setContentView(R.layout.activity_pay);

        ListView list = (ListView) findViewById(R.id.pay_list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected = i;
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new PayListAdapter();
        list.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    class PayListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return 3;
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
            view = inflater.inflate(R.layout.item_pay,viewGroup,false);

            LinearLayout container = (LinearLayout)view.findViewById(R.id.item_container);
            LinearLayout inner_container = (LinearLayout)view.findViewById(R.id.item_inner_container);
            ImageView imageView = (ImageView)view.findViewById(R.id.pay_item_image);
            TextView textView = (TextView)view.findViewById(R.id.pay_item_text);

            switch (i){
                case 0:
                    container.setBackgroundColor(getResources().getColor(R.color.payphone));
                    imageView.setImageResource(R.drawable.payphone);
                    textView.setText("PayPhone");
                    textView.setTextColor(getResources().getColor(R.color.lp_grey));
                    if (i == selected){
                        inner_container.setBackgroundColor(getResources().getColor(R.color.payphone));
                        textView.setTextColor(getResources().getColor(R.color.default_background));
                    }
                    break;
                case 1:
                    container.setBackgroundColor(getResources().getColor(R.color.datafast));
                    imageView.setImageResource(R.drawable.datafast);
                    textView.setText("Datafast");
                    textView.setTextColor(getResources().getColor(R.color.lp_grey));
                    if (i == selected){
                        inner_container.setBackgroundColor(getResources().getColor(R.color.datafast));
                        textView.setTextColor(getResources().getColor(R.color.default_background));
                    }
                    break;
                case 2:
                    container.setBackgroundColor(getResources().getColor(R.color.paypal));
                    imageView.setImageResource(R.drawable.paypal);
                    textView.setText("PayPal");
                    textView.setTextColor(getResources().getColor(R.color.lp_grey));
                    if (i == selected){
                        inner_container.setBackgroundColor(getResources().getColor(R.color.paypal));
                        textView.setTextColor(getResources().getColor(R.color.default_background));
                    }
                    break;
            }

            return view;
        }
    }
}
