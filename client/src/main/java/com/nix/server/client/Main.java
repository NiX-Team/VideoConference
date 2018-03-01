package com.nix.server.client;

import com.nix.server.client.common.VideoThread;
import javafx.application.Application;
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

/**
 * @author 11723
 */
public class Main extends Application {

    private Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("controller/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
        openVideo();
    }

    /**
     * 显示自己摄像头视频
     * */
    private void openVideo() {
        final ImageView imageView = (ImageView) getNodeById(root,"video_box");
        VideoThread.start(new VideoThread.Exe() {
            @Override
            public void exeImage(BufferedImage javaImage) {
                Image image = new Image("");
                imageView.setImage(SwingFXUtils.toFXImage(javaImage,new WritableImage(100,100)));
            }
        });


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
