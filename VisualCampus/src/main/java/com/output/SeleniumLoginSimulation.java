package com.output;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class SeleniumLoginSimulation {

    public static String[] readCredentials(String filePath) throws IOException {
        String userId = "";
        String password = "";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    if (parts[0].equals("userId")) {
                        userId = parts[1];
                    } else if (parts[0].equals("password")) {
                        password = parts[1];
                    }
                }
            }
        }

        return new String[]{userId, password};
    }

    public static void selectScoreBasedOnTitle(WebDriver driver, WebDriverWait wait, String titleText, int score) {
        // 1. 找到包含指定标题的元素
        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='sc-panel-thingNoImg-1-title wjzb-card-title' and contains(text(), '" + titleText + "')]")));

        // 2. 根据指定的评分值构建 XPath
        String xpath = String.format("//div[@class='fzItem bh-pull-left' and @data-x-fz='%d']", score);

        // 3. 等待并点击对应的评分按钮
        WebElement scoreButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        scoreButton.click();
    }
    public static void fillTextAreaBasedOnTitle(WebDriver driver, WebDriverWait wait, String titleText, String textContent) {
        // 1. 找到包含指定标题的元素
        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='sc-panel-thingNoImg-1-title wjzb-card-title' and contains(text(), '" + titleText + "')]")));

        // 2. 获取标题对应的文本区域元素
        WebElement textArea = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='sc-panel-thingNoImg-1-title wjzb-card-title' and contains(text(), '" + titleText + "')]/following-sibling::div//textarea")));

        // 3. 清空并输入文本内容
        textArea.clear();
        textArea.sendKeys(textContent);
    }


    public static void main(String[] args) throws IOException {
        // 设置 FirefoxDriver 的路径
        System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\Java\\jdk-21\\bin\\geckodriver.exe");

        // 初始化 Firefox 浏览器
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:130.0) Gecko/20100101 Firefox/130.0"); // 需要在开发者模式中查看网络中相关信息替换
        options.addPreference("javascript.options.showInConsole", false);  // 禁用控制台的 JavaScript 警告
        options.addPreference("dom.max_script_run_time", 0);  // 禁用脚本运行时间限制
        WebDriver driver = new FirefoxDriver(options);

        // 读取 credentials.txt 文件中的 userId 和 password
        String[] credentials = readCredentials("../credentials.txt"); // 确保路径正确
        String userId = credentials[0];
        String password = credentials[1];

        try {
            // 打开登录页面
            driver.get("https://ehall.seu.edu.cn/new/index.html");

            // 初始化 WebDriverWait
            WebDriverWait wait = new WebDriverWait(driver, 20); // 等待最多 20 秒

            WebElement loginButton1 = wait.until(ExpectedConditions.elementToBeClickable(By.id("ampLoginBtn")));
            loginButton1.click();

            // 等待并找到并填写用户名
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='一卡通号/唯一ID']")));
            usernameField.sendKeys(userId);

            // 等待并找到并填写密码
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='password']")));
            passwordField.sendKeys(password);

            // 等待并点击登录按钮
            WebElement loginButton2 = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.login-button-pc")));
            loginButton2.click();


            WebElement targetDiv1 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'widget-recommend-item') and .//div[@title='本科评教']]")));
            targetDiv1.click();

            // 等待并点击具有 id 为 ampDetailEnter 的 <div> 元素
            WebElement targetDiv2 = wait.until(ExpectedConditions.elementToBeClickable(By.id("ampDetailEnter")));
            targetDiv2.click();


            Set<String> windowHandles = driver.getWindowHandles();
            for (String windowHandle : windowHandles) {
                driver.switchTo().window(windowHandle);  // 切换到新窗口
            }

            WebElement targetDiv = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@type='dwpj' and contains(@class, 'top-card-item')]")));
            targetDiv.click();


            // 等待并点击具有 class 'card-btn blue' 且 data-action 为 'xspj立即评教' 的 <div> 元素
            WebElement evaluateButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@class='card-btn blue' and @data-action='xspj立即评教']")));
            evaluateButton.click();

            selectScoreBasedOnTitle(driver, wait, "1、我对这门课程和老师的总体评价", 10);
            selectScoreBasedOnTitle(driver, wait, "2、老师课堂无不良言论，价值导向积极，体现了“立德树人”的师者风范。", 10);
            selectScoreBasedOnTitle(driver, wait, "3、老师能够让我了解课程的价值，激发我的学习热情。", 10);
            selectScoreBasedOnTitle(driver, wait, "4、老师能让我明白每次课程的具体学习目标和要求。", 10);
            selectScoreBasedOnTitle(driver, wait, "5、老师教学能够理论联系实际，教学内容具有适当的挑战性。", 10);
            selectScoreBasedOnTitle(driver, wait, "6、我认为老师讲授思路清楚，重点突出，层次分明。", 10);
            selectScoreBasedOnTitle(driver, wait, "7、我认为老师备课充分，为我们提供了丰富的学习资料。", 10);
            selectScoreBasedOnTitle(driver, wait, "8、老师能根据大多数同学的学习情况，合理调整教学安排和进度。", 10);
            selectScoreBasedOnTitle(driver, wait, "9、老师对我们的问题（包括作业和考核）能够给予及时、有帮助的反馈。", 10);
            selectScoreBasedOnTitle(driver, wait, "10、我认为课程的考核评价方式能够反映我的学习成效。", 10);
            selectScoreBasedOnTitle(driver, wait, "11、我认为我能够达到本课程的教学目标，在学习中有所收获。", 10);
//            fillTextAreaBasedOnTitle(driver, wait, "12、对于老师的这门课，我有些话想说", "老师的课程内容很有帮助。");


            WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("保存")));
            saveButton.click();


        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
//        } finally {
//            // 确保浏览器在所有操作后关闭
//           try {
//               driver.quit();
//            } catch (Exception e) {
//                System.out.println("Error occurred while closing browser: " + e.getMessage());
//                e.printStackTrace();
//            }
        }
    }
}
