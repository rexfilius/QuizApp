package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizapp.data.AnswerListAsyncResponse;
import com.example.quizapp.data.QuestionBank;
import com.example.quizapp.model.Question;
import com.example.quizapp.model.Score;
import com.example.quizapp.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView counterText;
    private TextView scoreText;
    private TextView highscoreText;
    private TextView questionText;
    private Button trueButton;
    private Button falseButton;
    private ImageButton previousButton;
    private ImageButton nextButton;

    private int currentQuestionIndex = 0;
    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counterText = findViewById(R.id.counter_text);
        scoreText = findViewById(R.id.score_text);
        highscoreText = findViewById(R.id.high_score_text);
        questionText = findViewById(R.id.question_text);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        scoreText.setText(MessageFormat.format("Current Score: {0}",
                                                String.valueOf(score.getScore())));
        currentQuestionIndex = prefs.getState();
        highscoreText.setText(MessageFormat.format("Highest Score: {0}",
                                                    String.valueOf(prefs.getHighScore())));

        questionList =
                new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
                    @Override
                    public void processFinished(ArrayList<Question> questionArrayList) {
                        questionText.setText(
                                questionArrayList.get(currentQuestionIndex).getAnswer());
                        counterText.setText(
                                currentQuestionIndex + " / " + questionArrayList.size());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.previous_button:
                if(currentQuestionIndex > 0) {
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    updateQuestion();
                }
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

    private void checkAnswer(boolean userChooseCorrect) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageId = 0;
        if(userChooseCorrect == answerIsTrue) {
            fadeView();
            addPoints();
            toastMessageId = R.string.correct_answer;
        } else {
            shakeAnimation();
            deductPoints();
            toastMessageId = R.string.wrong_answer;
        }
        Toast.makeText(MainActivity.this, toastMessageId, Toast.LENGTH_SHORT).show();
    }

    private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        scoreText.setText(MessageFormat.format("Current Score: {0}",
                                                String.valueOf(score.getScore())));
    }

    private void deductPoints() {
        scoreCounter -= 100;
        if(scoreCounter > 0) {
            score.setScore(scoreCounter);
            scoreText.setText(MessageFormat.format("Current Score: {0}",
                                                    String.valueOf(score.getScore())));
        } else {
            score.setScore(scoreCounter);
            scoreText.setText(MessageFormat.format("Current Score: {0}",
                                                    String.valueOf(score.getScore())));
        }

    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        questionText.setText(question);
        counterText.setText(MessageFormat.format("{0} / {1}",
                                currentQuestionIndex, questionList.size()));
    }

    private void goNext() {
        currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
        updateQuestion();
    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        super.onPause();
    }

    private void fadeView() {
        final CardView cardView = findViewById(R.id.question_card);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f,0.0f);

        alphaAnimation.setDuration(350);
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
        Animation shake =
                AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.question_card);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
