package com.example.firsttry.utilities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.firsttry.R;

public class GlideHelper
{
    public static void setImage(
            ImageView imageView,
            String url,
            Context context)
    {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(imageView);
    }
}
