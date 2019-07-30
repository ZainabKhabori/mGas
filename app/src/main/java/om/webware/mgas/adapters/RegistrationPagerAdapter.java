package om.webware.mgas.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import om.webware.mgas.fragments.pager.RegistrationPagerInfoFragment;
import om.webware.mgas.fragments.pager.RegistrationPagerLocationFragment;

public class RegistrationPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public RegistrationPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch(i) {
            case 0:
                RegistrationPagerInfoFragment infoFragment = RegistrationPagerInfoFragment.createFragment();
                infoFragment.setActivity(context);
                return infoFragment;
            case 1:
                RegistrationPagerLocationFragment locationFragment = RegistrationPagerLocationFragment.createFragment();
                locationFragment.setContext(context);
                return locationFragment;
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
