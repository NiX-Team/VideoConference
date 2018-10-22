package com.nix.video.client.UI;

import com.alipay.remoting.Connection;
import com.nix.video.client.ClientWindow;
import com.nix.video.client.common.*;
import com.nix.video.client.remoting.RemotingVideoClient;
import com.nix.video.client.util.ImageUtil;
import com.nix.video.client.util.SyncCompareAndSet;
import com.nix.video.common.message.AbstractMessage;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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
    private final static ConcurrentHashMap<String,Boolean> OTHER_CLIENT_SIGN = new ConcurrentHashMap<>(16);
    private final static Map<String, SyncCompareAndSet> SERIAL_NUMBER = new HashMap<>(16);

    @FXML
    public void setImage(Image image) {
        video_box.setImage(image);
    }
    public void addAClient(AbstractMessage imageMessage) {
        if (imageMessage.getId() != SERIAL_NUMBER.get(imageMessage.getUserId()).highSet(imageMessage.getId())) {
            return;
        }
        Platform.runLater(() -> {
            if (maxPane != null && maxPane.getId().equals(imageMessage.getSign())) {
                ((ImageView)maxPane.lookup("#video")).setImage(SwingFXUtils.toFXImage(Objects.requireNonNull(ImageUtil.messageToBufferedImage(imageMessage)),new WritableImage(320,240)));
            }
            ObservableList<Node> children = otherVideoPane.getChildren().filtered(p -> imageMessage.getUserId().equals(p.getId()));
            ImageView view;
            Pane pane = null;
            if (children.size() == 0) {
                if (OTHER_CLIENT_SIGN.get(imageMessage.getSign())) {
                    pane = getClientPane(imageMessage, 320, 240, true);
                    otherVideoPane.getChildren().add(pane);
                    LogKit.debug("新增加一名用户：" + imageMessage);
                }
            } else {
                pane = (Pane) children.get(0);
            }
            if (pane != null) {
                view = (ImageView) pane.lookup("#video");
                view.setImage(SwingFXUtils.toFXImage(Objects.requireNonNull(ImageUtil.messageToBufferedImage(imageMessage)), new WritableImage(320, 240)));
            }
        });
    }

    public void serverSayHello(AbstractMessage imageMessage) {
        OTHER_CLIENT_SIGN.put(imageMessage.getUserId(),true);
        SERIAL_NUMBER.put(imageMessage.getUserId(),new SyncCompareAndSet(imageMessage.getId()));
    }

    public void removeClient(AbstractMessage imageMessage) {
        OTHER_CLIENT_SIGN.put(imageMessage.getUserId(),false);
        Platform.runLater(() -> {
            if (maxPane != null && maxPane.getId().equals(imageMessage.getSign())) {
                maxStage.close();
            }
            try {
                otherVideoPane.getChildren().filtered(p -> imageMessage.getSign().equals(p.getId())).forEach(pane -> otherVideoPane.getChildren().removeAll(pane));
                LogKit.info("移除面板" + imageMessage);
            }catch (Exception e) {
                LogKit.error("面板移除异常 {}",imageMessage);
            }
        });
    }

    public Pane getClientPane(AbstractMessage imageMessage, double width, double height, boolean haveMax) {
        Pane pane = new Pane();
        pane.setId(imageMessage.getUserId());
        pane.setPrefWidth(width);
        pane.setPrefHeight(height + 25);
        Text text = new Text(String.valueOf(imageMessage.getUserId()));
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
            Config.setRoomId(roomId.getText());
            Config.setUserId(userId.getText());
            Config.setServerHost(serverHost.getText());
            Config.setServerPort(Integer.valueOf(serverPort.getText()));
            Connection connection = RemotingVideoClient.VIDEO_CLIENT.connectionVideoServer(Config.getServerUrl());
            if (connection == null) {
                setError("连接服务器失败");
            } else {
                Config.setConnection(connection);
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
    private enum ButtonState{
        打开摄像头,
        关闭摄像头,
        打开屏幕分享,
        关闭屏幕分享
    }
    public void camera(MouseEvent mouseEvent) {
        if (openCamera.getText().equals(ButtonState.打开摄像头.name())) {
            ClientWindow.getClientWindow().openCameraVideo();
            openCamera.setText(ButtonState.关闭摄像头.name());
            openScreen.setText(ButtonState.打开屏幕分享.name());
            ClientWindow.getClientWindow().closeScreenVideo();
        }else {
            openCamera.setText(ButtonState.打开摄像头.name());
            ClientWindow.getClientWindow().closeCameraVideo();
            RemotingVideoClient.VIDEO_CLIENT.oneway(Config.getConnection(), AbstractMessage.createClientLeaveMessage(Config.getRoomId(),Config.getUserId()));
            setImage(null);
        }
    }

    public void screen(MouseEvent mouseEvent) {
        if (openScreen.getText().equals(ButtonState.打开屏幕分享.name())) {
            ClientWindow.getClientWindow().openScreenVideo();
            openScreen.setText(ButtonState.关闭屏幕分享.name());
            openCamera.setText(ButtonState.打开摄像头.name());
            ClientWindow.getClientWindow().closeCameraVideo();
        }else {
            openScreen.setText(ButtonState.打开屏幕分享.name());
            ClientWindow.getClientWindow().closeScreenVideo();
            RemotingVideoClient.VIDEO_CLIENT.oneway(Config.getConnection(), AbstractMessage.createClientLeaveMessage(Config.getRoomId(),Config.getUserId()));
            setImage(null);
        }
    }
}
