package com.meetup.uhoo.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.meetup.uhoo.R;
import com.meetup.uhoo.core.Survey;
import com.meetup.uhoo.service_layer.SurveyService;

/**
 * Created by sultankhan on 12/18/16.
 */
public class SurveyView extends FrameLayout {

    Survey survey;

    TextView tvSurveyTitle;
    TextView tvSurveyBody;
    TextView btnYes;
    TextView btnNo;

    public SurveyView(Context context) {
        super(context);
        initView(context);
    }

    public SurveyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SurveyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SurveyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }


    private void initView(Context context) {
        View view = inflate(context, R.layout.custom_view_survey, null);


        tvSurveyBody = (TextView) view.findViewById(R.id.tvSurveyBody);
        tvSurveyTitle = (TextView) view.findViewById(R.id.tvSurveyTitle);
        btnNo = (TextView) view.findViewById(R.id.tvNo);
        btnYes = (TextView) view.findViewById(R.id.tvYes);

        btnNo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                answerSurvey(false);
            }
        });

        btnYes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                answerSurvey(true);
            }
        });


        addView(view);
    }

    private void answerSurvey(boolean answer){

        SurveyService surveyService = new SurveyService(survey);
        surveyService.answerSurvey(answer);
        setVisibility(GONE);
    }

    public void setSurvey(Survey survey){
        this.survey = survey;
        tvSurveyBody.setText(survey.getBody());
        tvSurveyTitle.setText(survey.getTitle());
    }


}
