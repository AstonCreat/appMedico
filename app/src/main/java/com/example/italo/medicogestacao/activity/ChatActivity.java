package com.example.italo.medicogestacao.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.italo.medicogestacao.R;
import com.example.italo.medicogestacao.fragment.ContatoFragment;
import com.example.italo.medicogestacao.fragment.ConversaFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //CRIANDO TOOLBAR;
        toolbar = (Toolbar) findViewById(R.id.toolbarChat);
        toolbar.setTitle("Chat Gestante");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //FIM TOOLBAR;]
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
          getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Conversas", ConversaFragment.class)
                        .add("Contato", ContatoFragment.class)
                        .create()
        );
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
    }
}
