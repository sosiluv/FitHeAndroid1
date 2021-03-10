package com.fithe.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.fithe.login.LoginActivity;
import com.fithe.login.loginandroid.R;
import com.fithe.login.logoutActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nhn.android.naverlogin.OAuthLogin;

import org.json.JSONObject;

public class NaviMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // 현재시간 초기화
    private long backBtnTime = 0;
    private FirebaseAuth mAuth;
    private static Context mContext;
    private static OAuthLogin mOAuthLoginInstance;
    private String nemail;
    private String gemail;

    TextView naviId1,naviId2;
    NavigationView navigationView;

    public NaviMainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        //네이베이션바 가져오기
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //네비게이션바 안에 선언된 네비 헤더부분 선언
        View header = navigationView.getHeaderView(0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //헤더부분 텍스트뷰 가져오기
        naviId1 = (TextView)header.findViewById(R.id.nav_id1);
        naviId2 = (TextView)header.findViewById(R.id.nav_id2);

        mContext = this;
        mOAuthLoginInstance = OAuthLogin.getInstance(); //네이버 인스턴스
        mAuth = FirebaseAuth.getInstance(); //firebase 인스턴스


        //네이버 로그인정보 가져오기
        if(mOAuthLoginInstance.getAccessToken(mContext)!=null) {
            System.out.println("mOAuthLoginInstance>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
          Intent intent = getIntent();
          String id = intent.getExtras().getString("id");
          String nemail = intent.getExtras().getString("email");
          String mobile = intent.getExtras().getString("mobile");
          System.out.println(">>>>>>>>>>>>>>>"+id+nemail+mobile);
          this.nemail=nemail;
            naviId1.setText(nemail+"님 환영합니다.");
            naviId2.setText(mobile);



        //구글 로그인정보 가져오기
        }else if(mAuth.getCurrentUser() != null ){
            FirebaseUser user = mAuth.getCurrentUser();
            if(user != null) {
                System.out.println("FirebaseUser>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                naviId1.setText(user.getEmail() + "님 환영합니다.");
                naviId2.setText(user.getPhoneNumber());
//            System.out.println("navi>>>>>>>>>>>>>>>>>>>>>>>"+user.getEmail());
//            System.out.println("navi>>>>>>>>>>>>>>>>>>>>>>>"+user.getDisplayName());
//            System.out.println("navi>>>>>>>>>>>>>>>>>>>>>>>"+user.getPhoneNumber());
//            System.out.println("navi>>>>>>>>>>>>>>>>>>>>>>>"+user.getUid());
            }
        }else{
            System.out.println("User>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            Intent intent1 = getIntent();
            String uid = intent1.getExtras().getString("uid");
            String uemail = intent1.getExtras().getString("uemail");
            String ugender = intent1.getExtras().getString("ugender");

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+uid);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+uemail);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>"+ugender);
            naviId1.setText(uid+"님 환영합니다.");
            naviId2.setText(uemail);

        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(NaviMainActivity.this, "글을 씁니다.", Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(NaviMainActivity.this,Tab_Write_Form.class);
//                startActivity(intent);
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Calendar"));
        tabLayout.addTab(tabLayout.newTab().setText("Maps"));
//        tabLayout.addTab(tabLayout.newTab().setText("myPage"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                TabFragment1 fragment1 = new TabFragment1();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            long curTime = System.currentTimeMillis();
            long gapTime = curTime - backBtnTime;

            if(0 <= gapTime && 2000 >= gapTime) {
                super.onBackPressed();
            }
            else {
                backBtnTime = curTime;
                Toast.makeText(this, "한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            try {
                if(mOAuthLoginInstance.getAccessToken(mContext)!=null) {
                    mOAuthLoginInstance = OAuthLogin.getInstance(); // 네이버 인스턴스
                    mOAuthLoginInstance.logout(mContext);
                    NaversignOut();
                }else if(mAuth.getCurrentUser() != null){
                    signOut();
                }else{
                    Intent intent = new Intent(this, LoginActivity.class);
                    Toast.makeText(mContext, "로그아웃 하셨습니다." , Toast.LENGTH_SHORT).show();
                    startActivity(intent);

                }
            }catch(Exception e){
                e.printStackTrace();
            }
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //네이버 로그아웃
    private void NaversignOut(){
        new logoutActivity.DeleteTokenTask(mContext,mOAuthLoginInstance).execute();
        Toast.makeText(mContext, "로그아웃 하셨습니다." , Toast.LENGTH_SHORT).show();
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //구글 로그아웃
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(mContext, "로그아웃 하셨습니다." , Toast.LENGTH_SHORT).show();
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        /**
         * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
         * one of the sections/tabs/pages.
         */
        public class SectionsPagerAdapter extends FragmentPagerAdapter {

            public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int position) {
                // getItem is called to instantiate the fragment for the given page.
                // Return a PlaceholderFragment (defined as a static inner class below).
                return PlaceholderFragment.newInstance(position + 1);
            }

            @Override
            public int getCount() {
                // Show 3 total pages.
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:

                        TabFragment1 tab1 = new TabFragment1();
                    case 1:
                        TabFragment2 tab2 = new TabFragment2();
                    case 2:
                        TabFragment3 tab3 = new TabFragment3();
                }
                return null;
            }
        }
    }
}

