package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import wesicknessdect.example.org.wesicknessdetect.models.Country;

public class CountryArrayAdapter extends ArrayAdapter<String> {
    private  LayoutInflater mInflater;
    private  Context mContext;
    private List<Country> countries=new ArrayList<>();
    private  int mResource=0;
    public CountryArrayAdapter(@NonNull Context context, int resource,List<Country> mCountries) {
        super(context, resource);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        countries=mCountries;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);

        return view;
    }
}
