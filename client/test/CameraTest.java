import com.nix.client.VideoThread;
import com.xuggle.xuggler.*;

import java.awt.image.BufferedImage;
import java.io.File;

public class CameraTest {
    public static void main(String[] args) throws InterruptedException {
        VideoThread.start(new VideoThread.Exe() {
            @Override
            public void exeImage(BufferedImage javaImage) {
                System.out.println(javaImage.hashCode());
            }
        });
//        doCaptureWebCamFrame();

    }

    public static File doCaptureWebCamFrame() {
        return WebCamDialog.getInstance().init();
    }
}