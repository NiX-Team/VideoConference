package com.nix.video.client.common;

import com.xuggle.xuggler.*;

import java.awt.image.BufferedImage;

/**
 * @author 11723
 */
public class CameraVideoThread extends VideoThread{

    public static final String DEFAULT_DEVICE_NAME = "vfwcap";
    public static final String DEFAULT_VIDEO_SIZE = "320x240";
    public static final String DEFAULT_FRAME = "1/60";
    private static String deviceName;
    private static String videoSize;
    private static String frame;

    public CameraVideoThread(Exe exe) {
        super(exe);
        deviceName = DEFAULT_DEVICE_NAME;
        videoSize = DEFAULT_VIDEO_SIZE;
        frame = DEFAULT_FRAME;
    }

    @Override
    protected void setThread() {
        Thread thread = new Thread(){
            @Override
            public void run() {
                // Let's make sure that we can actually convert video pixel
                // formats.
                if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
                    throw new RuntimeException("you must install the GPL version of Xuggler (with IVideoResampler support) for this demo to work");
                }
                // Tell Xuggler about the device format
                IContainerFormat format = IContainerFormat.make();
                if (format.setInputFormat(deviceName) < 0) {
                    throw new IllegalArgumentException("couldn't open webcam device: " + deviceName);
                }
                // devices, unlike most files, need to have parameters set in
                // order for Xuggler to know how to configure them, for a
                // webcam, these parameters make sense
                IMetaData params = IMetaData.make();
                params.setValue("framerate", frame);
                params.setValue("video_size", videoSize);
                // Create a Xuggler container object
                IContainer container = IContainer.make();
                // Open up the container
                int retval = container.open(deviceName, IContainer.Type.READ, format, false, true,params, null);
                if (retval < 0) {
                    // This little trick converts the non
                    // friendly integer return value into
                    // a slightly more friendly object
                    // to get a human-readable error name
                    IError error = IError.make(retval);
                    throw new IllegalArgumentException("无法打开摄像头设备...: " + deviceName
                            + "; Error: " + error.getDescription());
                }

                // query how many streams the call to open found
                int numStreams = container.getNumStreams();

                // and iterate through the streams to find the first video
                // stream
                int videoStreamId = -1;
                IStreamCoder videoCoder = null;
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
                    throw new RuntimeException("找不到摄像头数据流...: "
                            + deviceName);
                }
                /*
                 * Now we have found the video stream in this file. Let's open
                 * up our decoder so it can do work.
                 */
                if (videoCoder.open() < 0) {
                    throw new RuntimeException("无法打开视频解码器...: "
                            + deviceName);
                }
                IVideoResampler resampler = null;
                if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
                    // if this stream is not in BGR24, we're going to need to
                    // convert it. The VideoResampler does that for us.
                    resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(),
                            IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(),
                            videoCoder.getPixelType());
                    if (resampler == null) {
                        throw new RuntimeException("无法生成色彩空间取样器...: "
                                + deviceName);
                    }
                }
                // Now, we start walking through the container looking at each
                // packet.
                IPacket packet = IPacket.make();
                all: while (container.readNextPacket(packet) >= 0 && !isInterrupted()) {
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
                                throw new RuntimeException("视频解码错误...: "
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
                                        throw new RuntimeException(
                                                "视频重取样错误...:" + deviceName);
                                    }
                                }
                                if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                                    throw new RuntimeException(
                                            "无法解码BGR24数据...:"
                                                    + deviceName);
                                }
                                // Convert the BGR24 to an Java buffered image
                                BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                                try {
                                    exe.exeImage(javaImage);
                                } catch (Exception e) {
                                }
                            }
                        }
                    } else {

                    }
                }
                System.out.println("开始关闭摄像头...");
                /*
                 * Technically since we're exiting anyway, these will be cleaned
                 * up by the garbage collector... but because we're nice people
                 * and want to be invited places for Christmas, we're going to
                 * show how to clean up.
                 */
                if (videoCoder != null) {
                    videoCoder.close();
                }
                if (container != null) {
                    container.close();
                }
                System.out.println("摄像头关闭完成...");
            }
        };
        setThread(thread);
    }
}
