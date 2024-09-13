package com.output;

import entity.*;
import DataSQL.DataAccessObject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class library_page {

    public JSONObject library_getUser(JSONObject user) {
        JSONObject response = new JSONObject();
        response.put("status", "failed");
        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 查询用户信息
            String userQuery = "SELECT * FROM libraryuser WHERE userId = ?";
            ResultSet libraryuser = dataAccessObject.executeQuery(userQuery, user.getInt("cardNumber"));

            LibraryUser libraryUser = null;
            if (libraryuser.next()) {
                libraryUser = new LibraryUser();
                libraryUser.setUserId(libraryuser.getInt("userId"));
                libraryUser.setUserName(libraryuser.getString("userName"));
                libraryUser.setUserMaxBorrowNum(libraryuser.getInt("userMaxBorrowNum"));
                libraryUser.setUserBorrowedNum(libraryuser.getInt("userBorrowedNum"));
                libraryUser.setUserStatus(libraryuser.getString("userStatus"));
            }

            if (libraryUser == null) {
                response.put("message", "User not found.");
                return response;
            }




            // 查询用户借阅信息
            String tranQuery = "SELECT * FROM librarytran WHERE tranUserId = ?";
            ResultSet librarytran = dataAccessObject.executeQuery(tranQuery, libraryUser.getUserId());

            LinkedList<LibraryTran> userBorrowed = new LinkedList<>();

            JSONArray transArray = new JSONArray();
            while (librarytran.next()) {
                LibraryTran libraryTran = new LibraryTran();
                JSONObject transObject = new JSONObject();
                libraryTran.setTranId(librarytran.getString("tranId"));
                libraryTran.setTranBorrowTime(librarytran.getDate("tranBorrowTime"));
                libraryTran.setTranDueTime(librarytran.getDate("tranDueTime"));
                libraryTran.setTranBookId(librarytran.getString("tranBookId"));
                libraryTran.setTranUserId(librarytran.getInt("tranUserId"));


                transObject.put("tranId", librarytran.getString("tranId"));
                transObject.put("tranBorrowTime", librarytran.getString("tranBorrowTime"));
                transObject.put("tranUserId", librarytran.getString("tranUserId"));
                transObject.put("tranDueTime", librarytran.getString("tranDueTime"));
                transObject.put("tranBookId", librarytran.getString("tranBookId"));
                transObject.put("tranIsbn", librarytran.getString("tranIsbn"));

                //根据bookid查询bookname
                String BooknameSql = "SELECT bookName FROM librarybook WHERE bookId = ?";
                ResultSet  bookname = dataAccessObject.executeQuery(BooknameSql, librarytran.getString("tranBookId"));
                String bookName = "";
                if (bookname.next()) { // 移动到结果集的第一行
                    bookName = bookname.getString("bookName");
                    // 在这里可以使用 bookIsbn 进行后续操作
                } else {
                    // 处理查询不到结果的情况
                    System.out.println("No ISBN found for the given bookId: " + librarytran.getString("tranBookId"));
                }

                transObject.put("bookName", bookName);
                userBorrowed.add(libraryTran);
                transArray.add(transObject);


            }







            // 构建返回的 JSON 对象

            JSONObject jsonUser = new JSONObject();
            jsonUser.put("userId", libraryUser.getUserId());
            jsonUser.put("userName", libraryUser.getUserName());
            jsonUser.put("userMaxBorrowNum", libraryUser.getUserMaxBorrowNum());
            jsonUser.put("userBorrowedNum", libraryUser.getUserBorrowedNum());


            jsonUser.put("userBorrowed", transArray);

            response.put("status", "success");
            response.put("user", jsonUser);

            librarytran.close();
            libraryuser.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        }

        return response;
    }

    public JSONObject library_getBooks(JSONObject user) {
        JSONObject response = new JSONObject();
        response.put("status", "failed");
        JSONArray booksArray = new JSONArray();

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 查询所有 book 表的记录
            String bookQuery = "SELECT * FROM librarybook";
            ResultSet bookResultSet = dataAccessObject.executeQuery(bookQuery);

            // 遍历所有书籍
            while (bookResultSet.next()) {
                // 创建 book 对象
                LibraryBook bookObj = new LibraryBook();

                // 设置书籍的基本信息
                bookObj.setBookId((bookResultSet.getString("bookId")));
                bookObj.setBookName(bookResultSet.getString("bookName"));
                bookObj.setBookStatus(bookResultSet.getString("bookStatus"));
                bookObj.setBookIsbn(bookResultSet.getString("bookIsbn"));
                bookObj.setBookAuthor(bookResultSet.getString("bookAuthor"));
                bookObj.setBookPress(bookResultSet.getString("bookPress"));
                bookObj.setBookDescription(bookResultSet.getString("bookDescription"));
                bookObj.setBookPlace(bookResultSet.getString("bookPlace"));
                bookObj.setBookNum(bookResultSet.getInt("bookNum"));



                // 查询 bookcomment 表并设置书评列表
                LinkedList<LibraryCom> bookComments = new LinkedList<>();
                String commentQuery = "SELECT * FROM librarycom WHERE comBookIsbn = ?";
                ResultSet commentResultSet = dataAccessObject.executeQuery(commentQuery, bookObj.getBookId());

                while (commentResultSet.next()) {
                    // 创建并添加 LibraryCom 对象到书评列表
                    LibraryCom comment = new LibraryCom();
                    comment.setComId(commentResultSet.getString("comId"));
                    comment.setComTime(commentResultSet.getDate("comTime"));
                    comment.setComUserId(commentResultSet.getInt("comUserId"));
                    comment.setComUserName(commentResultSet.getString("comUserName"));
                    comment.setComText(commentResultSet.getString("comText"));
                    comment.setComBookIsbn(commentResultSet.getString("comBookIsbn"));
                    bookComments.add(comment);
                }

                bookObj.setBookComment(bookComments);
                commentResultSet.close(); // 关闭 commentResultSet

                // 查询 booktran 表并处理相关数据 (根据需求添加逻辑)

                // 将 book 对象转换为 JSONObject 并添加到 booksArray
                JSONObject bookObject = new JSONObject();
                bookObject.put("bookId", bookObj.getBookId());
                bookObject.put("bookName", bookObj.getBookName());
                bookObject.put("bookIsbn", bookObj.getBookIsbn());
                bookObject.put("bookAuthor", bookObj.getBookAuthor());
                bookObject.put("bookPress", bookObj.getBookPress());
                bookObject.put("bookDescription", bookObj.getBookDescription());
                bookObject.put("bookPlace", bookObj.getBookPlace());
                bookObject.put("bookStatus", bookObj.getBookStatus());

                // 将书评列表转换为 JSONArray
                JSONArray commentsArray = new JSONArray();
                for (LibraryCom comment : bookComments) {
                    JSONObject commentObject = new JSONObject();
                    commentObject.put("comId", comment.getComId().toString());
                    commentObject.put("comTime", comment.getComTime().toString());
                    commentObject.put("comUserId", comment.getComUserId());
                    commentObject.put("comUserName", comment.getComUserName());
                    commentObject.put("comText", comment.getComText());
                    commentObject.put("comBookIsbn", comment.getComBookIsbn().toString());
                    commentsArray.add(commentObject);
                }

                bookObject.put("bookCommand", commentsArray);

                booksArray.add(bookObject);
            }

            bookResultSet.close(); // 关闭 bookResultSet

            response.put("status", "success");
            response.put("books", booksArray);

        } catch (SQLException e) {
            // 处理异常的代码，例如打印异常信息
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        }

        return response; // 返回包含所有书籍信息的对象
    }

    public JSONObject library_returnBooks(JSONObject librarybook) {
        JSONObject response = new JSONObject();
        response.put("status", "failed");
        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            String userId = librarybook.getString("cardNumber");
            String bookId = librarybook.getString("bookId");

            String checkBorrowedSql = "SELECT * FROM librarytran WHERE tranBookId = ? AND tranUserId = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(checkBorrowedSql, bookId,userId);
            if (!resultSet.next()) {
                response.put("message", "No record found for this book borrow.");
                return response; // 用户没有借过此书
            }

            String updateUser = "UPDATE libraryuser SET userBorrowedNum = userBorrowedNum-1 WHERE userId = ?";
            int userUpdated = dataAccessObject.executeUpdate(updateUser, userId);

            String deleteSql = "DELETE FROM librarytran WHERE tranBookId = ? AND tranUserId = ?";
            int rowsDeleted = dataAccessObject.executeDelete(deleteSql, bookId,userId);

//            String updatebookNum = "UPDATE librarybook SET bookNum = bookNum+1 WHERE bookId = ?";
//            int userUpdatebookNum = dataAccessObject.executeUpdate(updatebookNum, librarybook.getString("bookId"));

            String updatebook = "UPDATE librarybook SET bookStatus = ? WHERE bookId = ?";
            int bookUpdated = dataAccessObject.executeUpdate(updatebook, "on", bookId);

            response.put("status","success");

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        }

        return response;
    }

    public JSONObject library_borrowBooks(JSONObject librarybook) {
        JSONObject response = new JSONObject();
        response.put("status", "failed");
        response.put("book","");
        try {
            String bookId = librarybook.getString("bookId");
            String userId = librarybook.getString("cardNumber");
            DataAccessObject dataAccessObject = new DataAccessObject();

            String queryUserSql = "SELECT * FROM libraryuser WHERE userId = ?";
            ResultSet userSet = dataAccessObject.executeQuery(queryUserSql, userId);
            if(userSet.next()) {
                int userBorrowedNum=userSet.getInt("userBorrowedNum");
                int userMaxBorrowNum=userSet.getInt("userMaxBorrowNum");
                if(userBorrowedNum>=userMaxBorrowNum) {
                    response.put("error", "The user is not available to borrow books.");
                    return response;
                }
            }

            String queryBookSql = "SELECT bookStatus FROM librarybook WHERE bookId = ?";
            ResultSet resultSet = dataAccessObject.executeQuery(queryBookSql, bookId);
            if (!resultSet.next() || !("on".equals(resultSet.getString("bookStatus")))) {
                response.put("error","The book is not available for borrowing.");
                return response; // 书籍不可借阅
            }


//            String updatebookNum = "UPDATE librarybook SET bookNum = bookNum-1 WHERE bookId = ?";
//            int userUpdatebookNum = dataAccessObject.executeUpdate(updatebookNum, librarybook.getString("bookId"));
//
//            String queryBookNum = "SELECT bookNum FROM librarybook WHERE bookId = ?";
//            ResultSet resultSetAfterUpdate = dataAccessObject.executeQuery(queryBookNum, bookId);
//            if (resultSetAfterUpdate.next() && resultSetAfterUpdate.getInt("bookNum") == 0) {
//                // 如果数量为0，更新状态为 off
//                String updateBookStatusSql = "UPDATE librarybook SET bookStatus = 'off' WHERE bookId = ?";
//                dataAccessObject.executeUpdate(updateBookStatusSql, bookId);
//            }
            //1.获取书籍的Isbn
//            String queryBookSql = "SELECT bookStatus FROM librarybook WHERE bookId = ?";
//            ResultSet resultSet = dataAccessObject.executeQuery(queryBookSql, bookId);
            String BookSIsbnSql = "SELECT bookIsbn FROM librarybook WHERE bookId = ?";
            ResultSet  Isbn = dataAccessObject.executeQuery(BookSIsbnSql, bookId);
            String bookIsbn = "";
            if (Isbn.next()) { // 移动到结果集的第一行
                bookIsbn = Isbn.getString("bookIsbn");
                // 在这里可以使用 bookIsbn 进行后续操作
            } else {
                // 处理查询不到结果的情况
                System.out.println("No ISBN found for the given bookId: " + bookId);
            }

            // 2. 更新书籍状态为不可借阅
            String updateBookStatusSql = "UPDATE librarybook SET bookStatus = 'off' WHERE bookId = ?";
            dataAccessObject.executeUpdate(updateBookStatusSql, bookId);

            // 3. 更新用户的借书计数UPDATE
            String updateUserSql = "UPDATE libraryuser SET userBorrowedNum = userBorrowedNum + 1 WHERE userId = ?";
            dataAccessObject.executeUpdate(updateUserSql, userId);

            // 4. 在 tran 表中新增一条借书记录
            String insertTranSql = "INSERT INTO librarytran (tranId, tranBorrowTime,tranDueTime,tranBookId,tranIsbn,tranUserId) VALUES (?, ?, ?, ?, ?, ?)";
            String tranId =UUID.randomUUID().toString(); // 生成唯一的交易ID



            Date borrowDate = new Date(System.currentTimeMillis());

            JSONObject tranBook = new JSONObject();
            tranBook.put("tranId", tranId);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(borrowDate);

            // 增加三个月
            calendar.add(Calendar.MONTH, 3);

            // 获取更新后的日期
            Date updatedDate = calendar.getTime();


            String formattedDate = dateFormat.format(borrowDate);
            String DueTime = dateFormat.format(updatedDate);
            tranBook.put("tranBorrowTime", formattedDate);
            tranBook.put("tranDueTime",DueTime);
            tranBook.put("tranUserId", userId);
            tranBook.put("tranBookId", bookId);
            tranBook.put("tranIsbn", bookIsbn);


            dataAccessObject.executeInsert(insertTranSql, tranId, borrowDate,DueTime,bookId,bookIsbn,userId);



            String BookNameSql = "SELECT bookName FROM librarybook WHERE bookId = ?";
            ResultSet  bookname = dataAccessObject.executeQuery(BookNameSql, bookId);
            String bookName = "";
            if (bookname.next()) { // 移动到结果集的第一行
                bookName = bookname.getString("bookName");
                // 在这里可以使用 bookIsbn 进行后续操作
            } else {
                // 处理查询不到结果的情况
                System.out.println("No bookname found for the given bookId: " +bookId);
            }

            tranBook.put("bookName", bookName);


            // 借书成功
            response.put("status", "success");
            response.put("book", tranBook);
            System.out.println("Book borrowed successfully.");
            return response;


        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        }

        return response;
    }

    public JSONObject library_renewBooks(JSONObject toJson) {
        JSONObject response = new JSONObject();
        response.put("status", "failed");
        try {
            DataAccessObject dataAccessObject = new DataAccessObject();
            String bookId = toJson.getString("bookId");
            String bookQuery = "SELECT tranDueTime FROM librarytran WHERE tranBookId = ?";
            ResultSet tran = dataAccessObject.executeQuery(bookQuery, bookId);
            String tranDueTime = null;
            if (tran.next()) {
                tranDueTime = tran.getString("tranDueTime");
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date date = dateFormat.parse(tranDueTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // 增加三个月
            calendar.add(Calendar.MONTH, 3);

            // 获取更新后的日期
            Date updatedDate = calendar.getTime();

            String formattedDate = dateFormat.format(date);
            String DueTime = dateFormat.format(updatedDate);

            String updateSql = "UPDATE librarytran SET tranDueTime = ? WHERE tranBookId = ?";
            dataAccessObject.executeUpdate(updateSql, DueTime, bookId);
            response.put("status", "success");
            return response;

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return response;



    }

    public JSONObject library_addBook(JSONObject toJson) {
        JSONObject response = new JSONObject();
        response.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            JSONObject newBook = toJson.getJSONObject("newBook");

            String bookId = UUID.randomUUID().toString();
            String bookName = newBook.getString("bookName");
            String bookIsbn = newBook.getString("bookIsbn");
            String bookAuthor = newBook.getString("bookAuthor");
            String bookPress = newBook.getString("bookPress");
            String bookDescription = newBook.getString("bookDescription");
            String bookPlace = newBook.getString("bookPlace");
            String bookPhoto = newBook.getString("bookPhoto");
//            String bookStaus = newBook.getString("bookStatus");


            String insertBookSql = "INSERT INTO librarybook (bookId, bookName, bookIsbn, bookAuthor, bookPress, bookDescription, bookPlace, bookStatus,bookPhoto) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 'on', ?)";
            dataAccessObject.executeInsert(insertBookSql, bookId, bookName, bookIsbn, bookAuthor, bookPress, bookDescription, bookPlace,bookPhoto);

            response.put("status", "success");
            response.put("bookId", bookId);
            System.out.println("Book added successfully.");
            return response;

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        }

        return response;
    }

    public JSONObject library_removeBook(JSONObject book) {
        JSONObject response = new JSONObject();
        response.put("status", "failed");


        try {
            DataAccessObject dataAccessObject = new DataAccessObject();


            String bookId = book.getString("bookId");

            String querySqL = "Select bookStatus FROM librarybook WHERE bookId = ?";


            ResultSet bookStatus = dataAccessObject.executeQuery(querySqL, bookId);

            if(bookStatus.next()){

                if(bookStatus.getString("bookStatus").equals("off")){
                    response.put("message","The bookStatus is off.");
                    return response;
                }

            }
            else{
                return response;
            }
            String deleteSql = "DELETE FROM librarybook WHERE bookId = ?";
            int rowsDeleted = dataAccessObject.executeDelete(deleteSql,bookId);

            if (rowsDeleted > 0) {
                response.put("status", "success");
                System.out.println("Book removed successfully.");
            } else {
                response.put("message", "No book found with the given bookId.");
            }
            return response;

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        }

        return response;
    }

    public JSONObject library_updateBook(JSONObject toJson) {
        JSONObject response = new JSONObject();
        response.put("status", "failed");

        try {
            // 获取输入数据
            JSONObject book = toJson.getJSONObject("book");
            String bookIsbn = book.getString("bookIsbn");

            // 从 JSON 对象中获取所有的字段信息
            String bookName = book.getString("bookName");
            String bookAuthor = book.getString("bookAuthor");
            String bookPress = book.getString("bookPress");
            String bookDescription = book.getString("bookDescription");
            String bookPlace = book.getString("bookPlace");
            String bookPhoto = book.getString("bookPhoto");

            // 创建数据库连接
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 构建 SQL 更新语句
            String updateSql = "UPDATE librarybook SET bookName = ?, bookAuthor = ?, bookPress = ?, bookDescription = ?, bookPlace = ?,  bookPhoto = ? WHERE bookIsbn = ?";

            // 执行更新操作
            int rowsAffected = dataAccessObject.executeUpdate(updateSql,
                    bookName, bookAuthor, bookPress, bookDescription, bookPlace,  bookPhoto, bookIsbn);

            if (rowsAffected > 0) {
                response.put("status", "success");
            } else {
                response.put("status", "failed");
                response.put("message", "Failed to update book.");
            }

            // 关闭数据库连接
            dataAccessObject.disconnect();

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        }

        return response;
    }

    public JSONObject library_searchBooks(JSONObject searchbook) {
        JSONObject response = new JSONObject();
        JSONArray booksArray = new JSONArray();
        String serachModel= searchbook.getString("searchModel");
        String searchText = searchbook.getString("searchText");
        response.put("status", "failed");

        try {
            // 创建数据库连接
            DataAccessObject dataAccessObject = new DataAccessObject();
            String sql;
            String searchPattern;
            // SQL 查询语句，使用 LIKE 进行模糊搜索
            if(serachModel.equals("fuzzy")){
                sql = "SELECT * FROM librarybook WHERE bookName LIKE ? OR bookAuthor LIKE ?";
                searchPattern = "%" + searchText + "%";
            }
            else{
                sql= "SELECT * FROM librarybook WHERE bookName = ? OR bookAuthor = ?";
                searchPattern = searchText;
            }
            // 执行查询
            ResultSet resultSet = dataAccessObject.executeQuery(sql, searchPattern,searchPattern);
            // 临时存储合并结果的 Map，按 ISBN 存储
            Map<String, JSONObject> bookMap = new HashMap<>();
            // 处理查询结果
            while (resultSet.next()) {
                String bookId = resultSet.getString("bookId");
                String bookIsbn = resultSet.getString("bookIsbn");
                String bookName = resultSet.getString("bookName");
                String bookStatus = resultSet.getString("bookStatus");
                String bookPhoto= resultSet.getString("bookPhoto");
                // 如果该 ISBN 已存在于 bookMap 中
                if (bookMap.containsKey(bookIsbn)) {
                    JSONObject existingBook = bookMap.get(bookIsbn);
                    JSONArray statusArray = existingBook.getJSONArray("bookStatus");
                    // 为每个状态和 bookId 创建单独的记录
                    JSONObject statusObj = new JSONObject();
                    statusObj.put("status", bookStatus);
                    statusObj.put("bookId", bookId);  // 修改为直接存储字符串
                    statusArray.add(statusObj);
                    existingBook.put("bookStatus", statusArray);
                } else {
                    // 如果 ISBN 不存在，则创建新记录
                    JSONObject book = new JSONObject();
                    book.put("bookName", bookName);
                    book.put("bookIsbn", bookIsbn);
                    book.put("bookAuthor", resultSet.getString("bookAuthor"));
                    book.put("bookPress", resultSet.getString("bookPress"));
                    book.put("bookDescription", resultSet.getString("bookDescription"));
                    book.put("bookPlace", resultSet.getString("bookPlace"));
                    book.put("bookPhoto",resultSet.getString("bookPhoto"));
                    // 创建状态数组
                    JSONArray statusArray = new JSONArray();
                    JSONObject statusObj = new JSONObject();
                    statusObj.put("status", bookStatus);
                    statusObj.put("bookId", bookId);  // 修改为直接存储字符串
                    statusArray.add(statusObj);
                    book.put("bookStatus", statusArray);
                    bookMap.put(bookIsbn, book);
                }
            }
            // 计算每本书的 num 和 onCount，并添加评论信息
            for (JSONObject book : bookMap.values()) {
                String bookIsbn = book.getString("bookIsbn");
                JSONArray statusArray = book.getJSONArray("bookStatus");
                int num = statusArray.size(); // 总的记录数
                int onCount = 0;
                for (int i = 0; i < statusArray.size(); i++) {
                    JSONObject statusObj = statusArray.getJSONObject(i);
                    if (statusObj.getString("status").equals("on")) {
                        onCount++;
                    }
                }
                book.put("num", num);
                book.put("onCount", onCount);
                // 查询评论信息
                String commentSql = "SELECT comText, comTime, comUserId, comUserName FROM librarycom WHERE comIsbn = ?";
                ResultSet commentResultSet = dataAccessObject.executeQuery(commentSql, bookIsbn);
                JSONArray commentsArray = new JSONArray();
                while (commentResultSet.next()) {
                    JSONObject commentObj = new JSONObject();
                    commentObj.put("comText", commentResultSet.getString("comText"));
                    commentObj.put("comTime", commentResultSet.getDate("comTime").toString());
                    commentObj.put("comUserId", commentResultSet.getString("comUserId"));
                    commentObj.put("comUserName",commentResultSet.getString("comUserName"));
//                    commentObj.put("comId", commentResultSet.getString("comId"));
                    commentsArray.add(commentObj);
                }
                book.put("comments", commentsArray);
                booksArray.add(book);
            }
            response.put("status", "success");
            response.put("results", booksArray);
            // 关闭连接
            dataAccessObject.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        }
        return response;
    }

    public JSONObject library_addcom(JSONObject toJson) {
        JSONObject response = new JSONObject();
        response.put("status", "failed");

        try {
            // 获取输入数据
            String operation = toJson.getString("operation");
            int cardNumber = toJson.getInt("cardNumber");
            String bookIsbn = toJson.getString("bookIsbn");
            JSONObject bookCommand = toJson.getJSONObject("bookComment");

            // 从 bookComment 中提取评论信息
            String comText = bookCommand.getString("comText");
            String comTime = bookCommand.getString("comTime");
            String comUserName = bookCommand.getString("comUserName");
            String comId = UUID.randomUUID().toString();

            // 创建数据库连接
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 插入评论的 SQL 语句
            String insertSql = "INSERT INTO librarycom (comId,comTime,comUserId,comUserName,comText,comIsbn) VALUES (?, ?, ?, ?, ?, ?)";

            // 执行插入操作
            int rowsAffected = dataAccessObject.executeInsert(insertSql, comId,comTime,cardNumber,comUserName, comText,bookIsbn);

            if (rowsAffected > 0) {
                response.put("status", "success");
            } else {
                response.put("status", "failed");
                response.put("message", "Failed to add comment.");
            }

            // 关闭数据库连接
            dataAccessObject.disconnect();

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        }
        return response;
    }

    public JSONObject library_removecom(JSONObject toJson) {
        JSONObject response = new JSONObject();
        response.put("status", "failed");

        try {
            // 获取输入数据
            String comId = toJson.getString("comId");

            // 创建数据库连接
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 删除评论的 SQL 语句
            String deleteSql = "DELETE FROM librarycom WHERE comId = ?";

            // 执行删除操作
            int rowsAffected = dataAccessObject.executeUpdate(deleteSql, comId);

            if (rowsAffected > 0) {
                response.put("status", "success");
            } else {
                response.put("status", "failed");
                response.put("message", "Failed to delete comment.");
            }

            // 关闭数据库连接
            dataAccessObject.disconnect();

        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Database error occurred.");
        }

        return response;
    }

    public JSONObject library_message(JSONObject toJson) {
        JSONObject response = new JSONObject();
        JSONArray newsEventsArray = new JSONArray();
        JSONArray actionEventsArray = new JSONArray();

        try {
            // 基本 URL
            String baseUrl = "http://www.lib.seu.edu.cn";
            // 获取网页内容
            String url = baseUrl;
            Document doc = Jsoup.connect(url).get();

            // 选择所有 class 为 list-item 的 li 元素
            Elements listItems = doc.select("div.news-events ul li.list-item");

            // 遍历每个 list-item
            for (Element listItem : listItems) {
                JSONObject newsEvent = new JSONObject();

                // 获取时间
                String time = listItem.select("span.time").text();
                newsEvent.put("time", time);

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
                newsEvent.put("title", title);

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
                newsEvent.put("link", absoluteLink);

                // 添加到数组
                newsEventsArray.add(newsEvent);
            }

            // 同样处理 action-events 部分
            Elements actionItems = doc.select("div.action-events ul li.list-item");

            for (Element listItem : actionItems) {
                JSONObject actionEvent = new JSONObject();

                // 获取时间
                String time = listItem.select("span.time").text();
                actionEvent.put("time", time);

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
                actionEvent.put("title", title);

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
                actionEvent.put("link", absoluteLink);

                // 添加到数组
                actionEventsArray.add(actionEvent);
            }

            // 构建最终响应

            response.put("status", "success");
            response.put("newsEvents", newsEventsArray);
            response.put("actionEvents", actionEventsArray);


        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "An error occurred during crawling.");
        }

        // 输出最终结果
        System.out.println(response.toString());
        return response;
    }

    public JSONObject library_alltran(JSONObject user) {
        JSONObject object = new JSONObject();
        object.put("status", "failed");

        try {
            DataAccessObject dataAccessObject = new DataAccessObject();

            // 获取管理员的卡号
            int adminCardNumber = user.getInt("cardNumber");

            // 验证是否为管理员
            String adminQuery = "SELECT * FROM user WHERE cardNumber = ? AND role = 'admin'";
            ResultSet adminResultSet = dataAccessObject.executeQuery(adminQuery, adminCardNumber);

            if (!adminResultSet.isBeforeFirst()) { // 检查是否有返回结果
                object.put("status", "not_authorized");
                object.put("message", "User is not authorized as admin.");
                adminResultSet.close();
                return object;
            }

            adminResultSet.close(); // 关闭 ResultSet

            // 准备查询语句获取所有交易记录
            String query = "SELECT * FROM librarytran";
            ResultSet librarytran = dataAccessObject.executeQuery(query);

            if (!librarytran.isBeforeFirst()) { // 检查是否有交易记录
                object.put("status", "not_found");
                object.put("message", "No transactions found.");
            } else {
                JSONArray transactionsArray = new JSONArray();

                while (librarytran.next()) {
                    JSONObject transObject = new JSONObject();

                    // 提取交易信息
                    transObject.put("tranId", librarytran.getString("tranId"));
                    transObject.put("tranBorrowTime", librarytran.getTimestamp("tranBorrowTime").toString());
                    transObject.put("tranDueTime", librarytran.getTimestamp("tranDueTime").toString());
                    transObject.put("tranBookId", librarytran.getString("tranBookId"));
                    transObject.put("tranUserId", librarytran.getString("tranUserId"));
                    transObject.put("tranIsbn", librarytran.getString("tranIsbn"));

                    // 根据 bookId 查询书名
                    String bookNameQuery = "SELECT bookName FROM librarybook WHERE bookId = ?";
                    ResultSet bookResultSet = dataAccessObject.executeQuery(bookNameQuery, librarytran.getString("tranBookId"));

                    String bookName = "";
                    if (bookResultSet.next()) { // 如果查到书名
                        bookName = bookResultSet.getString("bookName");
                    } else {
                        bookName = "Unknown Book"; // 处理未找到书名的情况
                    }
                    transObject.put("bookName", bookName);
                    bookResultSet.close(); // 关闭结果集

                    // 将每条交易记录添加到 JSONArray 中
                    transactionsArray.add(transObject);
                }

                // 将交易记录添加到返回对象中
                object.put("status", "success");
                object.put("transactions", transactionsArray);

                librarytran.close(); // 关闭交易记录的 ResultSet
            }

        } catch (SQLException e) {
            // 处理 SQL 异常
            e.printStackTrace();
            object.put("status", "error");
            object.put("message", "Database error occurred.");
        }

        return object; // 返回结果对象
    }

}









