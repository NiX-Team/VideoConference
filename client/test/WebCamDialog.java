import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;

import javax.swing.JButton;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IMetaData;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JTextField;

public class WebCamDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private static WebCamDialog instance = null;

    public static WebCamDialog getInstance() {
        if (instance == null) {
            instance = new WebCamDialog(null);
            instance.setModal(true);
            instance.setLocationRelativeTo(null);
        }
        return instance;
    }

    private void doPackDialog() {
        if (FORMAT_BIG.equals(usingFormat)) {
            getPanelCenter().setPreferredSize(new Dimension(1280, 1024));
        } else {
            getPanelCenter().setPreferredSize(new Dimension(800, 600));
        }
        pack();
        this.setLocationRelativeTo(null);
    }

    /**
     * @param owner
     */
    private WebCamDialog(Frame owner) {
        super(owner);
        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(521, 239));
        this.setTitle("webcamdialog");
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getPanelEast(), BorderLayout.EAST);
            jContentPane.add(getPanelCenter(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    private File recentCaptureFile = null;
    private JPanel panelEast = null;
    private JButton jButtonCaptureAndReturn = null;
    private JButton jButtonExit = null;
    private JPanel panelCenter = null;
    private JButton jButtonCapture = null;
    private JButton jButtonOpenFolder = null;
    private BufferedImage javaImage = null;

    private Thread th = null; // @jve:decl-index=0:

    public File init() {
        doOpen();
        this.setVisible(true);
        return recentCaptureFile;
    }

    private void doOpen() {
        doClose = false;
        if (th != null && th.isAlive() && container != null && container.isOpened()) {

        } else {
            th = getNewThread();
            th.start();
        }
    }

    private String deviceName = "0"; // @jve:decl-index=0:
    private String driverName = "vfwcap";
    private IContainerFormat format = null;
    private IMetaData params = null;
    private IContainer container = null;
    private int videoStreamId = -1;
    private IStreamCoder videoCoder = null;
    private IVideoResampler resampler = null;

    private boolean doClose = false;
    private boolean onMovie = false;// 开始抓数据

    private static final String FORMAT_NORMAL = "800x600"; // @jve:decl-index=0:
    private static final String FORMAT_BIG = "1280x1024"; // @jve:decl-index=0:

    private String usingFormat = FORMAT_NORMAL; // @jve:decl-index=0:

    public static int ask(String title, Object info, Object[] options, Object initOption,
                          Component basement) {
        return showOptionDialog(basement, info, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, initOption);
    }

    private static int showOptionDialog(Component parentComponent, Object message, String title,
                                        int optionType, int messageType, Icon icon, Object[] options, Object initialValue) {
        return JOptionPane.showOptionDialog(parentComponent, message, title, optionType,
                messageType, icon, options, initialValue);
    }

    private Thread getNewThread() {
        // 选择分辨率
        int rtn = ask("视频分辨率", "请选择要使用的视频分辨率，若您的计算机性能一般或无特殊需求，请选择普通分辨率。", new String[] {
                "普通分辨率800x600", "高清分辨率1280x1024" }, "普通分辨率800x600", this);
        if (rtn == 0) {
            usingFormat = FORMAT_NORMAL;
        } else {
            usingFormat = FORMAT_BIG;
        }
        pack();
        this.setLocationRelativeTo(null);

        return new Thread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                onMovie = false;
                doInfo(false, "开始连接到摄像头硬件...");

                // Let's make sure that we can actually convert video pixel
                // formats.
                if (!IVideoResampler
                        .isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
                    throw new RuntimeException(
                            "you must install the GPL version of Xuggler (with IVideoResampler support) for this demo to work");


                // Tell Xuggler about the device format
                format = IContainerFormat.make();
                if (format.setInputFormat(driverName) < 0) {
                    doInfo(true, "无法连接到摄像头驱动...");
                    throw new IllegalArgumentException("couldn't open webcam device: " + driverName);
                }
                // devices, unlike most files, need to have parameters set in
                // order for Xuggler to know how to configure them, for a
                // webcam, these parameters make sense
                params = IMetaData.make();

                params.setValue("framerate", "30/1");
                params.setValue("video_size", "320x240");
                //params.setValue("framerate", "20/1");
                //params.setValue("video_size", usingFormat);// 800x600 1280x1024

                // Create a Xuggler container object
                container = IContainer.make();

                // Open up the container
                int retval = container.open(deviceName, IContainer.Type.READ, format, false, true,params, null);

                //int retval = container.open("G:\\eclicps_workspace\\XugglerDemo\\01.wmv", IContainer.Type.READ, null);
                //System.out.println(retval);

                if (retval < 0) {
                    // This little trick converts the non
                    // friendly integer return value into
                    // a slightly more friendly object
                    // to get a human-readable error name
                    IError error = IError.make(retval);
                    doInfo(true, "无法打开摄像头设备...");
                    throw new IllegalArgumentException("could not open file: " + deviceName
                            + "; Error: " + error.getDescription());
                }

                // query how many streams the call to open found
                int numStreams = container.getNumStreams();

                // and iterate through the streams to find the first video
                // stream
                videoStreamId = -1;
                videoCoder = null;
                for (int i = 0; i < numStreams; i++) {
                    // Find the stream object
                    IStream stream = container.getStream(i);
                    // Get the pre-configured decoder that can decode this
                    // stream;
                    IStreamCoder coder = stream.getStreamCoder();

                    if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                        videoStreamId = i;
                        videoCoder = coder;
                        break;
                    }
                }

                if (videoStreamId == -1) {
                    doInfo(true, "找不到摄像头数据流...");
                    throw new RuntimeException("could not find video stream in container: "
                            + deviceName);
                }
                /*
                 * Now we have found the video stream in this file. Let's open
                 * up our decoder so it can do work.
                 */
                if (videoCoder.open() < 0) {
                    doInfo(true, "无法打开视频解码器...");
                    throw new RuntimeException("could not open video decoder for container: "
                            + deviceName);
                }
                resampler = null;
                if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
                    // if this stream is not in BGR24, we're going to need to
                    // convert it. The VideoResampler does that for us.
                    resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(),
                            IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(),
                            videoCoder.getPixelType());
                    if (resampler == null) {
                        doInfo(true, "无法生成色彩空间取样器...");
                        throw new RuntimeException("could not create color space resampler for: "
                                + deviceName);
                    }
                }
                // Now, we start walking through the container looking at each
                // packet.
                IPacket packet = IPacket.make();
                //doInfo(true, deviceName);
                doInfo(false, "开始显示视频分辨率为" + usingFormat + "...");
                doPackDialog();
                onMovie = true;
                all: while (container.readNextPacket(packet) >= 0) {
                    // Now we have a packet, let's see if it belongs to our
                    // video stream
                    if (packet.getStreamIndex() == videoStreamId) {
                        // We allocate a new picture to get the data out of
                        // Xuggler
                        IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
                                videoCoder.getWidth(), videoCoder.getHeight());
                        int offset = 0;
                        while (offset < packet.getSize()) {
                            // Now, we decode the video, checking for any
                            // errors.
                            int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
                            if (bytesDecoded < 0) {
                                doInfo(true, "视频解码错误...");
                                onMovie = false;
                                throw new RuntimeException("got error decoding video in: "
                                        + deviceName);
                            }
                            offset += bytesDecoded;
                            // Some decoders will consume data in a packet, but
                            // will not be able to construct a full video
                            // picture yet. Therefore you should always check if
                            // you got a complete picture from the decoder
                            if (picture.isComplete()) {
                                IVideoPicture newPic = picture;
                                // If the resampler is not null, that means we
                                // didn't get the video in BGR24 format and need
                                // to convert it into BGR24 format.
                                if (resampler != null) {
                                    // we must resample
                                    newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),
                                            picture.getWidth(), picture.getHeight());
                                    if (resampler.resample(newPic, picture) < 0) {
                                        doInfo(true, "视频重取样错误...");
                                        onMovie = false;
                                        throw new RuntimeException(
                                                "could not resample video from: " + deviceName);
                                    }
                                }
                                if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                                    doInfo(true, "无法解码BGR24数据...");
                                    onMovie = false;
                                    throw new RuntimeException(
                                            "could not decode video as BGR 24 bit data in: "
                                                    + deviceName);
                                }

                                // Convert the BGR24 to an Java buffered image
                                javaImage = Utils.videoPictureToImage(newPic);
                                // and display it on the Java Swing window
                                if (!updateJavaWindow(javaImage)) {
                                    // break all;
                                }
                                if (doClose) {// 退出
                                    doClose = false;
                                    break all;
                                }
                            }
                        }
                    } else {
                        // This packet isn't part of our video stream, so we
                        // just silently drop it.
                        do {
                        } while (false);
                    }
                }
                onMovie = false;
                doInfo(false, "开始关闭摄像头...");
                /*
                 * Technically since we're exiting anyway, these will be cleaned
                 * up by the garbage collector... but because we're nice people
                 * and want to be invited places for Christmas, we're going to
                 * show how to clean up.
                 */
                if (videoCoder != null) {
                    videoCoder.close();
                    videoCoder = null;
                }
                if (container != null) {
                    container.close();
                    container = null;
                }
                doInfo(false, "摄像头关闭完成...");
            }
        });
    }

    private boolean updateJavaWindow(BufferedImage javaImage) {
        if (getPanelCenter() == null || getPanelCenter().getGraphics() == null) {
            return false;
        }
        return getPanelCenter().getGraphics().drawImage(javaImage, 0, 0, null);
    }

    /**
     * This method initializes panelEast
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelEast() {
        if (panelEast == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(6);
            panelEast = new JPanel();
            panelEast.setLayout(gridLayout);
            panelEast.add(getJButtonCaptureAndReturn(), null);
            panelEast.add(getJButtonCapture(), null);
            panelEast.add(getJButtonOpenFolder(), null);
            panelEast.add(getJButtonExit(), null);
            panelEast.add(getJButton(), null);
            panelEast.add(getJtfInfo(), null);
        }
        return panelEast;
    }

    /**
     * This method initializes jButtonCaptureAndReturn
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonCaptureAndReturn() {
        if (jButtonCaptureAndReturn == null) {
            jButtonCaptureAndReturn = new JButton("webcam_captureandreturn");
            jButtonCaptureAndReturn.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    recentCaptureFile = doCaptureFile();
                    if (recentCaptureFile != null) {
                        dispose();
                    }
                }
            });
        }
        return jButtonCaptureAndReturn;
    }

    /**
     * This method initializes jButtonExit
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonExit() {
        if (jButtonExit == null) {
            jButtonExit = new JButton("exit");
            jButtonExit.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    recentCaptureFile = null;
                    dispose();
                }
            });
        }
        return jButtonExit;
    }

    /**
     * This method initializes panelCenter
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelCenter() {
        if (panelCenter == null) {
            panelCenter = new JPanel();
            panelCenter.setLayout(new BorderLayout());
        }
        return panelCenter;
    }

    /**
     * This method initializes jButtonCapture
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonCapture() {
        if (jButtonCapture == null) {
            jButtonCapture = new JButton("webcam_capture");
            jButtonCapture.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    recentCaptureFile = doCaptureFile();
                }
            });
        }
        return jButtonCapture;
    }

    /**
     * 抓图且存为自动的时间文件名
     *
     * @return
     */
    private File doCaptureFile() {
        if (javaImage == null) {
            return null;
        }
        File rtn = doGetCurrentFile();
        if (rtn.exists()) {
            return null;
        }
        BufferedImage bi = javaImage;
        Iterator writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();
        try {
            ImageOutputStream ios = ImageIO.createImageOutputStream(rtn);
            writer.setOutput(ios);
            writer.write(bi);
            ios.close();
        } catch (java.io.IOException io) {
            System.out.println("IOException");
        }
        return rtn;
    }

    /**
     * 20120221 按时间生成文件名
     *
     * @return
     */
    private File doGetCurrentFile() {
        String now = String.valueOf(System.currentTimeMillis());
        File rtn = new File("c:\\" + now + ".jpg");
        int loop = 0;
        while (rtn.exists()) {
            rtn = new File("c:\\" + now + ('A' + loop) + ".jpg");
            loop++;
        }
        return rtn;
    }

    /**
     * This method initializes jButtonOpenFolder
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonOpenFolder() {
        if (jButtonOpenFolder == null) {
            jButtonOpenFolder = new JButton("webcam_openfolder");
            jButtonOpenFolder.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        Desktop.getDesktop().open(new File("c:\\"));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
        return jButtonOpenFolder;
    }

    private JButton jButton = null;
    private JTextField jtfInfo = null;

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton("resetwebcam");
            jButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (onMovie == true) {
                        doClose = true;
                        th = null;
                        // 20120829 等待关闭完成
                        while (container.isOpened()) {
                        }
                        // 20120830 且重新打开
                        doOpen();
                    } else {
                        error(null, "errnoresponsefromwebcam", null);
                    }
                }
            });
        }
        return jButton;
    }

    public boolean error(String title, Object info, Component basement) {
        return showOptionDialog(basement, info, title, JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE, null, null, null) == 0;
    }

    public void doInfo(boolean alarm, String msg) {
        if (alarm) {
            getJtfInfo().setForeground(Color.red);
        } else {
            getJtfInfo().setForeground(Color.blue);
        }
        getJtfInfo().setText(msg);
    }

    /**
     * This method initializes jtfInfo
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJtfInfo() {
        if (jtfInfo == null) {
            jtfInfo = new JTextField();
            jtfInfo.setEditable(false);
        }
        return jtfInfo;
    }

} // @jve:decl-index=0:visual-constraint="10,10"