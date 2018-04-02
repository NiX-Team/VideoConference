package com.nix.client.controller;

import com.nix.client.Main;
import com.nix.client.common.ClientConsumers;
import com.nix.client.common.HttpClient;
import com.nix.client.common.TCPUtil;
import com.nix.client.util.ImageUtil;
import com.nix.share.message.ImageMessage;
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
import com.nix.share.util.log.LogKit;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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

    private ClientConsumers clientConsumers;
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
    @FXML
    public void exeMessage(ImageMessage imageMessage) {
        if (imageMessage.isBye()) {
            removeClient(imageMessage);
        }else {
            addAClient(imageMessage);
        }
    }

    private void addAClient(ImageMessage imageMessage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (maxPane != null && maxPane.getId().equals(imageMessage.getRoomId() + "-" + imageMessage.getUserId())) {
                    ((ImageView)maxPane.lookup("#video")).setImage(SwingFXUtils.toFXImage(ImageUtil.messageToBufferedImage(imageMessage),new WritableImage(320,240)));
                }
                Pane pane = (Pane) otherVideoPane.lookup("#" + imageMessage.getUserId());
                ImageView view;
                if (pane == null) {
                    pane = getClientPane(imageMessage,320,240,true);
                    otherVideoPane.getChildren().add(pane);
                    LogKit.info("新增加一名用户：" + imageMessage);
                }
                view = (ImageView) pane.lookup("#video");
                view.setImage(SwingFXUtils.toFXImage(ImageUtil.messageToBufferedImage(imageMessage),new WritableImage(320,240)));
            }
        });
    }

    private void removeClient(ImageMessage imageMessage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (maxPane != null && maxPane.getId().equals(imageMessage.getRoomId() + "-" + imageMessage.getUserId())) {
                    maxStage.close();
                }
                Pane pane = (Pane) otherVideoPane.lookup("#" + imageMessage.getUserId());
                otherVideoPane.getChildren().removeAll(pane);
                LogKit.info("移除面板" + imageMessage);
            }
        });
    }

    private Pane getClientPane(ImageMessage imageMessage,double width,double height,boolean haveMax) {
        Pane pane = new Pane();
        pane.setId(imageMessage.getUserId());
        pane.setPrefWidth(width);
        pane.setPrefHeight(height + 25);
        Text text = new Text(imageMessage.getId());
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
        if (Boolean.parseBoolean(HttpClient.doGet("http://" + serverHost.getText() + "/server/" + roomId.getText() + "/" + userId.getText(),null)) && clientConsumers == null) {
            setError("用户名已存在");
            return;
        }
        if (clientConsumers == null) {
            clientConsumers = new ClientConsumers(100, 100, new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("client-consumers");
                    return t;
                }
            });
            clientConsumers.start();
        }
        if (!TCPUtil.isConnect()) {
            TCPUtil.setRoomId(roomId.getText());
            TCPUtil.setUserId(userId.getText());
            int port;
            try {
                port = Integer.parseInt(serverPort.getText());
            }catch (Exception e) {
                setError("端口错误");
                return;
            }
            if (!TCPUtil.connectTcpServer(serverHost.getText(), port)) {
                setError("服务器不存在");
                return;
            }
        }
    }



    /**
     * 新建一个最大化窗口
     * */
    private void showMaxVideo(ImageMessage message) {
        Stage stage = new Stage();
        maxStage = stage;
        stage.setTitle(message.getRoomId() + "-" + message.getUserId());
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getBounds();
        Pane pane = getClientPane(message,bounds.getMaxX(),bounds.getMaxY(),false);
        maxPane = pane;
        maxPane.setId(message.getRoomId() + "-" + message.getUserId());
        stage.setScene(new Scene(pane));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                maxPane = null;
            }
        });
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
        TCPUtil.close();
        clientConsumers.close();
    }

    public void camera(MouseEvent mouseEvent) {
        if (openCamera.getText().equals("打开摄像头")) {
            Main.main.openCameraVideo();
            openCamera.setText("关闭摄像头");
            openScreen.setText("打开屏幕分享");
            Main.main.closeScreenVideo();
        }else {
            openCamera.setText("打开摄像头");
            Main.main.closeCameraVideo();
        }
    }

    public void screen(MouseEvent mouseEvent) {
        if (openScreen.getText().equals("打开屏幕分享")) {
            Main.main.openScreenVideo();
            openScreen.setText("关闭屏幕分享");
            openCamera.setText("打开摄像头");
            Main.main.closeCameraVideo();
        }else {
            openScreen.setText("打开屏幕分享");
            Main.main.closeScreenVideo();
        }
    }
}
