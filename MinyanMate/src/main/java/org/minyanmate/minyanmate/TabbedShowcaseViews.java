package org.minyanmate.minyanmate;

import android.app.Activity;
import android.support.v4.view.ViewPager;

import com.espian.showcaseview.ShowcaseViews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 3/18/14.
 */
public class TabbedShowcaseViews extends ShowcaseViews {

    List<Boolean> switchList = new ArrayList<Boolean>();
    ViewPager viewPager;

    public TabbedShowcaseViews(Activity activity, OnShowcaseAcknowledged acknowledgedListener,
                               ViewPager mViewPager) {
        super(activity, acknowledgedListener);

        viewPager = mViewPager;
    }

    public ShowcaseViews addView(ItemViewProperties properties, boolean isSwitchPoint) {
        switchList.add(isSwitchPoint);
        return super.addView(properties);
    }

    public void show(){

        if(switchList.get(0)){
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true);
        }
        switchList.remove(0);
        super.show();
    }

}
