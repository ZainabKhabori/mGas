package om.webware.mgas.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

import om.webware.mgas.fragments.pager.MakeOrderPagerFragment;

public class MakeOrderPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private ArrayList<MakeOrderPagerFragment> fragments;
    private int index;

    public MakeOrderPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {
        switch(i) {
            case 0:
                MakeOrderPagerFragment serviceType = MakeOrderPagerFragment.createFragment("serviceType");
                serviceType.setContext(context);
                fragments.add(serviceType);
                index = i;
                return serviceType;
            case 1:
                MakeOrderPagerFragment cylinderSize = MakeOrderPagerFragment.createFragment("cylinderSize");
                cylinderSize.setContext(context);
                fragments.add(cylinderSize);
                return cylinderSize;
            case 2:
                MakeOrderPagerFragment deliveryOptions = MakeOrderPagerFragment.createFragment("deliveryOptions");
                deliveryOptions.setContext(context);
                fragments.add(deliveryOptions);
                index = i;
                return deliveryOptions;
            case 3:
                MakeOrderPagerFragment confirmOrder = MakeOrderPagerFragment.createFragment("confirmOrder");
                confirmOrder.setContext(context);
                fragments.add(confirmOrder);
                index = i;
                return confirmOrder;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    public MakeOrderPagerFragment getCurrentFragment() {
        return fragments.get(index);
    }
}
