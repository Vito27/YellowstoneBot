package com.yellowstone_bot.prediction;

import java.io.IOException;
import java.util.List;

public interface Prediction {
    List<String> getPrediction() throws IOException;
}