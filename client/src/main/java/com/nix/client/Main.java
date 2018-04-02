package com.nix.client;

import com.nix.client.common.CameraVideoThread;
import com.nix.client.common.ScreenVideoThread;
import com.nix.client.common.TCPUtil;
import com.nix.client.common.VideoThread;
import com.nix.client.controller.MainController;
import com.nix.client.util.ImageUtil;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.image.BufferedImage;

/**
 * @author 11723
 */
public class Main extends Application {
    private final VideoThread.Exe exe = new VideoThread.Exe() {
        @Override
        public void exeImage(BufferedImage javaImage) {
            //在本地窗口显示自己的视频
            setImage(javaImage);
            //上传录制视频到服务器
            TCPUtil.sendImageMessage(ImageUtil.imageToImageMessage(javaImage));
        }
    };
    private final CameraVideoThread cameraVideoThread = new CameraVideoThread(exe);
    private final ScreenVideoThread screenVideoThread = new ScreenVideoThread(exe);
    private static Parent root;
    public static Main main;
    public MainController mainController;
    @Override
    public void start(Stage primaryStage) throws Exception{
        main = this;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("controller/sample.fxml"));
        root = fxmlLoader.load();
        primaryStage.setTitle("客户端");
        primaryStage.setScene(new Scene(root, 950, 736));
        primaryStage.show();
        mainController = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    mainController.close();
                    cameraVideoThread.stop();
                    screenVideoThread.stop();
                }catch (Exception e1){}
            }
        });
    }

    /**
     * 显示自己摄像头视频
     * */
    public void openCameraVideo() {
        cameraVideoThread.start();
    }
    /**
     * 关闭
     * */
    public void closeCameraVideo() {
        cameraVideoThread.stop();
    }

    /**
     * 显示屏幕录制视频
     * */
    public void openScreenVideo() {
        screenVideoThread.start();
    }
    public void closeScreenVideo() {
        screenVideoThread.stop();
    }
    /**
     * 设置本地视频窗口视频
     * */
    public static void setImage(BufferedImage javaImage) {
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
