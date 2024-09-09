package com.output;

import org.opencv.core.*;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.LinkedList;
import java.util.List;

public class FacialVerification {
    static {
        // 设置java.library.path
        System.setProperty("java.library.path", "D:\\AIZ\\opencv\\build\\java\\x64");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // 加载OpenCV库
    }

    public static void main(String[] args) {
        String imagePath1 = "D:\\AIZ\\5EDC7FF79000B99C6CF1A4A35F5D93E6.jpg"; // 第一张图片的路径
        String imagePath2 = "D:\\AIZ\\37851D600A52299FEC318FC62CA7AF0D.jpg"; // 第二张图片的路径 "D:\\AIZ\\EF79B5938B387259975014F0B1A40133.jpg"
        //"D:\\AIZ\\37851D600A52299FEC318FC62CA7AF0D.jpg"

        // 1. 初始化人脸检测分类器
        CascadeClassifier faceDetector = new CascadeClassifier("D:/AIZ/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");

        // 2. 读取两张图片
        Mat image1 = Imgcodecs.imread(imagePath1);
        Mat image2 = Imgcodecs.imread(imagePath2);

        if (image1.empty() || image2.empty()) {
            System.out.println("无法加载图片，请检查路径");
            return;
        }

        // 3. 检测并裁剪两张图片中的人脸
        Mat face1 = detectAndCropFace(image1, faceDetector);
        Mat face2 = detectAndCropFace(image2, faceDetector);

        if (face1 == null || face2 == null) {
            System.out.println("未检测到人脸！");
            return;
        }

        // 4. 使用ORB特征提取和匹配进行人脸验证
        boolean isMatch = compareFaces(face1, face2);

        // 5. 输出匹配结果
        if (isMatch) {
            System.out.println("匹配成功，图片中的人脸相似！");
        } else {
            System.out.println("匹配失败，图片中的人脸不相似！");
        }
    }

    // 检测并裁剪人脸区域
    private static Mat detectAndCropFace(Mat image, CascadeClassifier faceDetector) {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faceDetections, 1.05, 3); // 调整检测的参数

        Rect[] facesArray = faceDetections.toArray();
        if (facesArray.length == 0) {
            return null; // 未检测到人脸
        }

        return new Mat(image, facesArray[0]); // 裁剪出第一个检测到的人脸区域
    }

    // 使用ORB算法提取特征并比较人脸
    private static boolean compareFaces(Mat face1, Mat face2) {
        ORB orb = ORB.create();

        // 特征点和描述符
        MatOfKeyPoint keyPoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        // 提取特征
        orb.detectAndCompute(face1, new Mat(), keyPoints1, descriptors1);
        orb.detectAndCompute(face2, new Mat(), keyPoints2, descriptors2);

        // 匹配特征
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors1, descriptors2, matches);

        List<DMatch> matchesList = matches.toList();
        double maxDist = 0;
        double minDist = 100;

        // 找出最小和最大的距离
        for (DMatch match : matchesList) {
            double dist = match.distance;
            if (dist < minDist) minDist = dist;
            if (dist > maxDist) maxDist = dist;
        }

        // 放宽距离的限制，使匹配标准更加宽松
        double threshold = 1.0 * minDist; // 放宽匹配条件

        // 仅考虑“好”的匹配
        LinkedList<DMatch> goodMatches = new LinkedList<>();
        for (DMatch match : matchesList) {
            if (match.distance <= threshold) { // 这里使用更宽松的阈值
                goodMatches.add(match);
            }
        }

        // 如果匹配特征点数量超过阈值，则认为是相似的人脸
        return goodMatches.size() > 4; // 调整阈值
    }
}
