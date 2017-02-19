package vtHacks;


import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class GifSpliter {

    private File gif;
    private File outputFile;


    public GifSpliter(File input) {
        gif = input;
    }


    public List<BufferedImage> getFrames() {
        return this.getFrames(10);
    }


    public List<BufferedImage> getAllFrames() {
        return this.getFrames(-1);
    }


    public List<BufferedImage> getFrames(int numFrames) {
        List<BufferedImage> output = new ArrayList<>();
        outputFile = new File("src/gifFrames.zip");
        ZipOutputStream out;
        try {
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif")
                .next();
            ImageInputStream stream = ImageIO.createImageInputStream(gif);
            reader.setInput(stream, false);
            out = new ZipOutputStream(new FileOutputStream(outputFile));
            int count = reader.getNumImages(true);
            int inc;
            if (numFrames == -1 || count < numFrames) {
                inc = 1;
            }
            else {
                inc = count / numFrames;
            }
            for (int index = 0; index < count; index += inc) {
                BufferedImage frame = reader.read(index);
                output.add(frame);
                out.putNextEntry(new ZipEntry("frame" + index + ".png"));
                ImageIO.write(frame, "png", out);
            }
            out.close();
            reader.dispose();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return output;
    }


    public BufferedImage createActionProfile() {
        List<BufferedImage> frames = getAllFrames();
        int maxH = 0;
        int maxW = 0;
        for (int i = 0; i < frames.size(); i++) {
            BufferedImage img = frames.get(i);
            if (img.getWidth() > maxW) {
                maxW = img.getWidth();
            }

            if (img.getHeight() > maxH) {
                maxH = img.getHeight();
            }
        }
        BufferedImage actionProfile = new BufferedImage(maxW, maxH,
            BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < maxW; x++) {
            for (int y = 0; y < maxH; y++) {
                actionProfile.setRGB(x, y, 16777215);
            }
        }

        for (int i = 0; i + 1 < frames.size(); i++) {
            BufferedImage img1 = frames.get(i);
            BufferedImage img2 = frames.get(i + 1);
            if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2
                .getHeight()) {
                for (int x = 0; x < img1.getWidth(); x++) {
                    for (int y = 0; y < img1.getHeight(); y++) {
                        int dif = Math.abs(img1.getRGB(x, y) - img2.getRGB(x,
                            y));
                        int dif2 = Math.abs(actionProfile.getRGB(x,y) - dif);
                        actionProfile.setRGB(x, y,dif2);
                    }

                }
            }
        }

        try {
            ImageIO.write(actionProfile, "jpg", new File(
                "src/actionProfile.jpg"));
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return actionProfile;

    }


    public void clear() {
        outputFile.delete();
    }

}
