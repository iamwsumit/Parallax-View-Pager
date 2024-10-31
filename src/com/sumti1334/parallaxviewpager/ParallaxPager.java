package com.sumti1334.parallaxviewpager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.*;
import com.sumti1334.parallaxviewpager.ViewPager.Mode;
import com.sumti1334.parallaxviewpager.ViewPager.ParallaxViewPager;

import java.util.ArrayList;

public class ParallaxPager extends AndroidNonvisibleComponent {
  private Context activity;
  private ParallaxViewPager parallaxViewPager;
  private String modeString;
  private Mode mode;
  private boolean isCreated=false;
  private String TAG="Parallax View Pager";
  private ArrayList<View> views=new ArrayList<>();
  private Interpolator interpolator;
  private String interpolatorString;
  private float factor;

  public ParallaxPager(ComponentContainer container) {
    super(container.$form());
    this.activity= container.$context();
    this.parallaxViewPager=new ParallaxViewPager(this.activity);
    this.parallaxViewPager.setAdapter(new CustomPagerAdapter(this.views));
    this.Mode("None");
    this.InterpolatorFactor(0.5f);
    this.Interpolator("None");
    this.parallaxViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int i, float v, int i1) {
      }

      @Override
      public void onPageSelected(int i) {
        PageChanged(i+1);
      }

      @Override
      public void onPageScrollStateChanged(int i) {
      }
    });
  }
  @SimpleFunction
  public void AddViewPager(HVArrangement in){
    if (!this.isCreated){
      ((LinearLayout)((ViewGroup)in.getView()).getChildAt(0)).addView(this.parallaxViewPager);
      Log.d(TAG, "AddPager: Pager has been added to given layout"+in.toString());
      this.isCreated=true;
      this.Update();
    }
  }
  @SimpleFunction
  public void AddPage(AndroidViewComponent component){
    if (this.isCreated){
      Log.d(TAG, "AddTab: View Removed from its parent");
      ((ViewGroup) component.getView().getParent()).removeView(component.getView());
      this.parallaxViewPager.addView(component.getView());
      Log.d(TAG, "AddTab: View added to the view pager");
      this.views.add(component.getView());
      this.parallaxViewPager.getAdapter().notifyDataSetChanged();
      if (this.views.size()==1)
        this.parallaxViewPager.setCurrentItem(0);
    }
  }
  @SimpleFunction
  public void SelectPage(int position){
    if (this.isCreated){
      this.parallaxViewPager.setCurrentItem(position-1);
    }
  }
  @SimpleProperty
  public int GetCurrentPage(){
    if (this.isCreated)
      return this.parallaxViewPager.getCurrentItem()+1;
    else
      return 0;
  }
  @SimpleProperty
  @DesignerProperty(defaultValue = "0.5",editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_FLOAT)
  public void InterpolatorFactor(float factor){
    this.factor = factor;
    this.Interpolator(this.Interpolator()==null ? "None" : this.Interpolator());
  }
  @SimpleProperty
  public float InterpolatorFactor(){
    return this.factor;
  }
  @SimpleProperty
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_CHOICES,defaultValue = "None",editorArgs = {"None","Accelerate","Decelerate"})
  public void Interpolator(String interpolator2){
    this.interpolator=interpolator2.equals("Decelerate") ? new DecelerateInterpolator(this.factor) : new AccelerateInterpolator(this.factor);
    this.interpolatorString=interpolator2.equals("Decelerate") ? "Decelerate" : "Accelerate";
    this.Update();
  }
  @SimpleProperty
  public String Interpolator(){
    return this.interpolatorString;
  }
  @SimpleFunction
  public void RemoveView(int position){
    if (this.isCreated){
      try{
        Log.d(TAG, "RemoveView: Remove VIew at position called");
        if (!(this.views.size()<position)){
          this.parallaxViewPager.setAdapter(null);
          this.views.remove(this.views.get(position-1));
          this.parallaxViewPager.setAdapter(new CustomPagerAdapter(this.views));
          this.parallaxViewPager.getAdapter().notifyDataSetChanged();
        }else
          Log.e("Remove View","Large Index");
      }catch (Exception e){
        Log.e("Remove View",e.toString());
      }
    }
  }
  @SimpleFunction
  public void RemoveAll(){
    if (this.isCreated) {
      Log.d(TAG, "RemoveAll: Remove All views called");
      this.parallaxViewPager.setAdapter(null);
      this.views = new ArrayList<>();
      this.parallaxViewPager.setAdapter(new CustomPagerAdapter(this.views));
      this.parallaxViewPager.getAdapter().notifyDataSetChanged();
    }
  }
  @SimpleEvent
  public void PageChanged(int position){
    EventDispatcher.dispatchEvent(this,"PageChanged",position);
  }
  @SimpleProperty
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_CHOICES,defaultValue = "None",editorArgs = {"None","Left Overlay","Right Overlay"})
  public void Mode(String mode){
    if (mode.equals("None"))
      this.mode=Mode.NONE;
    else if (mode.equals("Left Overlay"))
      this.mode=Mode.LEFT_OVERLAY;
    else if (mode.equals("Right Overlay"))
      this.mode=Mode.RIGHT_OVERLAY;
    else
      return;
    this.modeString=mode;
    this.Update();
  }
  @SimpleProperty
  public String Mode(){
    return this.modeString;
  }
  private void Update(){
    if (this.isCreated){
      this.parallaxViewPager.setInterpolator(this.interpolator);
      this.parallaxViewPager.setMode(this.mode);
    }
  }
  public class CustomPagerAdapter extends PagerAdapter {
    private ArrayList<View> views;

    public CustomPagerAdapter(ArrayList<View> arrayList) {
      this.views = arrayList;
    }

    @Override
    public int getCount() {
      return this.views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int n) {
      View view = this.views.get(n);
      if (((ViewGroup) view.getParent()) !=null)
        ((ViewGroup) view.getParent()).removeView(view);
      viewGroup.addView(view);
      return view;
    }

    @Override
    public int getItemPosition(Object object) {
      int n = this.views.indexOf(object);
      if (n == -1) {
        return -2;
      }
      return n;
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int n, Object object) {
      viewGroup.removeView(this.views.get(n));
    }

    public int addView(View view) {
      this.views.add(view);
      return this.views.size();
    }

    public int addView(View view, int n) {
      this.views.add(n, view);
      return n;
    }

    public int removeView(ViewPager viewPager, View view) {
      int n = this.views.indexOf((Object)view);
      this.views.remove((Object)view);
      return n;
    }

    public int removeView(ViewPager viewPager, int n) {
      viewPager.setAdapter(null);
      this.views.remove(n);
      viewPager.setAdapter(this);
      return n;
    }
  }
}
