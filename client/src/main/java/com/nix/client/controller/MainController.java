package com.nix.client.controller;

import com.nix.client.util.ImageUtil;
import com.nix.message.ImageMessage;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

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
    public void setImage(Image image) {
        video_box.setImage(image);
    }
    @FXML
    public void setAFriend(ImageMessage imageMessage) {
        ImageView view = (ImageView) otherVideoPane.lookup("#" + imageMessage.getUserId());
        if (view == null) {
            otherVideoPane.getChildren().add(new ImageView());
        }
        view.setImage(SwingFXUtils.toFXImage(ImageUtil.messageToBufferedImage(imageMessage),new WritableImage(100,100)));
    }
}
