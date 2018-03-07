package com.nix.client.controller;

import com.nix.client.Main;
import com.nix.client.common.ClientConsumers;
import com.nix.client.common.TcpUtil;
import com.nix.client.util.ImageUtil;
import com.nix.message.ImageMessage;
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
import util.log.LogKit;

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
                    view.setFitWidth(600);
                    view.setFitHeight(400);
                    otherVideoPane.getChildren().add(view);
                    LogKit.info("新增加一名用户：" + imageMessage);
                }
                view.setImage(SwingFXUtils.toFXImage(ImageUtil.messageToBufferedImage(imageMessage),new WritableImage(100,100)));
            }
        });
    }

    public void sign(MouseEvent mouseEvent) {
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
        TcpUtil.connectServer();
        if (boolOpenCamera.isSelected()) {
            Main.main.openCameraVideo();
        }
        if (boolOpenScreen.isSelected()) {
            Main.main.openScreenVideo();
        }
    }
}
