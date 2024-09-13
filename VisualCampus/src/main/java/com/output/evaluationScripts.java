package com.output;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Set;

public class evaluationScripts {

    // 登录并进行评教的主方法
    public static void studentEvaluation(WebDriver driver, WebDriverWait wait, String userId, String password) {
        try {
            // 打开登录页面
            driver.get("https://ehall.seu.edu.cn/new/index.html");

            // 点击登录按钮，跳转到登录页面
            WebElement loginButton1 = wait.until(ExpectedConditions.elementToBeClickable(By.id("ampLoginBtn")));
            loginButton1.click();

            // 等待并填写用户名
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder='一卡通号/唯一ID']")));
            usernameField.sendKeys(userId);

            // 等待并填写密码
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='password']")));
            passwordField.sendKeys(password);

            // 点击登录按钮
            WebElement loginButton2 = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.login-button-pc")));
            loginButton2.click();

            // 进入评教页面
            WebElement targetDiv1 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'widget-recommend-item') and .//div[@title='本科评教']]")));
            targetDiv1.click();

            // 点击进入详情
            WebElement targetDiv2 = wait.until(ExpectedConditions.elementToBeClickable(By.id("ampDetailEnter")));
            targetDiv2.click();

            // 切换窗口
            Set<String> windowHandles = driver.getWindowHandles();
            for (String windowHandle : windowHandles) {
                driver.switchTo().window(windowHandle);
            }

            // 选择评教类型
            WebElement targetDiv = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@type='dwpj' and contains(@class, 'top-card-item')]")));
            targetDiv.click();

            // 点击立即评教按钮
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

            // 保存评教结果
            WebElement saveButton = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("保存")));
            saveButton.click();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 确保浏览器在所有操作后关闭
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Error occurred while closing browser: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 根据标题选择评分
    public static void selectScoreBasedOnTitle(WebDriver driver, WebDriverWait wait, String titleText, int score) {
        // 找到标题
        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@class='sc-panel-thingNoImg-1-title wjzb-card-title' and contains(text(), '" + titleText + "')]")));

        // 构造评分按钮的 XPath
        String xpath = String.format("//div[@class='fzItem bh-pull-left' and @data-x-fz='%d']", score);
        WebElement scoreButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        scoreButton.click();
    }
}
