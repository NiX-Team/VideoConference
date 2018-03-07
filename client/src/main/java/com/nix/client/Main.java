package com.nix.client;

import com.nix.client.common.TcpUtil;
import com.nix.client.common.VideoThread;
import com.nix.client.controller.MainController;
import com.nix.client.util.ImageUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * @author 11723
 */
public class Main extends Application {

    private static Parent root;
    public static Main main;
    public MainController mainController;
    private int i = 0;
    @Override
    public void start(Stage primaryStage) throws Exception{
        main = this;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("controller/sample.fxml"));
        root = fxmlLoader.load();
        primaryStage.setTitle("客户端");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
        mainController = fxmlLoader.getController();
        TcpUtil.getRoomId();
//        openVideo();
    }



    /**
     * 显示自己摄像头视频
     * */
    private void openVideo() {
        VideoThread.start(new VideoThread.Exe() {
            @Override
            public void exeImage(BufferedImage javaImage) {
                //在本地窗口显示自己的视频
                mainController.setImage(SwingFXUtils.toFXImage(javaImage,new WritableImage(100,100)));
                //上传录制视频到服务器
                TcpUtil.sendImageMessage(ImageUtil.imageToImageMessage(javaImage));
            }
        });
    }

    /**
     * 设置本地视频窗口视频
     * */
    public static void setImage(BufferedImage javaImage) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                final ImageView imageView = (ImageView) main.getNodeById(root,"video_box");
//                imageView.setImage(SwingFXUtils.toFXImage(javaImage,new WritableImage(100,100)));
//            }
//        });
        main.mainController.setImage(SwingFXUtils.toFXImage(javaImage,new WritableImage(100,100)));
    }

    /**
     * 在面板容器中根据id获取node
     * @param parent 容器
     * @param id 需要寻找node的id
     * @return
     * */
    private Node getNodeById(Parent parent,String id) {
        Node n = null;
        for (Node node:parent.getChildrenUnmodifiable()) {
            if (id.equals(node.getId())) {
                n = node;
            }else if (node instanceof Parent) {
                n = getNodeById((Parent) node,id);
            }
        }
        return n;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
