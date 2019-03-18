package com.alttech.admaxx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

public class BannerView extends android.support.v7.widget.AppCompatImageView {
  public BannerView(Context context) {
    super(context);
    this.onClick();
  }

  public BannerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.onClick();
  }

  public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.onClick();
  }

  public void onClick() {
    this.setOnClickListener(view -> AdMaxx.get().clickThrough(getContext()));
  }

  @Override
  public void setImageBitmap(Bitmap bm) {
    super.setImageBitmap(bm);
    AdMaxx.get().registerCreateView();
  }

  @Override
  public void setImageDrawable(@Nullable Drawable drawable) {
    super.setImageDrawable(drawable);
    AdMaxx.get().registerCreateView();
  }

}
