package com.meetup.uhoo.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meetup.uhoo.R;
import com.meetup.uhoo.core.Happening;
import com.meetup.uhoo.core.Survey;
import com.meetup.uhoo.core.SurveyOption;
import com.meetup.uhoo.restaurant.HappeningsRecyclerAdapter;
import com.meetup.uhoo.service_layer.SurveyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by sultankhan on 1/13/17.
 */
public class SurveyFragment extends Fragment {

    private Survey survey;
    private TextView tvSurveyTitle;
    private TextView tvSurveyBody;
    private TextView btnYes;
    private TextView btnNo;

    private RecyclerView recyclerView;
    private SurveyFragmentListAdapter adapter;
    private List<SurveyOption> surveyOptionList;

    public SurveyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_survey, container, false);

        tvSurveyBody = (TextView) view.findViewById(R.id.tvSurveyBody);
        tvSurveyTitle = (TextView) view.findViewById(R.id.tvSurveyTitle);
        btnNo = (TextView) view.findViewById(R.id.tvNo);
        btnYes = (TextView) view.findViewById(R.id.tvYes);


        if(survey != null){
            tvSurveyBody.setText(survey.getBody());
            tvSurveyTitle.setText(survey.getTitle());

            surveyOptionList = survey.getOptions();


        }

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerSurvey(false);
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerSurvey(true);
            }
        });

        adapter = new SurveyFragmentListAdapter(getActivity(), surveyOptionList);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvSurveyAnswers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }


    public static SurveyFragment newInstance(Survey survey) {
        SurveyFragment fragment = new SurveyFragment();
        fragment.setSurvey(survey);

        return fragment;
    }


    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    private void answerSurvey(boolean answer) {
        SurveyService surveyService = new SurveyService(survey);
        surveyService.answerSurvey(answer);
    }


}

