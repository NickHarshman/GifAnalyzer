package vtHacks;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import java.io.File;
import java.util.Scanner;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifierOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;


public class GifAnalyzer {
     
    public static void main(String[] args) {
        GifRecognition watson = new GifRecognition(
            VisualRecognition.VERSION_DATE_2016_05_20);
        watson.setApiKey("use api key");
        /*
        ClassifierOptions createOptions = new ClassifierOptions.Builder().classifierName("foo")
            .addClass("wave", new File("src/WavePosTrain.zip"))
            .negativeExamples(new File("src/WaveNegTrain.zip"))
            .build();
        watson.createClassifier(createOptions).execute();*/
        Scanner input = new Scanner(System.in);
        boolean again = true;
        while(again) {
            System.out.println("Input gif file:");
            File testfile = new File(input.nextLine());
            if (testfile.exists()){
                GifSpliter spliter = new GifSpliter(testfile);
                spliter.getFrames();
                spliter.createActionProfile();
                System.out.println("Loading...");
                ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
                    .images(new File("src/gifFrames.zip")).build();
                VisualClassification result = watson.classifyGif(options);
                System.out.print(result);
                options = new ClassifyImagesOptions.Builder().images(new File("src/actionProfile.jpg"))
                    .classifierIds("wave").build();
                result = watson.classify(options).execute();
                System.out.println(result);
            }
            else {
            System.out.println("File could not be found");
            }
            System.out.print("\nTry again? Y/N\n");
            if(input.nextLine().toLowerCase().equals("n")){
                again = false;
            }
        }
        input.close();
    }
    
        

}
