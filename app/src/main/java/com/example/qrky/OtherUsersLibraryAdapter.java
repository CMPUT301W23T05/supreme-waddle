package com.example.qrky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class OtherUsersLibraryAdapter extends BaseAdapter {
    Context context;
    List<String> codeName;
    List<Integer> codeScore;
    List<List<String>> codeDrawing;  // 0 = eyes, 1 = nose, 2 = mouth
    LayoutInflater inflater;

    public OtherUsersLibraryAdapter(Context context, List<String> codeName, List<Integer> codeScore, List<List<String>> codeDrawing) {
        this.codeName = codeName;
        this.codeScore = codeScore;
        this.codeDrawing = codeDrawing;
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (inflater == null) {inflater = LayoutInflater.from(parent.getContext());}
//        if (convertView == null) {convertView = inflater.inflate(R.layout.a_code_brief, parent, false);}
        if (inflater == null) {inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);}
        if (convertView == null) {convertView = inflater.inflate(R.layout.a_code_brief, null);}

        TextView aCodeName = convertView.findViewById(R.id.codeCardName);
        TextView aCodeScore = convertView.findViewById(R.id.codeCardScore);
        TextView codeDrawingEyes = convertView.findViewById(R.id.codeCardEyes);
        TextView codeDrawingNose = convertView.findViewById(R.id.codeCardNose);
        TextView codeDrawingMouth = convertView.findViewById(R.id.codeCardMouth);

        try {
            aCodeName.setText(codeName.get(position));
            aCodeScore.setText(String.valueOf(codeScore.get(position)));
            codeDrawingEyes.setText(codeDrawing.get(position).get(0));
            codeDrawingNose.setText(codeDrawing.get(position).get(1));
            codeDrawingMouth.setText(codeDrawing.get(position).get(2));
        } catch (Exception ignored) {}


        return convertView;
    }


    @Override
    public int getCount() {
        return codeName.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
