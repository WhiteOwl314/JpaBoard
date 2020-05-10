<%--
  Created by IntelliJ IDEA.
  User: seongju
  Date: 2020-05-09
  Time: 13:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"
         pageEncoding="UTF-8"
         isELIgnored="false"
%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    request.setCharacterEncoding("UTF-8");
%>
<c:set var="contextPath"  value="${pageContext.request.contextPath}"  />
<html>
<head>
    <meta charset="UTF-8">
    <title>글보기</title>
    <style>
        #tr_btn_modify{
            display:none;
        }
    </style>
    <script  src="http://code.jquery.com/jquery-latest.min.js"></script>
    <script type="text/javascript">
        function backToList(obj) {
            obj.action = "${contextPath}/board/listArticles.do";
            obj.submit();
        }
        function fn_enable(obj) {
            document.getElementById("i_title").disabled=false;
            document.getElementById("i_content").disabled=false;
            document.getElementById("i_imageFileName").disabled=false;
            document.getElementById("tr_btn_modify").style.display="block";
            document.getElementById("tr_btn").style.display="none";
        }

        function fn_modify_article(obj) {
            obj.action = "${contextPath}/board/modArticle.do";
            obj.submit();
        }

        function readRUL(input){
            if(input.files && input.files[0]){
                var reader = new FileReader();
                reader.onload = function (e) {
                    ${'#preview'}.attr('src', e.target.result);
                }
                reader.readAsDataURL((input.files[0]));
            }
        }
    </script>
</head>
<body>
    <form name="frmArticle" method="post" action="${contextPath}" enctype="multipart/form-data">
        <table border="0" align="center">
            <tr>
                <td width="150" align="center" bgcolor="#FF9933">
                    글번호
                </td>
                <td>
                    <input type="text" value="${article.articleNO}" disabled />
                    <input type="hidden" name="articleNO" value="${article.articleNO}" />
                </td>
            </tr>
            <tr>
                <td width="150" align="center" bgcolor="#FF9933">
                    작성자 아이디
                </td>
                <td>
                    <input type="text" value="${article.id}" name="writer" disabled />
                </td>
            </tr>
            <tr>
                <td width="150" align="center" bgcolor="#FF9933" >
                    제목
                </td>
                <td>
                    <input type="text" value="${article.title}" name="title" id="i_title" disabled />
                </td>
            </tr>
            <tr>
                <td width="150" align="center" bgcolor="#FF9933" >
                    내용
                </td>
                <td>
                    <textarea rows="20" cols="60" name="content" id="i_content" disabled >
                        ${article.content}
                    </textarea>
                </td>
            </tr>
            <c:if test="${not empty article.imageFileName && article.imageFileName!='null'}">
                <tr>
                    <td width="150" align="center" bgcolor="#FF9933" rowspan="2">
                        이미지
                    </td>
                    <td>
                        <input type="hidden" name="originalFileName" value="${article.imageFileName}" />
                        <img src="${contextPath}/download.do?articleNO=${article.articleNO}&imageFileName=${article.imageFileName}" id="preview" />
                        <br>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="file" name="imageFileName" id="i_imageFileName" disabled onchange="readURL(this);" />
                    </td>
                </tr>
            </c:if>
            <tr>
                <td width="150" align="center" bgcolor="#FF9933">
                    등록일자
                </td>
                <td>
                    <input type="text" value="<fmt:formatDate value="${article.writeDate}" />" disabled/>
                </td>
            </tr>
            <tr id="tr_btn_modify">
                <td conspan="2" align="center">
                    <input type="button" value="수정반영하기" onclick="fn_modify_article(frmArticle)">
                    <input type="button" value="취소" onclick="backToList(frmArticle)">
                </td>
            </tr>

            <tr id="tr_btn">
                <td colspan="2" align="center">
                    <input type="button" value="수정하기" onclick="fn_enable(this.form)">
                    <input type="button" value="삭제하기" onclick="fn_remove_article('${contextPath}/board/removeArticle.do',${article.articleNO})">
                    <input type="button" value="리스트로 돌아가기" onclick="backToList(this.form)">
                    <input type="button" value="답글쓰기" onclick="fn_reply_form('${contextPath}/board/replyForm.do',${article.articleNO})">
                </td>
            </tr>
        </table>
    </form>
</body>
</html>