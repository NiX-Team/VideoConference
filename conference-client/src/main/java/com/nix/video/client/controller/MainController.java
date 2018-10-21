package com.nix.video.client.controller;

import com.alipay.remoting.Connection;
import com.nix.video.client.ClientWindow;
import com.nix.video.client.common.*;
import com.nix.video.client.socket.RemotingVideoClient;
import com.nix.video.client.util.ImageUtil;
import com.nix.video.common.message.AbstractMessage;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import com.nix.video.common.util.log.LogKit;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;

/**
 * @author 11723
 * 主控制面板
 */
public class MainController {
    @FXML
    private ImageView video_box;
    @FXML
    private FlowPane otherVideoPane;
    @FXML
    private TextField roomId;
    @FXML
    private TextField userId;
    @FXML
    private Button openCamera;
    @FXML
    private Button openScreen;
    @FXML
    private Text error;
    @FXML
    private TextField serverHost;
    @FXML
    private TextField serverPort;

    /**
     * 宽高比
     * */
    private final float widthHeigth = 1.2F;
    private Stage maxStage;
    private Pane maxPane;

    @FXML
    public void setImage(Image image) {
        video_box.setImage(image);
    }

    public void addAClient(AbstractMessage imageMessage) {
        Platform.runLater(() -> {
            if (maxPane != null && maxPane.getId().equals(imageMessage.getRoomId() + "-" + imageMessage.getUserId())) {
                ((ImageView)maxPane.lookup("#video")).setImage(SwingFXUtils.toFXImage(Objects.requireNonNull(ImageUtil.messageToBufferedImage(imageMessage)),new WritableImage(320,240)));
            }
            Pane pane = (Pane) otherVideoPane.lookup("#" + imageMessage.getUserId());
            ImageView view;
            if (pane == null) {
                pane = getClientPane(imageMessage,320,240,true);
                otherVideoPane.getChildren().add(pane);
                LogKit.info("新增加一名用户：" + imageMessage);
            }
            view = (ImageView) pane.lookup("#video");
            view.setImage(SwingFXUtils.toFXImage(Objects.requireNonNull(ImageUtil.messageToBufferedImage(imageMessage)),new WritableImage(320,240)));
        });
    }

    public void removeClient(AbstractMessage imageMessage) {
        Platform.runLater(() -> {
            if (maxPane != null && maxPane.getId().equals(imageMessage.getRoomId() + "-" + imageMessage.getUserId())) {
                maxStage.close();
            }
            Pane pane = (Pane) otherVideoPane.lookup("#" + imageMessage.getUserId());
            otherVideoPane.getChildren().removeAll(pane);
            LogKit.info("移除面板" + imageMessage);
        });
    }

    public Pane getClientPane(AbstractMessage imageMessage, double width, double height, boolean haveMax) {
        Pane pane = new Pane();
        pane.setId(imageMessage.getUserId());
        pane.setPrefWidth(width);
        pane.setPrefHeight(height + 25);
        Text text = new Text(String.valueOf(imageMessage.getId()));
        text.setLayoutX(0);
        text.setLayoutY(17);
        text.setFont(Font.font(20));
        text.setWrappingWidth(width);
        text.setTextAlignment(TextAlignment.CENTER);
        ImageView view = new ImageView();
        view.setLayoutY(25);
        view.setId("video");
        view.setFitWidth(width);
        view.setFitHeight(height);
        pane.getChildren().addAll(text,view);
        if (haveMax) {
            ImageView max = new ImageView(new Image(String.valueOf(getClass().getResource("/max.jpg"))));
            max.setFitWidth(30);
            max.setFitHeight(30);
            max.setLayoutX(width - 40);
            max.setLayoutY(height - 40);
            max.setOnMouseClicked(event -> {
                if (maxPane == null) {
                    showMaxVideo(imageMessage);
                }
            });
            pane.getChildren().add(max);
        }
        return pane;
    }

    public void sign(MouseEvent mouseEvent) {

        setError("");
        if (roomId.getText() == null || roomId.getText().isEmpty()) {
            setError("房间号不能为空");
            return;
        }
        if (userId.getText() == null || userId.getText().isEmpty()) {
            setError("用户id不能为空");
            return;
        }
        if (Boolean.parseBoolean(HttpClient.doGet("http://" + serverHost.getText() + "/server/" + roomId.getText() + "/" + userId.getText(),null))) {
            setError("用户名已存在");
            return;
        }
        if (Config.getConnection() == null) {
            try {
                Config.setRoomId(roomId.getText());
                Config.setUserId(userId.getText());
                Config.setServerHost(serverHost.getText());
                Config.setServerPort(Integer.valueOf(serverPort.getText()));
                Connection connection = RemotingVideoClient.VIDEO_CLIENT.createConnection(Config.getServerUrl());
                if (connection == null) {
                    setError("服务器不存在");
                    return;
                }
                Config.setConnection(connection);
                RemotingVideoClient.VIDEO_CLIENT.oneway(connection,AbstractMessage.createClientSayHelloMessage(Config.getRoomId(),Config.getUserId()));
            }catch (Exception e) {
                setError("连接服务器失败");
            }
        }
    }



    /**
     * 新建一个最大化窗口
     * */
    private void showMaxVideo(AbstractMessage message) {
        Stage stage = new Stage();
        maxStage = stage;
        stage.setTitle(message.getRoomId() + "-" + message.getUserId());
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        Pane pane = getClientPane(message,bounds.getMaxX(),bounds.getMaxY(),false);
        maxPane = pane;
        maxPane.setId(message.getRoomId() + "-" + message.getUserId());
        stage.setScene(new Scene(pane));
        stage.setOnCloseRequest(event -> maxPane = null);
        stage.addEventHandler(KeyEvent.ANY, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                maxPane = null;
                stage.close();
            }
        });
        stage.setFullScreen(true);
        stage.show();
    }

    public void setError(String errorMsg) {
        error.setText(errorMsg);
    }
    public void close() {
        RemotingVideoClient.VIDEO_CLIENT.shutdown();
    }

    public void camera(MouseEvent mouseEvent) {
        if (openCamera.getText().equals("打开摄像头")) {
            ClientWindow.getClientWindow().openCameraVideo();
            openCamera.setText("关闭摄像头");
            openScreen.setText("打开屏幕分享");
            ClientWindow.getClientWindow().closeScreenVideo();
        }else {
            openCamera.setText("打开摄像头");
            ClientWindow.getClientWindow().closeCameraVideo();
        }
    }

    public void screen(MouseEvent mouseEvent) {
        if (openScreen.getText().equals("打开屏幕分享")) {
            ClientWindow.getClientWindow().openScreenVideo();
            openScreen.setText("关闭屏幕分享");
            openCamera.setText("打开摄像头");
            ClientWindow.getClientWindow().closeCameraVideo();
        }else {
            openScreen.setText("打开屏幕分享");
            ClientWindow.getClientWindow().closeScreenVideo();
        }
    }
}
