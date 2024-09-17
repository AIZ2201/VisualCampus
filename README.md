# 🏫 SEU-SummerSchool-VisualCampus 🏫
Summer School Professional Skills Practical Training Major Assignment (Completed, now adding front-end code and front-end exe)

本项目为东南大学 2022 级计算机科学与技术专业暑期学校专业技能实训项目VisualCampus（已完成，现已添加前端代码和前端exe）

本项目由以下 5 位本科生完成，欢迎访问他们的 GitHub 主页（还有一位暂未加入）

[AIZ2207](https://github.com/AIZ2201),
[moveon](https://github.com/hxk77882),
[Yishu Wang](https://github.com/seuwestbrrook),
[lee](https://github.com/leee040606)

## ⚙️ 快速开始

前端使用intellij IDEA，使用Kotlin语言编程

后端使用intellij IDEA Java进行编程，采用Maven架构

### 🔑 Requirements

- `JDK >= 21`
- `MySQL >= 8.4.2`

编写代码时各控件版本如下：

	IntelliJ IDEA 2024.2.0.2
 
	MySQL 8.4.2
 
	jdk 21

### 🔌 启动方法

前端可直接运行demo3中的exe文件

前端源代码见demo3文件夹

后端（服务器端）理论部署步骤：

1、部署VisualCampus文件夹

2、数据库使用MySQL运行于本地（数据库建议使用Navicat进行导入，现在的sql文件已更新）

3、修改QAtxt文件中提示的需修改的数据库信息和路径信息后即可运行

注：geckodriver是火狐浏览器脚本相关驱动，配置有些麻烦，可能导致后端报错，可自行删除几个脚本相关文件和评教页面中的脚步操作

## 📑 相关文档

javadoc文件见doc文件夹

课程相关文件也在doc文件夹中

## 📌 其他说明

前端说明还不算完全完整，待补充

后端代码已整合完成，展示用功能已测试，展示时基本无bug出现，可能有部分小bug未发现

- `VisualCampusQA为编程中出现的部分问题和解决方法`
- `vcampus.sql为数据库sql文件`
- `另外两个txt为其文件名所示内容`

现有评教脚本已经与前端对接完成，不过为了防止隐私信息泄露这里脚本并未接收前端传回的数据，固定在评教时自动给王世杰老师的专业技能实训课程进行评教，且评教分数固定为10，但是脚本接口已给出，可以根据需求进行修改

脚本中的userId和password需在项目文件夹（VisualCampus）外建立credentials.txt文件并将一卡通号和密码按如下格式输入即可（默认相对路径为项目文件夹外的credentials.txt，源代码中路径为"../credentials.txt"）

credentials.txt文件中格式：

	userId=yourUserId

	password=yourPassword
