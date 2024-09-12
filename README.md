# VisualCampus
Summer School Professional Skills Practical Training Major Assignment (incomplete)

后端使用intellij IDEA Java进行编程，采用Maven架构。

理论部署步骤：

1、部署VisualCampus文件夹

2、数据库使用MySQL运行于本地（数据库建议使用Navicat进行导入，现在的sql文件已更新）

3、修改QAtxt文件中提示的需修改的数据库信息和路径信息后即可运行

各控件版本如下：

	IntelliJ IDEA 2024.2.0.2
 
	MySQL 8.4.2
 
	jdk 21

后端代码已整合完成，还剩下极少数功能未测试，可能有部分小bug未更改

现有评教脚本但未与前端对接，脚本中的userId和password需在项目文件夹（VisualCampus）外建立credentials.txt文件并将一卡通号和密码按如下格式输入即可（默认相对路径为项目文件夹外的credentials.txt，源代码中路径为"../credentials.txt"）

credentials.txt文件中格式：

userId=yourUserId

password=yourPassword
