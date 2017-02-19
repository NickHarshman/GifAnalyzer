/**
 * 
 */
package vtHacks;

//import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

/**
 * @author nickh25
 *
 */
public class GifRecognition extends VisualRecognition {

    public GifRecognition(String versionDate) {
        super(versionDate);
    }


    public GifRecognition(String versionDate, String apiKey) {
        super(versionDate, apiKey);
    }


    public VisualClassification classifyGif(ClassifyImagesOptions options) {
        VisualClassification results = super.classify(options).execute();
        List<ImageClassification> stillResults = results.getImages();
        int numStills = results.getImagesProcessed();
        HashMap<String, VisualClassifier.VisualClass> visualMap =
            new HashMap<>();
        for (ImageClassification result : stillResults) {
            List<VisualClassifier> clasifications = result.getClassifiers();
            for (VisualClassifier clasif : clasifications) {
                List<VisualClassifier.VisualClass> visClass = clasif
                    .getClasses();
                for (VisualClassifier.VisualClass def : visClass) {
                    String currentName = def.getName();
                    if (visualMap.containsKey(currentName)) {
                        VisualClassifier.VisualClass prevClass = visualMap.get(
                            currentName);
                        prevClass.setScore(prevClass.getScore() + def
                            .getScore());
                    }
                    else {
                        visualMap.put(def.getName(), def);
                    }
                }
            }
        }
        List<VisualClassifier.VisualClass> resultClasses = new ArrayList<>();
        for (VisualClassifier.VisualClass values : visualMap.values()) {
            values.setScore(values.getScore() / numStills);
            if(values.getScore() > 0.10)
            {
                resultClasses.add(values);
            }
        }
        VisualClassifier resultClassifier = new VisualClassifier();
        resultClassifier.setClasses(resultClasses);
        List<VisualClassifier> classifierList = new ArrayList<>();
        classifierList.add(resultClassifier);
        ImageClassification resultImage = new ImageClassification();
        resultImage.setClassifiers(classifierList);
        List<ImageClassification> imageList = new ArrayList<>();
        imageList.add(resultImage);
        results.setImages(imageList);
        return results;
    }

}
