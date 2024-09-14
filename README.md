# VisualCampus
Summer School Professional Skills Practical Training Major Assignment (Completed, front-end code and exe not added yet)

暑期学校专业技能实训大作业（已完成，现已添加前端代码和前端exe）

后端使用intellij IDEA Java进行编程，采用Maven架构。

后端（服务器端）理论部署步骤：

1、部署VisualCampus文件夹

2、数据库使用MySQL运行于本地（数据库建议使用Navicat进行导入，现在的sql文件已更新）

3、修改QAtxt文件中提示的需修改的数据库信息和路径信息后即可运行

各控件版本如下：

	IntelliJ IDEA 2024.2.0.2
 
	MySQL 8.4.2
 
	jdk 21

后端代码已整合完成，展示用功能已测试，展示时基本无bug出现，可能有部分小bug未发现

现有评教脚本已经与前端对接完成，不过为了防止隐私信息泄露这里脚本并未接收前端传回的数据，固定在评教时自动给王世杰老师的专业技能实训课程进行评教，且评教分数固定为10，但是脚本接口已给出，可以根据需求进行修改

脚本中的userId和password需在项目文件夹（VisualCampus）外建立credentials.txt文件并将一卡通号和密码按如下格式输入即可（默认相对路径为项目文件夹外的credentials.txt，源代码中路径为"../credentials.txt"）

credentials.txt文件中格式：

	userId=yourUserId

	password=yourPassword
