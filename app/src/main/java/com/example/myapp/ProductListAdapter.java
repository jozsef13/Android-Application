package com.example.myapp;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.w3c.dom.Text;

import java.util.List;

public class ProductListAdapter extends ArrayAdapter<Product> {

    private Context mContext;
    int mResource;

    public ProductListAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = getItem(position).getName();
        String imgUrl = getItem(position).getImgUrl();
        String price = getItem(position).getPrice();
        String category = getItem(position).getCategory();
        String details = getItem(position).getDetails();

        Product product = new Product(name, imgUrl, price, category, details);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.productTitle);
        TextView tvDetails = (TextView) convertView.findViewById(R.id.productDetails);
        TextView tvPrice = (TextView) convertView.findViewById(R.id.productPrice);
        TextView tvCategory = (TextView) convertView.findViewById(R.id.productCategory);
        ImageView productImg = (ImageView) convertView.findViewById(R.id.productImage);

        tvName.setText(name);
        tvDetails.setText(details);
        tvPrice.setText(price);
        tvCategory.setText(category);

        Glide.with(mContext).load(imgUrl).centerCrop().crossFade().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                System.out.println(e.toString());
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(productImg);

        return convertView;
    }
}
