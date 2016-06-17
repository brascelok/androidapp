/*
 * Copyright (C) 2016 blackrose
 */
package com.blackrose.learnobjectivec.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackrose.learnobjectivec.R;
import com.blackrose.learnobjectivec.utilities.Util;
import com.blackrose.learnobjectivec.gui.PinnedSectionListView;


public class TheoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theory);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final PinnedSectionListView listView = (PinnedSectionListView) findViewById(R.id.listPinned);
        SimpleAdapter pinnedAdapter = new SimpleAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(pinnedAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) listView.getAdapter().getItem(position);
                if (position == 0 || position == 24) {
                    return;
                } else {
                    Intent intent = new Intent(getBaseContext(), TheoryDetailActivity.class);
                    intent.putExtra(Util.INTENT_THEORY_DETAIL_HTML_NAME, Util.HTML_FILES[position]);
                    intent.putExtra(Util.INTENT_THEORY_DETAIL_POS, position);
                    startActivity(intent);
                }
            }
        });
    }

    static class SimpleAdapter extends ArrayAdapter<Item> implements PinnedSectionListView.PinnedSectionListAdapter {

        private static final int[] COLORS = new int[]{
                R.color.green_light, R.color.orange_light,
                R.color.blue_light, R.color.red_light};

        public SimpleAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
            generateDataset();
        }

        public void generateDataset() {

            int sectionPosition = 0;

            Item section1 = new Item(Item.SECTION, "Basic");
            section1.sectionPosition = sectionPosition;
            add(section1);

            for (int i = 0; i < Util.TITLES_BASIC.length; i++) {
                Item item = new Item(Item.ITEM, Util.TITLES_BASIC[i]);
                item.sectionPosition = sectionPosition;
                add(item);
            }
            sectionPosition++;

            Item section2 = new Item(Item.SECTION, "Advance");
            section2.sectionPosition = sectionPosition;
            add(section2);

            for (int j = 0; j < Util.TITLES_ADVANCE.length; j++) {
                Item item2 = new Item(Item.ITEM, Util.TITLES_ADVANCE[j]);
                item2.sectionPosition = sectionPosition;
                add(item2);
            }

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTextColor(Color.DKGRAY);
            view.setTag("" + position);
            Item item = getItem(position);
            if (item.type == Item.SECTION) {
                view.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));
            }
            return view;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).type;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == Item.SECTION;
        }

    }

    static class Item {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final String text;

        public int sectionPosition;
        public int listPosition;

        public Item(int type, String text) {
            this.type = type;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

    }
}