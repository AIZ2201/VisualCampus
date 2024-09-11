package com.output;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
    public static void main(String[] args) {
        try {
            // 获取网页内容
            String url = "http://www.lib.seu.edu.cn/";
            Document doc = Jsoup.connect(url).get();

            // 选择所有 class 为 list-item 的 li 元素
            Elements listItems = doc.select("li.list-item");

            // 遍历每个 list-item
            for (Element listItem : listItems) {
                // 获取标题
                String title = listItem.select("a.name").attr("title");

                // 获取时间
                String time = listItem.select("span.time").text();

                // 获取地点
                String location = listItem.select("span.offline").text();

                // 输出信息
                System.out.println("标题: " + title);
                System.out.println("时间: " + time);
                System.out.println("地点: " + location);
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
