package com.frontepic.thequizmagnificent.data;


import com.frontepic.thequizmagnificent.model.Question;

import java.util.ArrayList;

public interface QuestionListAsyncResponse {
    void processFinished(ArrayList<Question> questionArrayList);
}