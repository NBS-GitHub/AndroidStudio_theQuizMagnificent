package com.frontepic.thequizmagnificent;


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.frontepic.thequizmagnificent.controller.AppController;
import com.frontepic.thequizmagnificent.data.QuestionBank;
import com.frontepic.thequizmagnificent.data.QuestionListAsyncResponse;
import com.frontepic.thequizmagnificent.model.Question;
import com.frontepic.thequizmagnificent.model.Score;
import com.frontepic.thequizmagnificent.util.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView question_textView;
    private TextView counter_textView;
    private TextView score_textView;
    private TextView highestScore_textView;
    private Button true_button;
    private Button false_button;
    private ImageButton prev_button;
    private ImageButton next_button;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;

    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();
        prefs = new Prefs(MainActivity.this);

        question_textView = findViewById(R.id.question_textView);
        counter_textView = findViewById(R.id.counter_textView);
        score_textView = findViewById(R.id.score_textView);
        highestScore_textView = findViewById(R.id.highestScore_textView);
        true_button = findViewById(R.id.true_button);
        false_button = findViewById(R.id.false_button);
        prev_button = findViewById(R.id.prev_button);
        next_button = findViewById(R.id.next_button);

        prev_button.setOnClickListener(this);
        next_button.setOnClickListener(this);
        true_button.setOnClickListener(this);
        false_button.setOnClickListener(this);

        score_textView.setText("Current score: " + score.getScore());
        currentQuestionIndex = prefs.getState();
        highestScore_textView.setText("Highest score: " + prefs.getHighestScore());

        questionList = new QuestionBank().getQuestions(new QuestionListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                question_textView.setText(questionArrayList.get(currentQuestionIndex).getQuestionText());
                counter_textView.setText(currentQuestionIndex + " / " + questionArrayList.size());
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.prev_button:
                currentQuestionIndex = currentQuestionIndex > 0 ? currentQuestionIndex - 1 : currentQuestionIndex;
                updateQuestion();
                break;
            case R.id.next_button:
                goNext();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
        }
    }

    private void checkAnswer(boolean usersAnswer) {
        boolean correctAnswer = questionList.get(currentQuestionIndex).getCorrectAnswer();
        int toastMessageId = 0;
        if(usersAnswer == correctAnswer) {
            fadeAnimation();
            addPoints();
            toastMessageId = R.string.correct_answer;
        } else {
            shakeAnimation();
            subtractPoints();
            toastMessageId = R.string.incorrect_answer;
        }
        Toast.makeText(getApplicationContext(), toastMessageId, Toast.LENGTH_SHORT).show();
    }

    private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        score_textView.setText("Current score: " + score.getScore());
    }

    private void subtractPoints() {
        scoreCounter -= 100;
        if(scoreCounter > 0) {
            score.setScore(scoreCounter);
            score_textView.setText("Current score: " + score.getScore());
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
            score_textView.setText("Current score: " + score.getScore());
        }
    }

    private void updateQuestion() {
        String questionText = questionList.get(currentQuestionIndex).getQuestionText();
        question_textView.setText(questionText);
        counter_textView.setText(currentQuestionIndex + " / " + questionList.size());
    }

    private void fadeAnimation() {
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, .7f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void goNext() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
    }

}

