package com.example.qrky;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Tests for Community-related fragments.
 *
 * @author Franco Bonilla
 */
@RunWith(MockitoJUnitRunner.class)
public class CommunityFragmentsTest {

    @Mock
    private View mockView;
    @Mock
    private LayoutInflater mockInflater;
    @Mock
    private ViewGroup mockContainer;
    @Mock
    private Bundle mockSavedInstanceState;
    @Mock
    private TabLayout mockTabLayout;
    @Mock
    private ViewPager mockViewPager;
    @Mock
    private FragmentManager mockFragmentManager;
    @Mock
    private PagerAdapter mockPagerAdapter;
    private CommunityFragment communityFragment;

    @Before
    public void setUp() {
        communityFragment = new CommunityFragment();
    }


    /**
     * Tests that the PagerAdapter can get items.
     */
    @Test
    public void testGetItem() {
        // Create a new PagerAdapter w/ 2 tabs
        CommunityFragment.PagerAdapter pagerAdapter = communityFragment.new PagerAdapter(mockFragmentManager, 2);

        // Get the frags
        Fragment peepsComm = pagerAdapter.getItem(0);
        Fragment codesComm = pagerAdapter.getItem(1);

        assertTrue(peepsComm instanceof CommunityFragmentPeople);
        assertTrue(codesComm instanceof CommunityFragmentQrs);
    }

    /**
     * Tests that the PagerAdapter can count.
     */
    @Test
    public void testGetCount() {
        CommunityFragment.PagerAdapter pagerAdapter = communityFragment.new PagerAdapter(mockFragmentManager, 2);
        int count = pagerAdapter.getCount();

        assert(count == 2);
    }

    /**
     * Tests that the PagerAdapter can get page titles.
     */
    @Test
    public void testGetPageTitle() {
        CommunityFragment.PagerAdapter pagerAdapter = communityFragment.new PagerAdapter(mockFragmentManager, 2);
        CharSequence title1 = pagerAdapter.getPageTitle(0);
        CharSequence title2 = pagerAdapter.getPageTitle(1);

        assert(title1.toString().equals("Top Players"));
        assert(title2.toString().equals("Top Codes"));
    }
}
