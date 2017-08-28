package com.meetup.uhoo.restaurant;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.meetup.uhoo.R;
import com.meetup.uhoo.core.Business;
import com.meetup.uhoo.core.Survey;
import com.meetup.uhoo.core.SurveyOption;
import com.meetup.uhoo.core.User;
import com.meetup.uhoo.service_layer.SurveyDataFetchListener;
import com.meetup.uhoo.service_layer.SurveyService;
import com.meetup.uhoo.views.SurveyAnswerCompleteListener;
import com.meetup.uhoo.views.SurveyAnswerInterface;
import com.meetup.uhoo.views.SurveyFragment;
import com.meetup.uhoo.views.SurveyFragmentListAdapter;
import com.meetup.uhoo.views.SurveyView;

import java.util.List;

/**
 * Created by sultankhan on 9/25/16.
 */

public class SurveyDialog extends DialogFragment {

    private Survey survey;
    private TextView tvSurveyTitle;
    private TextView tvSurveyBody;

    private RecyclerView recyclerView;
    private SurveyFragmentListAdapter adapter;
    private List<SurveyOption> surveyOptionList;

    private SurveyAnswerCompleteListener surveyAnswerCompleteListener;

    public static SurveyDialog newInstance(Survey survey, SurveyAnswerCompleteListener surveyAnswerCompleteListener) {
        SurveyDialog frag = new SurveyDialog();
        frag.setSurvey(survey);
        frag.setSurveyAnswerCompleteListener(surveyAnswerCompleteListener);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //TODO:NOTE THIS USES FRAGMENT_SURVEY XML FILE
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_survey, null);


        tvSurveyBody = (TextView) view.findViewById(R.id.tvSurveyBody);
        tvSurveyTitle = (TextView) view.findViewById(R.id.tvSurveyTitle);


        if(survey != null){
            tvSurveyBody.setText(survey.getBody());
            tvSurveyTitle.setText(survey.getTitle());
            surveyOptionList = survey.getOptions();

            if(survey.getBody().equals("")){
                tvSurveyBody.setVisibility(View.GONE);
            }
        }

        adapter = new SurveyFragmentListAdapter(getActivity(), surveyOptionList, new SurveyAnswerInterface() {
            @Override
            public void onSingleAnswerSelected(int position) {
                SurveyService surveyService = new SurveyService();

                // Set options list
                surveyOptionList.get(position).setValue(true);
                survey.setOptions(surveyOptionList);

                // Create Survey Report on Database
                surveyService.generateSurveyReport(survey, new SurveyAnswerCompleteListener() {
                    @Override
                    public void onComplete() {
                        dismiss();
                        surveyAnswerCompleteListener.onComplete();
                    }
                });
            }

            @Override
            public void onMultiAnswerSelected() {

            }

        });
        recyclerView = (RecyclerView) view.findViewById(R.id.rvSurveyAnswers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        builder.setView(view);
        Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        return dialog;


    }

    public void setSurvey(Survey survey){
        this.survey = survey;
    }

    public void setSurveyAnswerCompleteListener(SurveyAnswerCompleteListener surveyAnswerCompleteListener){
        this.surveyAnswerCompleteListener = surveyAnswerCompleteListener;
    }
}
