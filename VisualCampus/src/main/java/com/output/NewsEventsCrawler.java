package com.output;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NewsEventsCrawler {
    public static void main(String[] args) {
        try {
            // 基本 URL
            String baseUrl = "http://www.lib.seu.edu.cn";
            // 获取网页内容
            String url = baseUrl; // 网页 URL
            Document doc = Jsoup.connect(url).get();

            // 选择所有 class 为 list-item 的 li 元素
            Elements listItems = doc.select("div.news-events ul li.list-item");

            // 遍历每个 list-item
            for (Element listItem : listItems) {
                // 获取时间
                String time = listItem.select("span.time").text();

                // 获取标题和链接
                Element linkElement = listItem.select("a.name").first();
                String title = "";
                if (linkElement != null) {
                    title = linkElement.attr("title");
                    if (title.isEmpty()) {
                        title = linkElement.text(); // 如果 title 属性为空，则使用文本内容
                    }
                } else {
                    title = "无标题"; // 如果未找到 a.name 元素，设置默认标题
                }

                String relativeLink = linkElement != null ? linkElement.attr("href") : "";
                String absoluteLink = "";

                // 判断是否为相对路径，并转换为绝对路径
                if (!relativeLink.equals("javascript:void(0);")) {
                    if (relativeLink.startsWith("http") || relativeLink.startsWith("https")) {
                        absoluteLink = relativeLink;
                    } else {
                        absoluteLink = baseUrl + relativeLink; // 拼接绝对链接
                    }
                }

                // 输出信息
                System.out.println("时间: " + time);
                System.out.println("标题: " + title);
                if (!absoluteLink.isEmpty()) {
                    System.out.println("链接: " + absoluteLink);
                }
                System.out.println("---------------------------");
            }

            // 同样处理 action-events 部分
            Elements actionItems = doc.select("div.action-events ul li.list-item");

            for (Element listItem : actionItems) {
                // 获取时间
                String time = listItem.select("span.time").text();

                // 获取标题和链接
                Element linkElement = listItem.select("a.name").first();
                String title = "";
                if (linkElement != null) {
                    title = linkElement.attr("title");
                    if (title.isEmpty()) {
                        title = linkElement.text(); // 如果 title 属性为空，则使用文本内容
                    }
                } else {
                    title = "无标题"; // 如果未找到 a.name 元素，设置默认标题
                }

                String relativeLink = linkElement != null ? linkElement.attr("href") : "";
                String absoluteLink = "";

                // 判断是否为相对路径，并转换为绝对路径
                if (!relativeLink.equals("javascript:void(0);")) {
                    if (relativeLink.startsWith("http") || relativeLink.startsWith("https")) {
                        absoluteLink = relativeLink;
                    } else {
                        absoluteLink = baseUrl + relativeLink; // 拼接绝对链接
                    }
                }

                // 输出信息
                System.out.println("时间: " + time);
                System.out.println("标题: " + title);
                if (!absoluteLink.isEmpty()) {
                    System.out.println("链接: " + absoluteLink);
                }
                System.out.println("---------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




