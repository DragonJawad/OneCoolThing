package edu.umich.engin.cm.onecoolthing.CoolFeeds;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.umich.engin.cm.onecoolthing.CoolThings.CoolThingData;

/**
 * Created by jawad on 19/09/15.
 */
public class CoolThingsPagerAdapter extends PagerAdapter {
    public static final String LOGTAG = "MD/CoolThingsPagerAdapt";

    // Array of all the recyclable CoolThingFrags
    ArrayList<CoolThingFrag> mListOfFrags;

    // Values used for determining how many frags to keep in memory
        // Note: Code is not optimized for below number to be changed, yet
    private static int MAX_FRAGS = 5; // Should be an odd number

    // Array of all coolThings
    ArrayList<CoolThingData> mListOfCoolThings;

    // Index of intended cool thing to display [index starts at 0]
    private int mCurIntendedIndex = -1;

    public CoolThingsPagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;

        // Initiate the list of cool thing frags
        mListOfFrags = new ArrayList<CoolThingFrag>(MAX_FRAGS);
    }

    public void LoadCoolThings(ArrayList<CoolThingData> allCoolThings) {
        mListOfCoolThings = allCoolThings;

        resetAllFragments(0);

        notifyDataSetChanged();
    }

    // Return the subTitle of a CoolThing at the given position
    public String getSubTitle(int i) {
        return mListOfCoolThings.get(i).getSubTitle();
    }

    // Return the body text of a CoolThing at the given position
    public String getBodyText(int i) {
        return mListOfCoolThings.get(i).getBodyText();
    }

    // Return the palette color of a CoolThing at the given position
    public String getPaletteColor(int i) {return mListOfCoolThings.get(i).getPaletteColor(); }

    // Return the fullItemURL of a CoolThing at the given position
    public String getFullItemURL(int i) {
        return mListOfCoolThings.get(i).getFullItemURL();
    }

    // Returns the tweetText of a CoolThng at the given position
    public String getTweetText(int i) { return mListOfCoolThings.get(i).getTweetText(); }

    private void resetAllFragments(int newPosition) {
        // First, simply load the current position's appropriate fragment
        mListOfFrags.get(newPosition%5)
                .LoadNewCoolThing(mListOfCoolThings.get(newPosition),
                        newPosition,
                        mListOfCoolThings.size());

        // Try to load the fragments two places behind, if applicable
        if(newPosition != 0 && newPosition != 1) {
            mListOfFrags.get((newPosition-2)%5)
                .LoadNewCoolThing(mListOfCoolThings.get(newPosition-2),
                        newPosition-2,
                        mListOfCoolThings.size());
        }

        // Try to load the fragment right behind, if applicable
        if(newPosition != 0) {
            mListOfFrags.get((newPosition-1)%5)
                    .LoadNewCoolThing(mListOfCoolThings.get(newPosition-1),
                            newPosition-1,
                            mListOfCoolThings.size());
        }

        // Try to load the fragments two places ahead, if applicable
        if(newPosition != mListOfCoolThings.size()-1 && newPosition != mListOfCoolThings.size()-2) {
            mListOfFrags.get((newPosition+2)%5)
                    .LoadNewCoolThing(mListOfCoolThings.get(newPosition+2),
                            newPosition+2,
                            mListOfCoolThings.size());
        }

        // Try to load the fragment right ahead, if applicable
        if(newPosition != mListOfCoolThings.size()-1) {
            mListOfFrags.get((newPosition+1)%5)
                    .LoadNewCoolThing(mListOfCoolThings.get(newPosition+1),
                            newPosition+1,
                            mListOfCoolThings.size());
        }
    }

    public Fragment getItem(int position) {
        // If the list of cool things hasn't beem received yet, just display the first loading frag
        if(mListOfCoolThings == null) {
            // If position is NOT 0, something serious is going on
            if(position != 0) {
                Log.e(LOGTAG, "Unexpected event- getItem with non-zero position and no data yet!");
                throw new IllegalArgumentException("Unexpected event- getItem with non-zero position and no data yet!");
            }

            // Remember that the current position is now at the beginning
            mCurIntendedIndex = 0;

            return mListOfFrags.get(0);
        }

        int totalThings = mListOfCoolThings.size();
        // If new index is not adjacent, reset all fragments
        if(Math.abs(mCurIntendedIndex - position) > 1) {
            resetAllFragments(position);
        }
        // Otherwise if this is moving one frag forward, load a fragment for further ahead
        else if(position > mCurIntendedIndex) {
            // If at the last two positions, nothing to set up
            if(position == totalThings-1 || position == totalThings-2) {
                // Do nothing
            }
            // Otherwise simply load the frag two ahead
            else {
                mListOfFrags.get((position+2)%5)
                        .LoadNewCoolThing(mListOfCoolThings.get(position+2),
                                position+2,
                                mListOfCoolThings.size());
            }
        }
        // Otherwise if this is moving one frag backward, load a fragment for further behind
        else {
            // If this is at one of the first two spots, nothing to do
            if(position == 0 || position == 1) {
                // Do nothing. Yes I like if statements this way, easier to read
            }
            else {
                // Get the frag three positions behind and set it for two positions ahead
                mListOfFrags.get((position-2)%5)
                        .LoadNewCoolThing(mListOfCoolThings.get(position-2),
                                position-2,
                                mListOfCoolThings.size());
            }
        }

        mCurIntendedIndex = position;
        // Return the fragment appropriate for the current position (should already be set)
        return mListOfFrags.get(position%5);
    }

    @Override
    public int getCount() {
        // If the list of cool things hasn't been retrieved yet, use one only for displaying a single frag
        if(mListOfCoolThings == null)
            return 1;
        // Otherwise, the entire list of cool things represents all the items that can be displayed
        else
            return mListOfCoolThings.size();
    }

    private static final String TAG = "FragmentPagerAdapter";
    private static final boolean DEBUG = true;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        final long itemId = getItemId(position);

        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            if (DEBUG) Log.v(TAG, "Attaching item #" + itemId + ": f=" + fragment);
            mCurTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            if (DEBUG) Log.v(TAG, "Adding item #" + itemId + ": f=" + fragment);
            mCurTransaction.add(container.getId(), fragment,
                    makeFragmentName(container.getId(), itemId));
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        if (DEBUG) Log.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + object
                + " v=" + ((Fragment)object).getView());
        mCurTransaction.remove((Fragment)object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    /**
     * Return a unique identifier for the item at the given position.
     *
     * <p>The default implementation returns the given position.
     * Subclasses should override this method if the positions of items can change.</p>
     *
     * @param position Position within this adapter
     * @return Unique identifier for the item at position
     */
    public long getItemId(int position) {
        return position;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
