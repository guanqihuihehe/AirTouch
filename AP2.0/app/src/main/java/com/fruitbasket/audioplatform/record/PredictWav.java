package com.fruitbasket.audioplatform.record;


import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.fruitbasket.audioplatform.R;
import com.fruitbasket.audioplatform.WaveData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


import org.tensorflow.lite.Interpreter;


public class PredictWav extends Activity{
    private static final String TAG="..PredictWav";
    private String file_path = null;
    private Interpreter tflite = null;
    private boolean load_result = false;


    private List<String> resultLabel = new ArrayList<>();

    public PredictWav(String path){
        file_path = path;
        readCacheLabelFromLocalFile();
        load_model("keras_BNmodel_resize_all_txt");
        init();
    }

    private void init(){
        WaveData reader = new WaveData(file_path);
        double[][] tempdata = reader.getData();
        ByteBuffer inputData =getScaledMatrix(tempdata);
        try {
            // Data format conversion takes too long
            // Log.d("inputData", Arrays.toString(inputData));
            float[][] labelProbArray = new float[1][10];
            long start = System.currentTimeMillis();
            // get predict result
            tflite.run(inputData, labelProbArray);
            long end = System.currentTimeMillis();
            long time = end - start;
            float[] results = new float[labelProbArray[0].length];
            System.arraycopy(labelProbArray[0], 0, results, 0, labelProbArray[0].length);
            // show predict result and time
            int r = get_max_result(results);
            String show_text = "result：" + r + "\nname：" + resultLabel.get(r) + "\nprobability：" + results[r] + "\ntime：" + time + "ms";
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(tempdata.length+" "+tempdata[0].length);
    }

    /**
     * Memory-map the model file in Assets.
     */
    private MappedByteBuffer loadModelFile(String model) throws IOException {
        AssetFileDescriptor fileDescriptor = getApplicationContext().getAssets().openFd(model + ".tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // load infer model
    private void load_model(String model) {
        try {
            tflite = new Interpreter(loadModelFile(model).asReadOnlyBuffer());
            Toast.makeText(PredictWav.this, model + " model load success", Toast.LENGTH_SHORT).show();
            Log.d(TAG, model + " model load success");
//            tflite.setNumThreads(4);
            load_result = true;
        } catch (IOException e) {
            Toast.makeText(PredictWav.this, model + " model load fail", Toast.LENGTH_SHORT).show();
            Log.d(TAG, model + " model load fail");
            load_result = false;
            e.printStackTrace();
        }
    }

    public static ByteBuffer getScaledMatrix(double[][] fft_data) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(113*113*4);
        imgData.order(ByteOrder.nativeOrder());
        for (int i = 0; i < 113; ++i) {
            for (int j = 0; j < 113; ++j) {
                final double val = fft_data[i][j];
                imgData.putFloat((float) val);
            }
        }
        return imgData;
    }

    // get max probability label
    // predict image
    private int get_max_result(float[] result) {
        float probability = result[0];
        int r = 0;
        for (int i = 0; i < result.length; i++) {
            if (probability < result[i]) {
                probability = result[i];
                r = i;
            }
        }
        return r;
    }

    private void readCacheLabelFromLocalFile() {
        try {
            AssetManager assetManager = getApplicationContext().getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("labels.txt")));
            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                resultLabel.add(readLine);
            }
            reader.close();
        } catch (Exception e) {
            Log.e("labelCache", "error " + e);
        }
    }

}
