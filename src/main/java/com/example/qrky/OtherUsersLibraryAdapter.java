package com.example.qrky;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for ListView of other users' library in OtherUsersLibraryFragment. This adapter is used to
 * display the other users' library in the OtherUsersLibraryFragment. It is used to display all the
 * codes, currently unsorted.
 *
 * @author Franco Bonilla
 * @version 1.0 2023/03/07
 * @see OtherUsersCodes
 */
public class OtherUsersLibraryAdapter extends BaseAdapter {
    Context context;
    List<String> codeName;
    List<Integer> codeScore;
    List<List<String>> codeDrawing;  // 0 = eyes, 1 = nose, 2 = mouth
    LayoutInflater inflater;

    /**
     * Constructor for OtherUsersLibraryAdapter. This constructor is used to initialize the adapter.
     * It inflates the layout for the adapter.
     *
     * @param context: Context of the activity
     * @param codeName: List of code names
     * @param codeScore: List of code scores
     * @param codeDrawing: List of code drawings
     * @since 1.0
     */
    public OtherUsersLibraryAdapter(Context context, List<String> codeName, List<Integer> codeScore, List<List<String>> codeDrawing) {
        this.codeName = codeName;
        this.codeScore = codeScore;
        this.codeDrawing = codeDrawing;
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Gets the View for a grid item.
     * @param position The position of the item within the adapter's data set of the item whose
     *                view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return The View for the grid item.
     * @since 1.0
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

    /**
     * Gets the number of items in the data set represented by this Adapter.
     *
     * @return The number of items in codeName.
     * @since 1.0
     */
    @Override
    public int getCount() {
        return codeName.size();
    }

    /**
     * Gets nothing useful; this is a mandatory override.
     *
     * @param position Position of the item whose data we want within the adapter's
     * data set.
     * @return null.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Gets nothing useful; this is a mandatory override.
     *
     * @param position Position of the item whose data we want within the adapter's
     * data set.
     * @return 0.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

}
