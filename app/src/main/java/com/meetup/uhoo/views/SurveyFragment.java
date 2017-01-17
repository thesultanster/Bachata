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


        if(survey != null){
            tvSurveyBody.setText(survey.getBody());
            tvSurveyTitle.setText(survey.getTitle());

            surveyOptionList = survey.getOptions();

        }

        adapter = new SurveyFragmentListAdapter(getActivity(), surveyOptionList, new SurveyAnswerInterface() {
            @Override
            public void onSingleAnswerSelected(int position) {
                SurveyService surveyService = new SurveyService();

                // Set options list
                surveyOptionList.get(position).setValue(true);
                survey.setOptions(surveyOptionList);

                // Create Survey Report on Database
                surveyService.generateSurveyReport(survey);
            }

            @Override
            public void onMultiAnswerSelected() {

            }
        });
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



}

