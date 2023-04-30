package com.cs240.netzero;

        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;
        import java.util.ArrayList;
        import com.cs240.netzero.data.ExpenseView;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ExpenseView> arrayList;

    public CustomAdapter(Context context, ArrayList<ExpenseView> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View retView = convertView;
        if (retView == null) {
            retView = LayoutInflater.from(context).inflate(R.layout.row_custom_el, parent, false);
        }

        ImageView icon = retView.findViewById(R.id.el_icon);
        TextView title = retView.findViewById(R.id.el_title);
        TextView price = retView.findViewById(R.id.el_price);

        icon.setImageResource(arrayList.get(position).getIconId());
        title.setText(arrayList.get(position).getTitle());
        price.setText(String.valueOf(arrayList.get(position).getSpent()));

        return retView;
    }
}
