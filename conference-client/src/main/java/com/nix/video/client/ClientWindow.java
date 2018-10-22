package com.nix.video.client;

import com.nix.video.client.common.*;
import com.nix.video.client.UI.MainController;
import com.nix.video.client.remoting.RemotingVideoClient;
import com.nix.video.client.util.ImageUtil;
import com.nix.video.common.message.AbstractMessage;
import com.nix.video.common.util.log.LogKit;
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
    private final VideoThread.Exe exe = new ImageExe();

    private final CameraVideoThread cameraVideoThread = new CameraVideoThread(exe);
    private final ScreenVideoThread screenVideoThread = new ScreenVideoThread(exe);
    private static ClientWindow clientWindow;
    private static Parent rootWindow;
    public MainController mainController;
    @Override
    public void start(Stage primaryStage) throws Exception{
        clientWindow = this;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UI/sample.fxml"));
        rootWindow = fxmlLoader.load();
        primaryStage.setTitle("客户端");
        primaryStage.setScene(new Scene(rootWindow, 950, 736));
        primaryStage.show();
        mainController = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(event -> {
            try {
                Thread.sleep(200);
                mainController.close();
                cameraVideoThread.stop();
                screenVideoThread.stop();
            }catch (Exception e){
                LogKit.error("close window error",e);
            }
        });
    }

    /**
     * 显示自己摄像头视频
     * */
    public void openCameraVideo() {
        cameraVideoThread.start();
        RemotingVideoClient.VIDEO_CLIENT.oneway(Config.getConnection(), AbstractMessage.createClientSayHelloMessage(Config.getRoomId(), Config.getUserId()));
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
        RemotingVideoClient.VIDEO_CLIENT.oneway(Config.getConnection(), AbstractMessage.createClientSayHelloMessage(Config.getRoomId(), Config.getUserId()));
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
