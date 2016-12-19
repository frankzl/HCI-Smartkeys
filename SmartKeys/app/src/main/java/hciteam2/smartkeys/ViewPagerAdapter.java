package hciteam2.smartkeys;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frankz on 17.12.16.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public int getCurrentPage(){
        for(int i = 0; i < mFragmentList.size(); i++){
            KeyPage page = (KeyPage)mFragmentList.get(i);
            if(page.isVisibleToUser()){
                return i;
            }
        }
        return -1;
    }

    public void renameCurrentPage(String newTitle){
        int index = getCurrentPage();
        System.out.println("index"+index);
        if(index != -1){
            mFragmentTitleList.set(index, newTitle);
            notifyDataSetChanged();
        }
    }

    public void removeCurrentPage(){
        int index = getCurrentPage();
        if(index != -1){
            mFragmentTitleList.remove(index);
            mFragmentList.remove(index);
        }
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}