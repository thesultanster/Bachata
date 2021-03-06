package com.meetup.uhoo.restaurant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meetup.uhoo.core.Happening;
import com.meetup.uhoo.R;
import com.meetup.uhoo.service_layer.happening_services.HappeningDataFetchListener;
import com.meetup.uhoo.service_layer.happening_services.HappeningService;

import java.util.ArrayList;

/**
 * Created by sultankhan on 11/22/16.
 */
public class HappeningsFragment extends Fragment {


    private RecyclerView recyclerView;
    private HappeningsRecyclerAdapter adapter;
    private String businessId;

    private SwipeRefreshLayout swipeRefreshLayout;


    @SuppressLint("ValidFragment")
    public HappeningsFragment( String businessId) {
        this.businessId = businessId;
    }

    public HappeningsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new HappeningsRecyclerAdapter(getActivity(), new ArrayList<Happening>());

        HappeningService happeningService = new HappeningService();
        happeningService.fetchHappenings(businessId, new HappeningDataFetchListener() {
            @Override
            public void onHappeningFetched(Happening object) {
                adapter.addRow(object);
            }
        });



        //adapter.addRow(new Happening("Free Drink","try our new Korean Citron Tea Slushie. Made fresh daily with hand squeezed citron and honey straight from the mouths of korean bees", Enum.HappeningType.DEAL));
        //adapter.addRow(new Happening("Open Mic Night","Open to comics of all colors, orientations, shapes and sizes, and planets of origin", Enum.HappeningType.EVENT));
        //adapter.addRow(new Happening("Pop Up Cafe","For five straight says, the folks from New York Bistro will be selling their handmade cockroach latte made with fresh roach larvee", Enum.HappeningType.COMEDY));
        //adapter.addRow(new Happening("Open Mic Night","This will be a closed off event only for the radical right wing sentiment that is sweeping the nation of Banana Republic", Enum.HappeningType.VARIETY));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people_checkedin, container, false);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}
