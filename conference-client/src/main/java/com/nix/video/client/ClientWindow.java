package com.nix.video.client;

import com.nix.video.client.common.CameraVideoThread;
import com.nix.video.client.common.Config;
import com.nix.video.client.common.ScreenVideoThread;
import com.nix.video.client.common.VideoThread;
import com.nix.video.client.controller.MainController;
import com.nix.video.client.socket.RemotingVideoClient;
import com.nix.video.client.util.ImageUtil;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;

/**
 * @author 11723
 */
public class ClientWindow extends Application {
    private final VideoThread.Exe exe = javaImage -> {
        //在本地窗口显示自己的视频
        setImage(javaImage);
        //上传录制视频到服务器
        RemotingVideoClient.VIDEO_CLIENT.oneway(Config.getConnection(),ImageUtil.imageToImageMessage(javaImage));
    };

    private final CameraVideoThread cameraVideoThread = new CameraVideoThread(exe);
    private final ScreenVideoThread screenVideoThread = new ScreenVideoThread(exe);
    private static ClientWindow clientWindow;
    private static Parent rootWindow;
    public MainController mainController;
    @Override
    public void start(Stage primaryStage) throws Exception{
        clientWindow = this;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("controller/sample.fxml"));
        rootWindow = fxmlLoader.load();
        primaryStage.setTitle("客户端");
        primaryStage.setScene(new Scene(rootWindow, 950, 736));
        primaryStage.show();
        mainController = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(event -> {
            try {
                mainController.close();
                cameraVideoThread.stop();
                screenVideoThread.stop();
            }catch (Exception ignored){}
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
        clientWindow.mainController.setImage(SwingFXUtils.toFXImage(javaImage,new WritableImage(100,100)));
    }

    public static ClientWindow getClientWindow() {
        return clientWindow;
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
