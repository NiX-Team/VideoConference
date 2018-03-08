package com.nix.client.controller;

import com.nix.client.Main;
import com.nix.client.common.ClientConsumers;
import com.nix.client.common.HttpsClient;
import com.nix.client.common.TcpUtil;
import com.nix.client.util.ImageUtil;
import com.nix.share.message.ImageMessage;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import com.nix.share.util.log.LogKit;
import javafx.scene.text.Text;

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
    private CheckBox boolOpenCamera;
    @FXML
    private CheckBox boolOpenScreen;
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
    @FXML
    public void setImage(Image image) {
        video_box.setImage(image);
    }
    @FXML
    public void setAFriend(ImageMessage imageMessage) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ImageView view = (ImageView) otherVideoPane.lookup("#" + imageMessage.getUserId());
                if (view == null) {
                    view = new ImageView();
                    view.setId(imageMessage.getUserId());
                    view.setFitWidth(1366);
                    view.setFitHeight(736);
                    otherVideoPane.getChildren().add(view);
                    LogKit.info("新增加一名用户：" + imageMessage);
                }
                view.setImage(SwingFXUtils.toFXImage(ImageUtil.messageToBufferedImage(imageMessage),new WritableImage(100,100)));
            }
        });
    }

    public void sign(MouseEvent mouseEvent) {
        if (roomId.getText() == null || roomId.getText().isEmpty()) {
            setError("房间号不能为空");
            return;
        }
        if (userId.getText() == null || userId.getText().isEmpty()) {
            setError("用户id不能为空");
            return;
        }
        if (Boolean.parseBoolean(HttpsClient.doGet("http://" + serverHost.getText() + "/server/" + roomId + "/" + userId,null))) {
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
        TcpUtil.setRoomId(roomId.getText());
        TcpUtil.setUserId(userId.getText());
        int port;
        try {
            port = Integer.parseInt(serverPort.getText());
        }catch (Exception e) {
            setError("端口错误");
            return;
        }
        if (!TcpUtil.connectServer(serverHost.getText(), port)) {
            setError("服务器不存在");
            return;
        }
        if (boolOpenCamera.isSelected()) {
            Main.main.openCameraVideo();
        }
        if (boolOpenScreen.isSelected()) {
            Main.main.openScreenVideo();
        }
    }

    public void setError(String errorMsg) {
        error.setText(errorMsg);
    }
    public void close() {
        TcpUtil.close();
        clientConsumers.close();
    }
}
