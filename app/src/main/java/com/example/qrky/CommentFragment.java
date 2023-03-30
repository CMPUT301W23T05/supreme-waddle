package com.example.qrky;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends DialogFragment  {
    private ListView mListView;
    private Button mButtonLoadData;
    private ArrayList<String> mCommentList;
    private CardAdapter mCardAdapter;

    EditText text;

    public CommentFragment() {
        // empty constructor
    }
    public static CommentFragment newInstance() {
        CommentFragment fragment = new CommentFragment();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_comment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder((getContext()));

        // Associate views with the layout file
        text = view.findViewById(R.id.comment_editor); //editText
        mListView = view.findViewById(R.id.list_comments);
        mButtonLoadData = view.findViewById(R.id.submit_comment); //POST

        mCommentList = new ArrayList<>();


        mButtonLoadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommentList.add(String.valueOf(text.getText()));
                text.setText("");
            }
        });
        return builder
                .setView(view)
                .setNegativeButton("Exit", null).create();
    }





    ////////////////////////////////////////////////////////////////////////////////////////
//    public class MainActivity extends AppCompatActivity {
//
//        private ListView mListView;
//        private Button mButtonLoadData;
//        private Button mButtonDeleteItem;
//
//        private ArrayList<String> mCityList;
//        private ArrayAdapter<String> mCityAdapter;
//
//        EditText text;
//
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_main);
//            // Associate views with the layout file
//            text = findViewById(R.id.text);
//            mListView = findViewById(R.id.list_view_city);
//            mButtonLoadData = findViewById(R.id.button_load_data);
//            // It is called only once (So, I will make all initializations here)
//            mCityList = new ArrayList<>();
//            //mCityAdapter = new ArrayAdapter<>(this, R.layout.item_city, mCityList);
//            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, mCityList);
//            addDataToList("Edmontondsssssssdsdfsdfsdfsdfsdfsdfsdfsdfsdfsdfsdf");
//            addDataToList("Calgary");
//            // Set the Adapter
//            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//            mListView.setAdapter(adapter);
//            mButtonLoadData.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int count = adapter.getCount();
//                    mCityList.add(String.valueOf(text.getText()));
//                    adapter.notifyDataSetChanged();
//                    text.setText("");
//                }
//            });
//            mButtonDeleteItem = findViewById(R.id.button_delete_item);
//            mButtonDeleteItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int check, count = adapter.getCount();
//                    if (count > 0) {
//                        check = mListView.getCheckedItemPosition();
//                        if (check > -1 && check < count) {
//                            mCityList.remove(check);
//                            mListView.clearChoices();
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            });
//        }
//
//        private void addDataToList(String cityName) {
//            mCityList.add(cityName);
//        }
//    }

}
